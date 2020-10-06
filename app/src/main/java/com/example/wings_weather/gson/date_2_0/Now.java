package com.example.wings_weather.gson.date_2_0;

import java.io.Serializable;

public class Now  implements Serializable {
    public String fxDate;
    public String sunrise;
    public String sunset;
    public String moonrise;
    public String moonset;
    public String tempMax;
    public String tempmin;
    public String iconDay;
    public String textDay;
    public String iconNight;
    public String textNight;
    public String wind360Day;
    public String windDirDay;
    public String windScaleDay;
    public String windSpeedDay;
    public String wind360Night;
    public String windDirNight;
    public String windScaleNight;
    public String windSpeedNight;
    public String humidity;
    public String precip;
    public String pressure;
    public String vis;
    public String uvIndex;

    @Override
    public String toString() {
        return "Weather_day3{" +
                "fxDate='" + fxDate + '\'' +
                ", sunrise='" + sunrise + '\'' +
                ", sunset='" + sunset + '\'' +
                ", moonrise='" + moonrise + '\'' +
                ", moonset='" + moonset + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", tempmin='" + tempmin + '\'' +
                ", iconDay='" + iconDay + '\'' +
                ", textDay='" + textDay + '\'' +
                ", iconNight='" + iconNight + '\'' +
                ", textNight='" + textNight + '\'' +
                ", wind360Day='" + wind360Day + '\'' +
                ", windDirDay='" + windDirDay + '\'' +
                ", windScaleDay='" + windScaleDay + '\'' +
                ", windSpeedDay='" + windSpeedDay + '\'' +
                ", wind360Night='" + wind360Night + '\'' +
                ", windDirNight='" + windDirNight + '\'' +
                ", windScaleNight='" + windScaleNight + '\'' +
                ", windSpeedNight='" + windSpeedNight + '\'' +
                ", humidity='" + humidity + '\'' +
                ", precip='" + precip + '\'' +
                ", pressure='" + pressure + '\'' +
                ", vis='" + vis + '\'' +
                ", uvIndex='" + uvIndex + '\'' +
                '}';
    }

}
