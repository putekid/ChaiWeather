package com.example.chaiweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaiweather.db.City;
import com.example.chaiweather.db.County;
import com.example.chaiweather.db.Province;
import com.example.chaiweather.db.QueryCity;
import com.example.chaiweather.util.HttpUtil;
import com.example.chaiweather.util.LogUtil;
import com.example.chaiweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 选中的级别
     */
    private int currentLevel;

    /**
     * onCreateView方法为碎片创建视图（加载布局）时调用
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //通过LayoutInflater构建布局
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //从引用的布局文件构建的view对象获取控件
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        //创建数组适配器
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        return view;
    }

    /**
     * 确保与碎片相关联的活动一定已经创建完毕时调用，在这里可以给控件添加点击事件等操作
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //给ListView创建点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){//说明当前显示的是省份列表
                    //在省份列表点击应该显示对应的城市列表
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    //在城市列表点击应该显示相应的县列表
                    selectedCity = cityList.get(position);
                    queryCouties();
                }else if(currentLevel == LEVEL_COUNTY){
                    //TODO 点击县应该显示天气页面
                    String countyName = countyList.get(position).getCountyName();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("city",countyName);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        //给返回按钮添加点击事件
        backButton.setOnClickListener(v -> {
            if(currentLevel == LEVEL_COUNTY){
                queryCities();
            }else if(currentLevel == LEVEL_CITY){
                queryProvinces();
            }
        });
        //第一次加载省份列表
        queryProvinces();
        //第一次的时候要加载查询天气api需要使用的数据
//        saveQueryCities();
    }

  /*  *//**
     * 自己找的天气api使用的城市数据，要将该数据写进数据库
     *//*
    private void saveQueryCities() {
        List<QueryCity> queryCities = LitePal.findAll(QueryCity.class,1);
        if(queryCities.isEmpty()){
            try {
                String queryCitiesString = getResources().getString(R.string.query_city_data);
                JSONArray queryCitiesJSONArray = new JSONArray(queryCitiesString);
                for (int i = 0; i < queryCitiesJSONArray.length(); i++) {
                    JSONObject queryCityJSONObject = queryCitiesJSONArray.getJSONObject(i);
                    QueryCity queryCity = new QueryCity();
                    queryCity.setCountyName(queryCityJSONObject.getString("countryZh"));
                    queryCity.setProvinceName(queryCityJSONObject.getString("provinceZh"));
                    queryCity.setCityName(queryCityJSONObject.getString("leaderZh"));
                    queryCity.setCountyName(queryCityJSONObject.getString("cityZh"));
                    queryCity.setQueryId(queryCityJSONObject.getString("queryId"));
                    queryCity.save();
                }
                QueryCity find = LitePal.find(QueryCity.class,3180);
                LogUtil.d(TAG,"查询城市id为3180的：" + find.getCityName() + find.getQueryId());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查到到再到服务器上查询
     */
    private void queryProvinces(){
        //设置标题
        titleText.setText("中国");
        //设置返回按钮隐藏，因为省份列表就不能再返回了
        backButton.setVisibility(View.GONE);
        //从数据库查询省份列表
        provinceList = LitePal.findAll(Province.class);
        if(!provinceList.isEmpty()){
            //清空原来的列表数据
            dataList.clear();
            //把省份名称存入列表数据集合
            for (Province p:provinceList) {
                dataList.add(p.getProvinceName());
            }
            //通知列表数据改变
            adapter.notifyDataSetChanged();
            //将列表显示到第一行
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;//设置当前级别为省份
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询选中省份的所有城市，优先从数据库查，查不到去服务器查询
     */
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        //从数据库查询
        cityList = LitePal.where("province_id = ?",selectedProvince.getId() + "").find(City.class);
        if(!cityList.isEmpty()){
            dataList.clear();
            for (City c:cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }

    private void queryCouties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("city_id = ?",selectedCity.getId() + "").find(County.class);

        if(!countyList.isEmpty()){
            dataList.clear();
            for (County c:countyList) {
                dataList.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode();
            queryFromServer(address,"county");
        }
    }

    /**
     * 从服务器查询省市县列表
     * @param address url地址
     * @param type 查询的类型 province 省 city 城市 county 县/区
     */
    private void queryFromServer(String address, String type) {
        LogUtil.d(TAG,"请求的url：" + address);
        //显示进度条对话框
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    closeProgressDialog();
                    //提示用户加载失败
                    Toast.makeText(MyApplication.getContext(),"加载数据失败，请稍后重试",Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取相应的数据
                String responseText = response.body().string();
                LogUtil.d(TAG,"响应的数据：" + responseText);
                boolean result = false;
                if("province".equals(type))
                    result = Utility.handleProvinceResponse(responseText);
                else if("city".equals(type))
                    result = Utility.handleCityResponse(responseText,selectedProvince);
                else if("county".equals(type))
                    result = Utility.handleCountyResponse(responseText,selectedCity);

                if(result){
                    //如果处理相应数据成功，即把数据成功插入到数据库，工具类返回true，那么加载listview
                    //这里为什么要用runOnUiThread是因为在queryXxx()方法中设计到刷新lsitview等UI操作
                    getActivity().runOnUiThread(() -> {
                        closeProgressDialog();
                        if("province".equals(type))
                            queryProvinces();
                        else if("city".equals(type))
                            queryCities();
                        else if("county".equals(type))
                            queryCouties();
                    });
                }

            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(Objects.isNull(progressDialog)){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中...");//设置提示信息
            progressDialog.setCanceledOnTouchOutside(false);//设置点击外部不能取消
        }
        progressDialog.show();
    }


    /**
     * 关闭进度条对话框
     */
    private void closeProgressDialog() {
        if(Objects.nonNull(progressDialog))
            progressDialog.dismiss();
    }
}
