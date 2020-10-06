package com.example.wings_weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wings_weather.gson.date_2_0.Weather_Live;
import com.example.wings_weather.util.HttpUtil;
import com.example.wings_weather.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Weather_dayActivity extends AppCompatActivity {


    private String mWeatherId;

    private String city_name;


    private ScrollView dayLyout;


    private Button title_back;

    private ImageView bingPicImg;

    private TextView day_location;

    private TextView day_temp;

    private TextView day_text;

    private TextView day_time;

    private ImageView icon_temp;



    private TextView feel_temp;

    private TextView wind_360;

    private TextView humidity;

    private TextView precip;

    private TextView pressure;

    private TextView vis;

    private TextView cloud;

    private TextView dew;



    private TextView windDir;

    private TextView windScale;

    private TextView windSpeed;

    private SwipeRefreshLayout swipeRefreshLayout;


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

        Intent intent = getIntent();


        bingPicImg = findViewById(R.id.bing_pic_img);
        title_back = findViewById(R.id.title_back);
        swipeRefreshLayout=findViewById(R.id.day_swipe);
        dayLyout = findViewById(R.id.day_layout) ;
        day_location = findViewById(R.id.day_location);
        day_temp = findViewById(R.id.day_temp);
        day_text = findViewById(R.id.day_text);
        day_time = findViewById(R.id.day_time);
        icon_temp = findViewById(R.id.icon_temp);


        feel_temp = findViewById(R.id.feel_temp);
        wind_360 = findViewById(R.id.wind_360);
        humidity = findViewById(R.id.humidity);
        precip = findViewById(R.id.precip);
        pressure = findViewById(R.id.pressure);
        vis = findViewById(R.id.vis);
        cloud = findViewById(R.id.cloud);
        dew = findViewById(R.id.dew);

        windDir = findViewById(R.id.windDir);
        windScale = findViewById(R.id.windScale);
        windSpeed = findViewById(R.id.windSpeed);


        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Glide.with(this).load(R.drawable.back_100).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(bingPicImg);

        mWeatherId = intent.getStringExtra("weather_id");

        city_name = intent.getStringExtra("city_name");
        if (city_name!=null)
        Log.d("day_error",city_name);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String weather_String = sharedPreferences.getString("weather_Live",null);

        if (weather_String!=null){
            Weather_Live weather_live1 = Utility.handleWeather_LiveResponse(weather_String);

                Log.d("day_error",weather_live1.toString());
            showWeather_day_Live_Info(weather_live1,city_name);

        }else {
            dayLyout.setVisibility(View.INVISIBLE);
            requestWeather_Live(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather_Live(mWeatherId);
            }
        });

    }

    /**
     * 请求和天气的实况信息
     */
    public void  requestWeather_Live(final String weatherId){
        final String weather_live_Url = "https://devapi.heweather.net/v7/weather/now?location="+weatherId+"&key=97ab95ea5401408e8a43d3dfec8db005";
        HttpUtil.sendOkHttpRequest(weather_live_Url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("error","onFailure");
                        Toast.makeText(Weather_dayActivity.this,"on failure 获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新.
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string(); //获取请求体
                final Weather_Live weather_live = Utility.handleWeather_LiveResponse(responseText);//使用GSON 将JSON解析为对应的数据类
                Log.d("error",weather_live.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather_live!= null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Weather_dayActivity.this).edit();
                            //获取SharedPreferences的Editor类将请求回来的数据缓存在本地.
                            editor.putString("weather_Live",responseText);
                            editor.apply();
                            showWeather_day_Live_Info(weather_live,city_name);
                        }else {
//                            Log.d("error",weather.code);
                            Log.d("error",weather_live == null ? "yes":"no");
                            Toast.makeText(Weather_dayActivity.this,"else 获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新
                    }
                });
            }
        });
    }


    /**
     * 展示信息到页面
     * @param weather_live
     * @param city_name
     */
    private void showWeather_day_Live_Info(Weather_Live weather_live ,String city_name){
        day_location.setText(city_name);
        day_temp.setText(weather_live.now_temp+"℃");
        day_text.setText(weather_live.describe_text);

        String time = weather_live.now_Time.split("T")[0];
        day_time.setText(time);
//        R.drawable.temp_100;
        String icon = "temp_"+weather_live.icon;
        int i = R.drawable.temp_302;
        try{
            Field field=R.drawable.class.getField(icon);
            i= field.getInt(new R.drawable());
        }catch(Exception e){
            e.printStackTrace();
        }
//        Glide.with(this).load(i).into(icon_temp);
        icon_temp.setImageResource(i);

        feel_temp.setText(weather_live.feels_temp+"℃");
        wind_360.setText(weather_live.wind360+"°");
        humidity.setText(weather_live.humidity+"%");
        precip.setText(weather_live.precip+"ml");
        pressure.setText(weather_live.pressure+"Pa");
        vis.setText(weather_live.vis+"Km");
        cloud.setText(weather_live.cloud+"%");
        dew.setText(weather_live.dew+"℃");

        windDir.setText(weather_live.windDir);
        windScale.setText(weather_live.windScale+"级");
        windSpeed.setText(weather_live.windSpeed+"Km/h");

        dayLyout.setVisibility(View.VISIBLE);//展示内容
        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
