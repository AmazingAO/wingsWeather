package com.example.wings_weather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")//GSON 将其类与json返回字段相互映射起来
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;

        @Override
        public String toString() {
            return "Update{" +
                    "updateTime='" + updateTime + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Basic{" +
                "cityName='" + cityName + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", update=" + update +
                '}';
    }
}
