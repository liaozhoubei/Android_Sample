package com.example.example.retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.example.R;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Retrofit demo
 * 发现 xml 转换器与 gson 转换器无法同时使用
 * 示例数据在 assert 中，分别为 hello.json 以及 test2.xml
 */
public class RetrofitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.9.219:8080/")
//                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GnakApi api = retrofit.create(GnakApi.class);
        Call<GankBean> call = api.getAndroidInfo();
        call.enqueue(new Callback<GankBean>() {
                         @Override
                         public void onResponse(Call<GankBean> call, Response<GankBean> response) {
                             List<GankBean.ResultsBean> results = response.body().getResults();
                             Log.e("Heool", "onResponse: " + results.get(0).toString() );
                         }

                         @Override
                         public void onFailure(Call<GankBean> call, Throwable t) {
                             Log.e("Heool", "onFailure: ",t );
                         }
                     }
           );



//        api.getNewsData().enqueue(new Callback<NewsDataXml>() {
//            @Override
//            public void onResponse(Call<NewsDataXml> call, Response<NewsDataXml> response) {
//                List<NewsXml> list = response.body().getList();
//                Log.e("Heool", "NewsDataXml onResponse: " + list.get(0).toString() );
//            }
//
//            @Override
//            public void onFailure(Call<NewsDataXml> call, Throwable t) {
//                Log.e("Heool", "NewsDataXml onFailure: ",t );
//            }
//        });
    }
}
