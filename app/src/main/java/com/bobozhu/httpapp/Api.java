package com.bobozhu.httpapp;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/8/12.
 */

public interface Api {

    @GET
    Observable<ResponseBody> get(@Url String url, @HeaderMap Map<String, String> map, @QueryMap Map<String, String> paramsMap);

    @FormUrlEncoded
    @POST
    Observable<ResponseBody> post(@Url String url,@HeaderMap Map<String, String> map, @FieldMap Map<String, String> paramsMap);


//    @GET
//    Flowable<ResponseBody> get1(@Url String url, @HeaderMap Map<String, String> map, @QueryMap Map<String, String> paramsMap);

}
