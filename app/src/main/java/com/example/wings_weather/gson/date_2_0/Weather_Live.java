package com.example.wings_weather.gson.date_2_0;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Weather_Live implements Serializable {
    @SerializedName("obsTime")
    public String now_Time; //实况观测天气

    @SerializedName("temp")
    public String now_temp; //实况温度

    @SerializedName("feelsLike")
    public String feels_temp;//实况体感温度

    public String icon;//当前天气图标

    @Override
    public String toString() {
        return "Now{" +
                "now_Time='" + now_Time + '\'' +
                ", now_temp='" + now_temp + '\'' +
                ", feels_temp='" + feels_temp + '\'' +
                ", icon='" + icon + '\'' +
                ", describe_text='" + describe_text + '\'' +
                ", wind360='" + wind360 + '\'' +
                ", windDir='" + windDir + '\'' +
                ", windScale='" + windScale + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", humidity='" + humidity + '\'' +
                ", precip='" + precip + '\'' +
                ", pressure='" + pressure + '\'' +
                ", vis='" + vis + '\'' +
                ", cloud='" + cloud + '\'' +
                ", dew='" + dew + '\'' +
                '}';
    }

    @SerializedName("text")
    public String describe_text;//实况天气文字描述

    public String wind360;//实况风向360角度

    public String windDir;//实况风向

    public String windScale;//实况风力等级

    public String windSpeed;//实况风速

    public String humidity;//实况相对湿度

    public String precip;//实况降水量

    public String pressure;//实况大气压强度

    public  String vis;//实况能见度

    public String cloud;//实况云量

    public String dew;//实况露点温度

}
