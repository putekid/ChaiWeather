package com.example.chaiweather.util;

import com.example.chaiweather.R;

public class WeatherImageUtil {
    public static int getImageIdByWeather(String weather){
        int imageId;
        if(weather.endsWith("雨"))
            imageId = R.drawable.yu;
        else if(weather.endsWith("雪"))
            imageId = R.drawable.xue;
        else if(weather.endsWith("云"))
            imageId = R.drawable.yun;

        //设置天气描述图片
        switch (weather){
            case "晴":
                imageId = R.drawable.qing;
                break;
            case "雷阵雨":
                imageId = R.drawable.zhenyu;
                break;
            case "扬沙":
                imageId = R.drawable.shachen;
                break;
            case "雾":
                imageId = R.drawable.wu;
                break;
            case "阴":
                imageId = R.drawable.yin;
                break;
            case "雨夹雪":
                imageId = R.drawable.yujiaxue;
                break;
                default:
                    imageId = R.mipmap.ic_launcher;
        }
        return imageId;
    }
}
