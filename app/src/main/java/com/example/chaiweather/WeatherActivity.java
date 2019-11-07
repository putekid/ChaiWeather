package com.example.chaiweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WeatherActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各控件
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

        //获取缓存对象
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //这里我想的是，获取定位，然后根据定位的城市查询有没有天气信息，如果有，再判断更新时间是否超过三个小时
        //如果没有或者超过了三个小时，就去服务器获取天气信息
        //否则直接渲染天气信息
    }
}
