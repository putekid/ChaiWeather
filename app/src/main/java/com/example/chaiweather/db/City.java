package com.example.chaiweather.db;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class City extends LitePalSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private Province province;
    private List<County> counties;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public List<County> getCounties() {
        return counties;
    }

    public void setCounties(List<County> counties) {
        this.counties = counties;
    }
}
