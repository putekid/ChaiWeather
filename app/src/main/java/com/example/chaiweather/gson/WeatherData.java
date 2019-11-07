package com.example.chaiweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气缓存实体类
 */
public class WeatherData {
    private String day;//7日（今天）
    private String date;//日期
    private String week;//星期几
    @SerializedName("wea_img")
    private String weaImg;//天气对应图标(xue, lei, shachen, wu, bingbao, yun, yu, yin, qing)
    private int air;//空气质量
    private int humidity;//湿度
    @SerializedName("air_level")
    private String airLever;//空气等级
    @SerializedName("air_tips")
    private String airTips;//空气质量描述
    private String tem1;//最高气温
    private String tem2;//最低气温
    private String tem;//当前气温
    private List<String> win;//风向，第一个是早上，第二个是晚上
    @SerializedName("win_speed")
    private String winSpeed;//风速
    private List<HoursWeather> hours;
    private List<Tips> index;


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWeaImg() {
        return weaImg;
    }

    public void setWeaImg(String weaImg) {
        this.weaImg = weaImg;
    }

    public int getAir() {
        return air;
    }

    public void setAir(int air) {
        this.air = air;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getAirLever() {
        return airLever;
    }

    public void setAirLever(String airLever) {
        this.airLever = airLever;
    }

    public String getAirTips() {
        return airTips;
    }

    public void setAirTips(String airTips) {
        this.airTips = airTips;
    }

    public String getTem1() {
        return tem1;
    }

    public void setTem1(String tem1) {
        this.tem1 = tem1;
    }

    public String getTem2() {
        return tem2;
    }

    public void setTem2(String tem2) {
        this.tem2 = tem2;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public List<String> getWin() {
        return win;
    }

    public void setWin(List<String> win) {
        this.win = win;
    }

    public String getWinSpeed() {
        return winSpeed;
    }

    public void setWinSpeed(String winSpeed) {
        this.winSpeed = winSpeed;
    }

    public List<HoursWeather> getHours() {
        return hours;
    }

    public void setHours(List<HoursWeather> hours) {
        this.hours = hours;
    }

    public List<Tips> getIndex() {
        return index;
    }

    public void setIndex(List<Tips> index) {
        this.index = index;
    }

}
