package com.example.example.retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.example.R;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit demo
 * 发现 xml 转换器与 gson 转换器无法同时使用
 * 示例数据在 assert 中，分别为 hello.json 以及 test2.xml
 */
public class RetrofitActivity extends AppCompatActivity {
    private String TAG = "RetrofitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);


    }

    public void convertTwo(View view) {
        ConvertOne();
    }

    public void convertOne(View view) {
        ConvertSecond();
    }

    /**
     * 第一种混合转换器
     *
     * @return
     */
    private void ConvertOne() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.9.219:8080/")
                .addConverterFactory(JsonOrXmlConverterFactory.create())
                .build();
        GnakApi api = retrofit.create(GnakApi.class);
        Call<GankBean> call = api.getAndroidInfo();
        call.enqueue(new Callback<GankBean>() {
                         @Override
                         public void onResponse(Call<GankBean> call, Response<GankBean> response) {
                             List<GankBean.ResultsBean> results = response.body().getResults();
                             Log.e(TAG, "GankBean onResponse: " + results.get(0).toString());
                         }

                         @Override
                         public void onFailure(Call<GankBean> call, Throwable t) {
                             Log.e(TAG, "onFailure: ", t);
                         }
                     }
        );


        api.getNewsData().enqueue(new Callback<NewsDataXml>() {
            @Override
            public void onResponse(Call<NewsDataXml> call, Response<NewsDataXml> response) {
                List<NewsXml> list = response.body().getList();
                Log.e(TAG, "NewsDataXml onResponse: " + list.get(0).toString());
            }

            @Override
            public void onFailure(Call<NewsDataXml> call, Throwable t) {
                Log.e(TAG, "NewsDataXml onFailure: ", t);
            }
        });
    }

    /**
     * 第二种混合转换器
     */
    private void ConvertSecond() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.9.219:8080/")
                .addConverterFactory(CompositeConverterFactory.create(GsonConverterFactory.create()))
                .build();
        GnakApi api = retrofit.create(GnakApi.class);
        Call<GankBean> call = api.getAndroidInfo_Second();
        call.enqueue(new Callback<GankBean>() {
                         @Override
                         public void onResponse(Call<GankBean> call, Response<GankBean> response) {
                             List<GankBean.ResultsBean> results = response.body().getResults();
                             Log.e(TAG, "CompositeConverterFactory GankBean onResponse: " + results.get(0).toString());
                         }

                         @Override
                         public void onFailure(Call<GankBean> call, Throwable t) {
                             Log.e(TAG, "onFailure: ", t);
                         }
                     }
        );


        api.getNewsData_Second().enqueue(new Callback<NewsDataXml>() {
            @Override
            public void onResponse(Call<NewsDataXml> call, Response<NewsDataXml> response) {
                List<NewsXml> list = response.body().getList();
                Log.e(TAG, "CompositeConverterFactory NewsDataXml onResponse: " + list.get(0).toString());
            }

            @Override
            public void onFailure(Call<NewsDataXml> call, Throwable t) {
                Log.e(TAG, "CompositeConverterFactory NewsDataXml onFailure: ", t);
            }
        });
    }


}
