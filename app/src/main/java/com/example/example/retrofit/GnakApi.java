package com.example.example.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;

public interface GnakApi {

    @GET("apk/test/hello.json")
    @ResponseFormat("json")
    Call<GankBean> getAndroidInfo();


    @GET("apk/test/test2.xml")
    @ResponseFormat("xml")
    Call<NewsDataXml> getNewsData();


    @GET("apk/test/hello.json")
    @ResponseConverter(GsonConverterFactory.class)
    Call<GankBean> getAndroidInfo_Second();


    @GET("apk/test/test2.xml")
    @ResponseConverter(SimpleXmlConverterFactory.class)
    Call<NewsDataXml> getNewsData_Second();

}