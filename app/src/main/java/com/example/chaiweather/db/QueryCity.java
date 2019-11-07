package com.example.chaiweather.db;

import org.litepal.crud.LitePalSupport;

/**
 * 因为老师提供的天气api的数据不准确，所以自己找的api，改api需要的城市数据
 */
public class QueryCity extends LitePalSupport {
    private int id;
    private String countryName;
    private String provinceName;
    private String cityName;
    private String countyName;
    private String queryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }
}
