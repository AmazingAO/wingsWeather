package com.example.wings_weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wings_weather.gson.Forecast;
import com.example.wings_weather.gson.Weather;
import com.example.wings_weather.gson.date_2_0.Now;
import com.example.wings_weather.gson.date_2_0.Weather_Live;
import com.example.wings_weather.gson.date_2_0.Weather_day3;
import com.example.wings_weather.util.HttpUtil;
import com.example.wings_weather.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {


    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private  TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg; //背景图片

    public SwipeRefreshLayout swipeRefreshLayout;

    private String mWeatherId;

    public DrawerLayout drawerLayout ;

    private Button navButton;

    private String city_name;



    private int tag = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        //初始化各种控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout); // 总显示框
        titleCity = (TextView) findViewById(R.id.title_city); // 城市
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time); // 更新时间
        degreeText = (TextView) findViewById(R.id.degree_text); // 温度
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text); // 天气情况
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout); //未来几天天气预报
        aqiText = (TextView) findViewById(R.id.aqi_text); //AQI指数
        pm25Text = (TextView)findViewById(R.id.pm25_text); //PM2.5指数
        comfortText = (TextView)findViewById(R.id.comfort_text); // 舒适度
        carWashText = (TextView) findViewById(R.id.car_wash_text); // 洗车建议
        sportText = (TextView)findViewById(R.id.sport_text); //运动建议


        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);//获取SharedPreferences对象

        String bingPic = pref.getString("bing_pic",null); //先去SharedPreferences寻找图片是否缓存在本地，没有则请求后台发送。
        if (bingPic!=null){
         Glide.with(this).load(bingPic).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(bingPicImg);
        }else {
            loadBingPic();
        }


        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });



        String weatherString = pref.getString("weather",null);
        String weatherString_Live = pref.getString("weather_Live",null);
        String weatherString_3_Live = pref.getString("weather_Live_day3",null);

        if (weatherString != null && weatherString_Live!=null&&weatherString_3_Live!=null){//将每次请求的JSON 数据缓存在本地 SharedPreferences 文件中
            Weather weather = Utility.handleWeatherResponse(weatherString);
            Weather_Live weather_Live = Utility.handleWeather_LiveResponse(weatherString_Live);
            Weather_day3 weather_day3 = Utility.handleWeatherDay3_LiveResponse(weatherString_3_Live);
            mWeatherId = weather.basic.weatherId;
            city_name = weather.basic.cityName;
            showWeatherInfo(weather);
            showWeather_Live_Info(weather_Live);
            showWeatherDay3_Live_Info(weather_day3);
        }else {
            Intent intent = getIntent();
            city_name = intent.getStringExtra("city_name");
            mWeatherId = intent.getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);//请求数据时先将其内容展示隐藏.
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                loadBingPic();
            }
        });


        degreeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(WeatherActivity.this,Weather_dayActivity.class);
                intent.putExtra("weather_id",mWeatherId);
                intent.putExtra("city_name",city_name);
                startActivity(intent);
            }
        });
    }

    /**
     * 请求未来3天实况信息
     */
    public void  requestWeatherDay3_Live(final String weatherId){
        final String weather_live_Url = "https://devapi.heweather.net/v7/weather/3d?location="+weatherId+"&key=97ab95ea5401408e8a43d3dfec8db005";
        HttpUtil.sendOkHttpRequest(weather_live_Url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"on day3  failure 获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新.
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string(); //获取请求体
                final Weather_day3 weather_day3 = Utility.handleWeatherDay3_LiveResponse(responseText);//使用GSON 将JSON解析为对应的数据类
//                Log.d("werror",weather_day3.daily.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather_day3!= null){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //获取SharedPreferences的Editor类将请求回来的数据缓存在本地.

                            editor.putString("weather_Live_day3",responseText);
                            editor.apply();
                            showWeatherDay3_Live_Info(weather_day3);
                        }else {
                            Toast.makeText(WeatherActivity.this,"else 获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新
                    }
                });
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
                        Toast.makeText(WeatherActivity.this,"on failure 获取天气信息失败",Toast.LENGTH_SHORT).show();
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
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //获取SharedPreferences的Editor类将请求回来的数据缓存在本地.
                            editor.putString("weather_Live",responseText);
                            editor.apply();
                            requestWeatherDay3_Live(weatherId);
                            showWeather_Live_Info(weather_live);
                        }else {
//                            Log.d("error",weather.code);
                            Log.d("error",weather_live == null ? "yes":"no");
                            Toast.makeText(WeatherActivity.this,"else 获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新
                    }
                });
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        final String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&KEY=ab56595ea238463b807452e13aac7394";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("error","onFailure");
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新.
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string(); //获取请求体
                final Weather weather = Utility.handleWeatherResponse(responseText);//使用GSON 将JSON解析为对应的数据类
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            //获取SharedPreferences的Editor类将请求回来的数据缓存在本地.
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeatherId  = weather.basic.weatherId;
                            city_name = weather.basic.cityName;
                            requestWeather_Live(weatherId);
                            showWeatherInfo(weather);
                        }else {
                            Log.d("error",weather == null ? "yes":"no");
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
//                        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
//        String degree = weather.now.temperature+"℃";
//        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
//        degreeText.setText(degree);
//        weatherInfoText.setText(weatherInfo);
//        forecastLayout.removeAllViews();
        if (weather == null)
            Log.d("weather_error",null);

        if (weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度:" + weather.suggestion.comfort.info;
        String carWash = "洗车指数:" + weather.suggestion.carWash.info;
        String sport = "运动建议:" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
    }

    /**
     * 显示2.0版本数据
     */

    private void showWeather_Live_Info(Weather_Live weather){
        String degree = weather.now_temp+"℃";
        String weatherInfo =weather.describe_text;
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
    }

    /**
     * 显示3未来三天数据
     */
    private void showWeatherDay3_Live_Info(Weather_day3 weather){
        forecastLayout.removeAllViews();
        int size = 1;
        for (final Now forecast : weather.daily){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);

            dateText.setText(forecast.fxDate);
            infoText.setText(forecast.textDay);
            maxText.setText(forecast.tempMax);
            minText.setText(forecast.tempMin);

            final int finalSize = size;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WeatherActivity.this,Weather_day3Activity.class);
                    intent.putExtra("forecast",forecast);
                    intent.putExtra("city_name",city_name);
                    startActivity(intent);
                }
            });
            forecastLayout.addView(view);
            size++;
        }
        weatherLayout.setVisibility(View.VISIBLE);//展示内容
        swipeRefreshLayout.setRefreshing(false);//关闭下拉刷新.
        Intent intent = new Intent(this,AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 加载背景图片 从网上获取
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}
