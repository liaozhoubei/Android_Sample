package com.example.example.retrofit.Interceptor;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 更换 baseUrl 路径
 */
public class BaseUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        //获取request
        Request request = chain.request();
        //从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
        //获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        //从request中获取headers，通过给定的键 url_name
        List<String> headerValues = request.headers("url_change");
        if (headerValues != null && headerValues.size() > 0) {
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            String newUrl = headerValues.get(0);
            builder.removeHeader("url_change");
            if (TextUtils.isEmpty(newUrl)){
                throw new RuntimeException("new Url is Empty, if header contain url_change ,the value must not be empty!");
            }
            //匹配获得新的BaseUrl
            HttpUrl newBaseUrl = HttpUrl.parse(newUrl);
            if (newBaseUrl != null) {
                //重建新的HttpUrl，修改需要修改的url部分
                HttpUrl newFullUrl = oldHttpUrl
                        .newBuilder()
                        .scheme(newBaseUrl.scheme())//更换网络协议
                        .host(newBaseUrl.host())//更换主机名
                        .port(newBaseUrl.port())//更换端口
                        .build();
                //重建这个request，通过builder.url(newFullUrl).build()；
                // 然后返回一个response至此结束修改
                Log.e("Url", "intercept: " + newFullUrl.toString());
                return chain.proceed(builder.url(newFullUrl).build());
            }
        }
        return chain.proceed(request);
    }
}
