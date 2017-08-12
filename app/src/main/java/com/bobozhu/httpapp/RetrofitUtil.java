package com.bobozhu.httpapp;

import com.bobozhu.httpapp.logInterceptor.Level;
import com.bobozhu.httpapp.logInterceptor.LoggingInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/8/12.
 */

public class RetrofitUtil {

    public static Retrofit getRetrofit(String url) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .addInterceptor(new LoggingInterceptor.Builder()
                        .loggable(true)
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .build());
        return new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
