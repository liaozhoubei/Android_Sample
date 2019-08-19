package com.example.example.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.example.retrofit.Interceptor.BaseUrlInterceptor;
import com.example.example.retrofit.Interceptor.HeaderInterceptor;
import com.example.example.retrofit.Interceptor.LoggingInterceptor;
import com.example.example.retrofit.converter_two.CompositeConverterFactory;
import com.example.example.retrofit.cookie.CookiesManager;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static OkHttpClient okHttpClient;
    private static RetrofitApi retrofitService;

    public static RetrofitApi getInstance(HttpUrl baseUrl, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient(context))
                .addConverterFactory(CompositeConverterFactory.create(GsonConverterFactory.create()))
                .build();
        retrofitService = retrofit.create(RetrofitApi.class);
        return retrofitService;
    }


    public static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new BaseUrlInterceptor())
                    .addInterceptor(new LoggingInterceptor())
                    .addInterceptor(new HeaderInterceptor())
                    .cookieJar(new CookiesManager(context)) // cookie 管理
                    .sslSocketFactory(createSSLSocketFactory(), new TrustAllManager())  // 测试版本，信任所有未知来源证书
                    .build();
        }
        return okHttpClient;
    }

    // ---------------------------- 以下方法仅供测试使用  -------------------------------------------

    /**
     * 默认信任所有的证书
     * TODO 最好加上证书认证，主流App都有自己的证书
     * Android Https相关完全解析 当OkHttp遇到Https
     * https://blog.csdn.net/lmj623565791/article/details/48129405
     *
     * @return
     */
    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return sSLSocketFactory;
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    // ---------------------------- 以上方法仅供测试使用  -------------------------------------------


    /**
     * 异步上传
     * 上传文件的简单封装，部分参数需要针对服务器进行修改
     *
     * @param file
     * @param callback
     */
    public void uploadFile(File file, Callback<String> callback) {
        //创建表单map,里面存储服务器本接口所需要的数据;
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //在这里添加服务器除了文件之外的其他参数
                .addFormDataPart("enctype", "multipart/form-data");
        //设置文件的格式;两个文件上传在这里添加
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        //添加文件(uploadfile就是你服务器中需要的文件参数)
        builder.addFormDataPart("uploadfile", file.getName(), body);
        //builder.addFormDataPart("uploadfile1", file1.getName(), imageBody1);
        //生成接口需要的list
        List<MultipartBody.Part> parts = builder.build().parts();
        Call<String> call = retrofitService.upLoading(parts);
        call.enqueue(callback);
    }


    /**
     * 同步 Get 请求
     *
     * @param call
     * @param <T>
     * @return
     */
    public <T> T synRequest(Call<T> call) {

        try {
            Response<T> execute = call.execute();
            T body = execute.body();
            if (body != null) {
                return body;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void test() {
        try {
            Call<NewsDataXml> newsData = retrofitService.getNewsData();
            Response<NewsDataXml> execute = newsData.execute();
            NewsDataXml body = execute.body();
            if (body != null) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
