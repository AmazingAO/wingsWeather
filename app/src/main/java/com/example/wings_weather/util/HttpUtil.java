package com.example.wings_weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil { //来进行数据的发送，使用okHttp来进行数据发送。
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);//开启了线程去查询。
    }
}
