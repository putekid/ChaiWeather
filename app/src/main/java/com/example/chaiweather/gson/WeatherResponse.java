package com.example.chaiweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("cityid")
    private String queryCityId;
    @SerializedName("update_time")
    private String updateTime;
    private List<WeatherData> data;

    public String getQueryCityId() {
        return queryCityId;
    }

    public void setQueryCityId(String queryCityId) {
        this.queryCityId = queryCityId;
    }



    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }


    public List<WeatherData> getData() {
        return data;
    }

    public void setData(List<WeatherData> data) {
        this.data = data;
    }
}
