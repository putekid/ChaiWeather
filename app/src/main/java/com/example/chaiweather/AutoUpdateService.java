package com.example.chaiweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.baidu.location.LocationClient;
import com.example.chaiweather.gson.WeatherResponse;
import com.example.chaiweather.util.HttpUtil;
import com.example.chaiweather.util.WeatherParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    private LocationClient locationClient;
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        //写入定时任务，每两个小时执行一次更新
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int twoHours = 2 * 60 * 60 * 1000;//两个小时
        long triggerAtTime = SystemClock.elapsedRealtime() + twoHours;
        Intent intent1 = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,intent1,0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateBingPic() {
        String requestUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }

    private void updateWeather() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = sharedPreferences.getString("current_city",null);
        if(Objects.nonNull(cityName)){
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://www.tianqiapi.com/api");
            try {
                urlBuilder.append("?appid=").append(WeatherParam.APPID)
                        .append("&appsecret=").append(WeatherParam.APPSECRET)
                        .append("&version=").append("v1")
                        .append("&city=").append(URLEncoder.encode(cityName,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpUtil.sendOkHttpRequest(urlBuilder.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putString(cityName,responseText);
                    editor.apply();
                }
            });
        }
    }
}
