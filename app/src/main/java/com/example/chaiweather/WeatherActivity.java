package com.example.chaiweather;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.example.chaiweather.gson.Tips;
import com.example.chaiweather.gson.WeatherData;
import com.example.chaiweather.gson.WeatherResponse;
import com.example.chaiweather.util.HttpUtil;
import com.example.chaiweather.util.LogUtil;
import com.example.chaiweather.util.TimeUtil;
import com.example.chaiweather.util.Utility;
import com.example.chaiweather.util.WeatherParam;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity {
    private static final String TAG = "WeatherActivity";

    private DrawerLayout drawerLayout;//滑动菜单
    private Button navButton;//点击按钮显示滑动菜单
    private ProgressBar loadWeatherProgressBar;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView airLevelText;
    private TextView airDescText;
    private TextView ultravioletRayText;//紫外线指数
    private TextView bodyText;//健康指数
    private TextView wearText;//穿衣指数
    private TextView carWashText;//洗车指数
    private TextView sportText;//运动指数
    private ImageView bingPicImg;
    private TextView temRangeText;
    private ImageView weatherImg;
    private SwipeRefreshLayout swipeRefreshLayout;//天气信息下拉刷新组件

    private LocationClient mLocationClient;
    private String locationCity;//当前定位城市

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化定位
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_weather);


        //如果当前版本大于21即安卓5以上，那么设置状态栏透明
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        //初始化各控件
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        airLevelText = findViewById(R.id.air_level_text);
        airDescText = findViewById(R.id.air_desc_text);
        ultravioletRayText = findViewById(R.id.ultraviolet_ray_text);
        bodyText = findViewById(R.id.body_text);
        wearText = findViewById(R.id.wear_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        loadWeatherProgressBar = findViewById(R.id.load_weather_progress_bar);
        bingPicImg = findViewById(R.id.bing_pic_img);
        temRangeText = findViewById(R.id.tem_range_text);
        weatherImg = findViewById(R.id.weather_img);
        swipeRefreshLayout = findViewById(R.id.swiper_refresh);



        //获取缓存对象
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //这里我想的是，获取定位，然后根据定位的城市查询有没有天气信息，如果有，再判断更新时间是否超过三个小时
        //如果没有或者超过了三个小时，就去服务器获取天气信息
        //否则直接渲染天气信息

        //如果是从其他通过Intent跳转过来的
        String intentCity = getIntent().getStringExtra("city");
        if(Objects.nonNull(intentCity)){
            //如果不是null，说明是跳转过来的，设置为跳转传递的城市
            locationCity = intentCity;
            //跳转过来之后就不再重新获取定位，直接获取天气并显示
            updateWeatherInfo();
        }else {
            //如果是空的，说明不是跳转过来的，那么去申请权限并获取天气
            requestPermissionsToUseLocation();

        }
        if(Objects.isNull(locationCity)) locationCity = "北京";

        //渲染必应每日一图
        //从缓存获取必应每日一图url
        String binPicUrl = sharedPreferences.getString("bing_pic",null);
        if(Objects.nonNull(binPicUrl)){
            //使用Glide加载图片
            Glide.with(this).load(binPicUrl).into(bingPicImg);
        }else {
            loadingBingPic();
        }

        //下拉刷新事件
        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                requestWeather(locationCity);
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(this,"刷新失败",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        //点击navButton显示滑动菜单
        navButton.setOnClickListener((v) -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

    }

    /**
     * 从服务器获取必应每日一图
     */
    private void loadingBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MyApplication.getContext(),"获取每日一图失败",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultUrl = response.body().string();
                //保存图片url
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("bing_pic",resultUrl);
                editor.apply();
                runOnUiThread(() -> {
                    //加载图片
                    Glide.with(WeatherActivity.this).load(resultUrl).into(bingPicImg);
                });
            }
        });
    }

    /**
     * 展示天气信息
     * @param weatherResponse
     */
    private void showWeatherInfo(WeatherResponse weatherResponse) {
        String cityName = locationCity;
        String updateTime = weatherResponse.getUpdateTime().split(" ")[1];
        WeatherData todayWeather = weatherResponse.getData().get(0);
        String degree = todayWeather.getTem();
        String weatherInfo = todayWeather.getWea();
        String temRange = todayWeather.getTem2() + "-" + todayWeather.getTem1();

        if(todayWeather.getWea().endsWith("雨"))
            weatherImg.setImageResource(R.drawable.yu);
        else if(todayWeather.getWea().endsWith("雪"))
            weatherImg.setImageResource(R.drawable.xue);
        else if(todayWeather.getWea().endsWith("云"))
            weatherImg.setImageResource(R.drawable.yun);

        //设置天气描述图片
        switch (todayWeather.getWea()){
            case "晴":
                weatherImg.setImageResource(R.drawable.qing);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.zhenyu);
                break;
            case "扬沙":
                weatherImg.setImageResource(R.drawable.shachen);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.wu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.yujiaxue);
                break;
        }

        //设置内容
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        temRangeText.setText(temRange);

        //未来几天天气情况
        forecastLayout.removeAllViews();//先移除
        List<WeatherData> weatherDataList = weatherResponse.getData();
        weatherDataList.remove(0);
        for (WeatherData data:weatherDataList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(data.getDate());
            infoText.setText(data.getWea());
            maxText.setText(data.getTem1());
            minText.setText(data.getTem2());
            forecastLayout.addView(view);
        }

        //空气等级
        airLevelText.setText(todayWeather.getAirLever());
        airDescText.setText(todayWeather.getAirTips());

        //建议
        /*private TextView ultravioletRayText;//紫外线指数
        private TextView bodyText;//健康指数
        private TextView wearText;//穿衣指数
        private TextView carWashText;//洗车指数
        private TextView sportText;//运动指数*/
        List<Tips> tips = todayWeather.getIndex();
        for (Tips t:tips) {
            String content = t.getTitle() + ":" + t.getLevel() + "," + t.getDesc();
            switch (t.getTitle()){
                case "紫外线指数":
                    ultravioletRayText.setText(content);
                    break;
                case "健臻·血糖指数":
                    bodyText.setText(content);
                    break;
                case"穿衣指数":
                    wearText.setText(content);
                    break;
                case "洗车指数" :
                    carWashText.setText(content);
                    break;
                case "空气污染扩散指数" :
                    sportText.setText(content);
                    break;
                    default:
                        break;
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);//显示天气信息
        loadWeatherProgressBar.setVisibility(View.GONE);
    }

    /**
     * 从服务器获取天气信息
     * @param locationCity
     */
    private void requestWeather(String locationCity) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://www.tianqiapi.com/api");
        urlBuilder.append("?appid=").append(WeatherParam.APPID)
                .append("&appsecret=").append(WeatherParam.APPSECRET)
                .append("&version=").append("v1")
                .append("&city=").append(URLEncoder.encode(locationCity,"UTF-8"));

        HttpUtil.sendOkHttpRequest(urlBuilder.toString(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //不用runOnUiThread就报错
                //java.lang.RuntimeException: Can't toast on a thread that has not called Looper.prepare()
                runOnUiThread(() -> {
                    Toast.makeText(MyApplication.getContext(),"获取天气信息失败",Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取响应文本
                String responseText = response.body().string();
                //转换为天气对象
                WeatherResponse weatherResponse = Utility.handleWeatherResponse(responseText);
                //设置到UI操作，要用新线程
                runOnUiThread(() -> {
                    //缓存天气信息
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(WeatherActivity.this)
                            .edit();
                    editor.putString(locationCity,responseText);
                    editor.apply();
                    showWeatherInfo(weatherResponse);
                    swipeRefreshLayout.setRefreshing(false);//关闭刷新显示
                });

            }
        });

        //加载每日一图
        loadingBingPic();
    }

    /**
     * 申请权限
     */
    private void requestPermissionsToUseLocation() {
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions = new String[permissionList.size()];
            for (int i = 0; i < permissionList.size(); i++) {
                permissions[i] =  permissionList.get(i);
            }
            ActivityCompat.requestPermissions(this,permissions,1);

        }else {
            requestLocation();
        }
    }

    /**
     * 获取地理位置
     */
    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    /**
     * 初始化定位工作
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for (int result:grantResults) {
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"您需要同意权限申请，才能正常使用应用",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"我发生了错误，发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }

        }
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String district = bdLocation.getDistrict();//获取当前县/区，保存为要查询的城市
            LogUtil.d(TAG, "获取的经度：" + bdLocation.getLongitude());
            LogUtil.d(TAG,"获取的维度：" + bdLocation.getLatitude());
            if(Objects.nonNull(district)){
                if(district.endsWith("市")){
                    district = district.substring(0,district.lastIndexOf("市"));
                }
                if(district.endsWith("县")){
                    district = district.substring(0,district.lastIndexOf("市"));
                }
                if(district.endsWith("区")){
                    district = district.substring(0,district.lastIndexOf("区"));
                }
                locationCity = district;
            }


            //获取到定位之后更新天气信息
            updateWeatherInfo();
        }
    }

    /**
     * 之前是在onCreate方法中获取天气信息，然后显示到页面中，后来经过调试，发现回调方法是异步的，可能会比较慢，在获取到定位之前，就已经渲染了页面，导致一直显示北京的信息，所以我们要在获取定位之后再进行渲染
     */
    private void updateWeatherInfo() {

        //获取当前定位的城市的缓存
        String currentLocationWeather = sharedPreferences.getString(locationCity,null);
        if(Objects.isNull(currentLocationWeather)){
            //如果没有缓存，那么重新获取天气
            //先让ScrollView隐藏，因为现在还没有数据，空数据的界面看上去不好看
            weatherLayout.setVisibility(View.GONE);
            try {
                requestWeather(locationCity);
            }catch (Exception e){
                Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }else {
            //缓存不为null，判断是否更新时间超过3小时
            WeatherResponse weatherResponse = Utility.handleWeatherResponse(currentLocationWeather);
            String updateTime = weatherResponse.getUpdateTime();
            long mills = TimeUtil.durationDate1ToDate2(TimeUtil.parseStringToDate(updateTime),new Date());
            if(TimeUtil.isLargeThan3Hour(mills)){
                //大于3小时，重新获取天气信息
                weatherLayout.setVisibility(View.GONE);
                try {
                    requestWeather(locationCity);
                }catch (Exception e){
                    Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }else {
                showWeatherInfo(Utility.handleWeatherResponse(currentLocationWeather));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.isFinishing()){
            mLocationClient.stop();
        }
    }
}
