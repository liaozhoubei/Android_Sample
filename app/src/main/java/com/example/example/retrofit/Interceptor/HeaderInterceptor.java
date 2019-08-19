package com.example.example.retrofit.Interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 统一添加请求头
 */
public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")

//                .addHeader("Accept-Encoding", "gzip, deflate")

                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
//                .addHeader("Cookie", "add cookies here")
//                .addHeader("sessionid", "2018022619571443")


                .build();

        return chain.proceed(request);

    }
}
