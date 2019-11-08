package com.example.chaiweather.gson;

public class WeatherItem {
    private String cityName;
    private String cityWeather;
    private String cityTem;

    public WeatherItem(String cityName, String cityWeather, String cityTem) {
        this.cityName = cityName;
        this.cityWeather = cityWeather;
        this.cityTem = cityTem;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityWeather() {
        return cityWeather;
    }

    public void setCityWeather(String cityWeather) {
        this.cityWeather = cityWeather;
    }

    public String getCityTem() {
        return cityTem;
    }

    public void setCityTem(String cityTem) {
        this.cityTem = cityTem;
    }
}
