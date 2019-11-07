package com.example.chaiweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.chaiweather.db.City;
import com.example.chaiweather.db.County;
import com.example.chaiweather.db.Province;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {
    private static final String TAG = "Utility";

    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject proviceJSONObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(proviceJSONObject.getString("name"));
                    province.setProvinceCode(proviceJSONObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handleCityResponse(String response,Province province){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityJSONObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityJSONObject.getString("name"));
                    city.setCityCode(cityJSONObject.getInt("id"));
                    city.setProvince(province);
                    city.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean handleCountyResponse(String response, City city){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyJSONObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyJSONObject.getString("name"));
                    county.setWeatherId(countyJSONObject.getString("weather_id"));
                    county.setCity(city);
                    county.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
