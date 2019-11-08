package com.example.chaiweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.chaiweather.gson.WeatherItem;
import com.example.chaiweather.gson.WeatherResponse;
import com.example.chaiweather.util.HttpUtil;
import com.example.chaiweather.util.Utility;
import com.example.chaiweather.util.WeatherParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CityManagerActivity extends BaseActivity {
    private List<WeatherItem> weatherItemList = new ArrayList<>();
    private WeatherItemAdapter adapter;
    private RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    private ProgressBar cityManagerProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        cityManagerProgressBar = findViewById(R.id.city_manager_progress_bar);
        //获取缓存存储对象
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        recyclerView = findViewById(R.id.city_recycler_view);

        //通过Intent获取城市名称
        String transCityName = getIntent().getStringExtra("cityName");
        if(Objects.nonNull(transCityName)){
            //如果不是null，说明是跳转来的，那么把这个城市存到缓存
            String citiesString = sharedPreferences.getString("cityManager",null);
            String saveCitiesString;
            if(Objects.isNull(citiesString)){
                //之前没存过，存一个新的
                saveCitiesString = transCityName + ",";
            }else {
                saveCitiesString = citiesString + transCityName + ",";
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cityManager",saveCitiesString);
            editor.apply();
        }

        adapter = new WeatherItemAdapter(weatherItemList);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        try {
            initWeatherItems();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void initWeatherItems() throws UnsupportedEncodingException {
        //由于获取天气比较耗时，所以在新线程进行
        new Thread(() -> {

        }).start();
        //从缓存获取城市列表
        String managerCitys = sharedPreferences.getString("cityManager",null);
        //分割城市列表字符串，然后遍历，每一个城市获取天气信息
        if(Objects.nonNull(managerCitys)){
            String[] cities = managerCitys.split(",");
            List<String> failCities = new ArrayList<>();
            for (String city:cities) {
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append("https://www.tianqiapi.com/api");
                urlBuilder.append("?appid=").append(WeatherParam.APPID)
                        .append("&appsecret=").append(WeatherParam.APPSECRET)
                        .append("&version=").append("v1")
                        .append("&city=").append(URLEncoder.encode(city,"UTF-8"));

                HttpUtil.sendOkHttpRequest(urlBuilder.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        failCities.add(city);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText = response.body().string();
                        WeatherResponse weatherResponse = Utility.handleWeatherResponse(responseText);
                        //创建WeatherItem对象
                        WeatherItem weatherItem = new WeatherItem(city,weatherResponse.getData().get(0).getWea(),weatherResponse.getData().get(0).getTem());
                        weatherItemList.add(weatherItem);
                    }
                });
            }

            //修改ui
            if(!failCities.isEmpty()){
                runOnUiThread(() -> {
                    StringBuilder failCityString = new StringBuilder();
                    failCities.forEach(s -> failCityString.append(s).append(","));
                    failCityString.subSequence(0,failCityString.length() - 1);
                    Toast.makeText(CityManagerActivity.this,failCityString.toString() + "获取天气信息失败",Toast.LENGTH_LONG).show();
                });
            }
            runOnUiThread(() -> {
                //通知改变
                adapter.notifyDataSetChanged();
                cityManagerProgressBar.setVisibility(View.VISIBLE);

            });
        }
        //通过天气信息创建WeatherItem对象，添加到集合中


    }

    public static void actionStart(Context context,String cityName){
        Intent intent = new Intent(context,CityManagerActivity.class);
        if(Objects.nonNull(cityName))
        intent.putExtra("cityName",cityName);
        context.startActivity(intent);
    }
}
