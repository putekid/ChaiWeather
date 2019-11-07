package com.example.chaiweather.gson;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class HoursWeather {
    private String day;
    private String wea;
    private String tem;
    private String win;
    @SerializedName("win_speed")
    private String winSpeed;



    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWea() {
        return wea;
    }

    public void setWea(String wea) {
        this.wea = wea;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getWinSpeed() {
        return winSpeed;
    }

    public void setWinSpeed(String winSpeed) {
        this.winSpeed = winSpeed;
    }
}
