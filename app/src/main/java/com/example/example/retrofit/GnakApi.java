package com.example.example.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GnakApi {

    @GET("apk/test/hello.json")
    Call<GankBean> getAndroidInfo();


    @GET("apk/test/test2.xml")
    Call<NewsDataXml> getNewsData();

}