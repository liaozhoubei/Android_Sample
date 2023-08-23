package com.example.example.retrofit.Interceptor;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommonParamsInterceptor  implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        String oldUrl = request.url().toString();
//        Log.e("---拦截器",request.url()+"---"+request.method()+"--"+request.header("User-agent"));
        Map<String,String> map = new HashMap<>();
//        map.put("ak","12345678");
        if ("GET".equals(method)){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(oldUrl);
            if (oldUrl.contains("?")){
                if (oldUrl.indexOf("?") == oldUrl.length()-1){
                }else {
                    stringBuilder.append("&");
                }
            }else {
                stringBuilder.append("?");
            }
            for (Map.Entry<String,String> entry: map.entrySet()) {
                stringBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            if (stringBuilder.indexOf("&") != -1){
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
            }
            String newUrl = stringBuilder.toString();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }else if ("POST".equals(method)){
            RequestBody oldRequestBody = request.body();
            if (oldRequestBody instanceof FormBody){
                FormBody oldBody = (FormBody) oldRequestBody;
                FormBody.Builder builder = new FormBody.Builder();
                for (int i=0;i<oldBody.size();i++){
                    builder.add(oldBody.name(i),oldBody.value(i));
                }
                for (Map.Entry<String,String> entry:map.entrySet()) {
                    builder.add(entry.getKey(),entry.getValue());
                }
                FormBody newBody = builder.build();
                request = request.newBuilder()
                        .url(oldUrl)
                        .post(newBody)
                        .build();
            }
        }
        Response response = chain.proceed(request);
        return response;

    }
}
