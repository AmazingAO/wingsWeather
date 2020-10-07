package com.example.wings_weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wings_weather.gson.date_2_0.Now;
import com.example.wings_weather.gson.date_2_0.Weather_Live;
import com.example.wings_weather.gson.date_2_0.Weather_day3;

import java.lang.reflect.Field;

public class Weather_day3Activity extends AppCompatActivity {
    private Button back;

    private ScrollView dayLyout;

    private TextView day_location;

    private TextView fxDate;

    private TextView tempMin;

    private TextView tempMax;

    private ImageView bing_pic_img;

    private ImageView icon_Day;

    private ImageView icon_Night;

    private TextView textDay;

    private TextView textNight;


    private TextView humidity;

    private TextView precip;

    private TextView pressure;

    private TextView vis;

    private TextView cloud;

    private TextView uvIndex;



    private TextView windDirDay;

    private TextView windScaleDay;

    private TextView windSpeedDay;

    private TextView windDirNight;

    private TextView windScaleNight;

    private TextView windSpeedNight;


    private TextView sunrise;

    private TextView sunset;

    private TextView moonrise;

    private TextView moonset;

    private TextView moonPhase;

    private String city_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_day);

        setContentView(R.layout.activity_day3);
        Intent intent = getIntent();
        Now  forecast = (Now) intent.getSerializableExtra("forecast");
        city_name = intent.getStringExtra("city_name");

        dayLyout=findViewById(R.id.day_layout);
        back = findViewById(R.id.title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        day_location = findViewById(R.id.day_location);
        fxDate = findViewById(R.id.fxDate);
        tempMin = findViewById(R.id.tempMin);
        tempMax = findViewById(R.id.tempMax);
        icon_Day = findViewById(R.id.iconDay);
        icon_Night = findViewById(R.id.iconNight);
        textDay = findViewById(R.id.textDay);
        textNight = findViewById(R.id.textNight);
        humidity = findViewById(R.id.humidity);
        precip = findViewById(R.id.precip);
        pressure = findViewById(R.id.pressure);
        vis = findViewById(R.id.vis);
        cloud = findViewById(R.id.cloud);
        uvIndex = findViewById(R.id.uvIndex);
        windDirDay = findViewById(R.id.windDirDay);
        windScaleDay = findViewById(R.id.windScaleDay);
        windSpeedDay = findViewById(R.id.windSpeedDay);
        windDirNight = findViewById(R.id.windDirNight);
        windScaleNight = findViewById(R.id.windScaleNight);
        windSpeedNight = findViewById(R.id.windSpeedNight);
        sunrise  = findViewById(R.id.sunset);
        sunset = findViewById(R.id.sunset);
        moonrise = findViewById(R.id.moonrise);
        moonset = findViewById(R.id.moonset);
        moonPhase = findViewById(R.id.moonPhase);
        bing_pic_img = findViewById(R.id.bing_pic_img);

        Glide.with(this).load(R.drawable.back_10).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(bing_pic_img);


        if (forecast == null){
            Toast.makeText(Weather_day3Activity.this,"信息加载失败",Toast.LENGTH_SHORT).show();
        }else {
            dayLyout.setVisibility(View.INVISIBLE);
            showWeather_day3_Live_Info(forecast,city_name);
        }
    }


    /**
     * 展示信息到页面
     * @param weather_live
     * @param city_name
     */
    private void showWeather_day3_Live_Info(Now weather_live , String city_name){
        day_location.setText(city_name);
        fxDate.setText(weather_live.fxDate);
        tempMin.setText(weather_live.tempMin+"℃");
        tempMax.setText(weather_live.tempMax+"℃");

//        R.drawable.temp_100;
        String iconDay = "temp_"+weather_live.iconDay;
        int day_i = R.drawable.temp_302;
        try{
            Field field=R.drawable.class.getField(iconDay);
            day_i= field.getInt(new R.drawable());
        }catch(Exception e){
            e.printStackTrace();
        }
        String iconNight = "temp_"+weather_live.iconNight;
        int night_i = R.drawable.temp_302;
        try{
            Field field=R.drawable.class.getField(iconNight);
            night_i= field.getInt(new R.drawable());
        }catch(Exception e){
            e.printStackTrace();
        }
//        Glide.with(this).load(day_i).into(icon_Day);
//        Glide.with()
        icon_Day.setImageResource(day_i);
        icon_Night.setImageResource(night_i);
        textDay.setText(weather_live.textDay);
        textNight.setText(weather_live.textNight);


//        Glide.with(this).load(i).into(icon_temp);

        humidity.setText(weather_live.humidity+"%");
        precip.setText(weather_live.precip+"ml");
        pressure.setText(weather_live.pressure+"Pa");
        vis.setText(weather_live.vis+"Km");
        cloud.setText(weather_live.cloud+"%");
        uvIndex.setText(weather_live.uvIndex);

        windDirDay.setText(weather_live.windDirDay);
        windScaleDay.setText(weather_live.windScaleDay+"级");
        windSpeedDay.setText(weather_live.windSpeedDay+"Km/h");
        windDirNight.setText(weather_live.windDirNight);
        windScaleNight.setText(weather_live.windScaleNight+"级");
        windSpeedNight.setText(weather_live.windSpeedNight+"Km/h");


         sunrise.setText("清晨"+weather_live.sunrise);

         sunset.setText("傍晚"+weather_live.sunset);

         moonrise.setText("夜晚"+weather_live.moonrise);

         moonset.setText("上午"+weather_live.moonset+"+1");

         moonPhase.setText(weather_live.moonPhase);


        dayLyout.setVisibility(View.VISIBLE);//展示内容
    }
}
