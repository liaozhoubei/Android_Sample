package com.example.example.retrofit;

import com.example.example.retrofit.converter_one.ResponseFormat;
import com.example.example.retrofit.converter_two.ResponseConverter;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 注解详情
 * https://www.jianshu.com/p/f23be7f8cb93
 */
public interface RetrofitApi {
    // 第一种混合转换器
    @GET("apk/test/hello.json")
    @ResponseFormat("json")
    Call<GankBean> getAndroidInfo();


    @GET("apk/test/test2.xml")
    @ResponseFormat("xml")
    Call<NewsDataXml> getNewsData();
    // 第一种混合转换器 结束

    // 第二种混合转换器
    @GET("apk/test/hello.json")
    @ResponseConverter(GsonConverterFactory.class)
    Call<GankBean> getAndroidInfo_Second();


    @GET("apk/test/test2.xml")
    @ResponseConverter(SimpleXmlConverterFactory.class)
    Call<NewsDataXml> getNewsData_Second();


    /**
     * 上传
     * Multipart 表示多表单上传,
     *
     * @param partList 表单信息
     * @return .
     */
    @Multipart
    @POST("UploadServlet")
    @ResponseConverter(ScalarsConverterFactory.class)
    Call<String> upLoading(@Part List<MultipartBody.Part> partList);

    /**
     * 下载文件
     * 如果下载大文件的一定要加上   @Streaming  注解
     *
     * @param fileUrl 文件的路径
     * @return 请求call
     */
    @Headers("url_change: abc")
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String fileUrl);

//    @Headers("url_change: http://192.168.99.8:8888")  // 设置 headers, 然后在 Interceptor 中修改 baseUrl
//    @GET("apk/test/test2.xml")
//    @ResponseConverter(SimpleXmlConverterFactory.class)
//    Call<NewsDataXml> getNewsData_Second();

}