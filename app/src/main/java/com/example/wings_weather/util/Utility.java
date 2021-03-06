package com.example.wings_weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.wings_weather.db.City;
import com.example.wings_weather.db.County;
import com.example.wings_weather.db.Province;
import com.example.wings_weather.gson.Weather;
import com.example.wings_weather.gson.date_2_0.Weather_Live;
import com.example.wings_weather.gson.date_2_0.Weather_day3;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String resopnse){
        if (!TextUtils.isEmpty(resopnse)){
            try{
                JSONArray allProvinces = new JSONArray(resopnse);
                for (int i =0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save(); //将数据保存在数据库。
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response);
                for (int i = 0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理和保存县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.d("werror",weatherContent);
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析实况信息的JSON数据解析成Weather_Live类
     */
    public static Weather_Live handleWeather_LiveResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            String code  = jsonObject.getString("code");
            if (!code.equals("200")){
                return  null;
            }
            String weather_now  = jsonObject.getString("now");
            return new Gson().fromJson(weather_now,Weather_Live.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析未来3天实况信息将JSON 转为 Weather_day3类
     */
    public static Weather_day3 handleWeatherDay3_LiveResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            String code  = jsonObject.getString("code");
            if (!code.equals("200")){
                return  null;
            }
            Weather_day3 weather_day3 = new Gson().fromJson(response,Weather_day3.class);
            return  weather_day3;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
