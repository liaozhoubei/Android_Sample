package com.example.example.retrofit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.example.R;
import com.example.example.retrofit.Interceptor.LoggingInterceptor;
import com.example.example.retrofit.converter_one.JsonOrXmlConverterFactory;
import com.example.example.retrofit.converter_two.CompositeConverterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

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


    public void ConverterOne(View view) {
        ConvertOne();
    }

    public void ConverterTwo(View view) {
        ConvertSecond();
    }

    public void Download(View view) {
        downLoadMethod();
    }

    public void Upload(View view) {
        upLoadingMethod();
    }


    /**
     * 第一种混合转换器
     * @return
     */
    private void ConvertOne() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.9.219:8080/")
                .addConverterFactory(JsonOrXmlConverterFactory.create())
                .build();
        RetrofitApi api = retrofit.create(RetrofitApi.class);
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
                Log.e(TAG, "NewsDataXml onResponse: " + list.get(0).toString() );
            }

            @Override
            public void onFailure(Call<NewsDataXml> call, Throwable t) {
                Log.e(TAG, "NewsDataXml onFailure: ",t );
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
        RetrofitApi api = retrofit.create(RetrofitApi.class);
        Call<GankBean> call = api.getAndroidInfo_Second();
        call.enqueue(new Callback<GankBean>() {
                         @Override
                         public void onResponse(Call<GankBean> call, Response<GankBean> response) {
                             List<GankBean.ResultsBean> results = response.body().getResults();
                             Log.e(TAG, "CompositeConverterFactory GankBean onResponse: " + results.get(0).toString() );
                         }

                         @Override
                         public void onFailure(Call<GankBean> call, Throwable t) {
                             Log.e(TAG, "onFailure: ",t );
                         }
                     }
        );


        api.getNewsData_Second().enqueue(new Callback<NewsDataXml>() {
            @Override
            public void onResponse(Call<NewsDataXml> call, Response<NewsDataXml> response) {
                List<NewsXml> list = response.body().getList();
                Log.e(TAG, "CompositeConverterFactory NewsDataXml onResponse: " + list.get(0).toString() );
            }

            @Override
            public void onFailure(Call<NewsDataXml> call, Throwable t) {
                Log.e(TAG, "CompositeConverterFactory NewsDataXml onFailure: ",t );
            }
        });
    }

    /**
     * 响应上传点击事件的方法
     */
    private void upLoadingMethod() {

        //创建文件(你需要上传到服务器的文件)
        //file1Location文件的路径 ,我是在手机存储根目录下创建了一个文件夹,里面放着了一张图片;
        String file1Location = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "MyDownload" +
                File.separator +"download.jpg";
        File file = new File(file1Location);

        //创建表单map,里面存储服务器本接口所需要的数据;
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //在这里添加服务器除了文件之外的其他参数
                .addFormDataPart("enctype", "multipart/form-data");


        //设置文件的格式;两个文件上传在这里添加
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // RequestBody imageBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
        //添加文件(uploadfile就是你服务器中需要的文件参数)
        builder.addFormDataPart("uploadfile", file.getName(), imageBody);
        //builder.addFormDataPart("uploadfile1", file1.getName(), imageBody1);
        //生成接口需要的list
        List<MultipartBody.Part> parts = builder.build().parts();
        //创建设置OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                //允许失败重试
                .retryOnConnectionFailure(true)
                .build();
        //创建retrofit实例对象
        Retrofit retrofit = new Retrofit.Builder()
                //设置基站地址(基站地址+描述网络请求的接口上面注释的Post地址,就是要上传文件到服务器的地址,
                // 这只是一种设置地址的方法,还有其他方式,不在赘述)
                .baseUrl("http://192.168.99.218:8080/UploadTest/")
                //设置委托,使用OKHttp联网,也可以设置其他的;
                .client(okHttpClient)
                //设置数据解析器,如果没有这个类需要添加依赖:
                .addConverterFactory(ScalarsConverterFactory.create())
                //设置支持rxJava
                // .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        //实例化请求接口,把表单传递过去;
        Call<String> call = retrofit.create(RetrofitApi.class).upLoading(parts);
        //开始请求
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //联网有响应或有返回数据
                if (response.isSuccessful()){
                    try {
                        System.out.println(response.body().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //连接失败,多数是网络不可用导致的
                System.out.println("网络不可用");
                t.printStackTrace();
            }
        });

    }


    /**
     * 响应下载点击事件的方法
     * https://www.jianshu.com/p/1043b8998ac3
     */
    private void downLoadMethod() {

        //创建设置OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                //允许失败重试
                .retryOnConnectionFailure(true)
                .build();
        // http://img.ph.126.net/T3xfDm6NpNk_MZfNrsDjYA==/3071736420860264427.jpg
        //创建retrofit实例对象
        Retrofit retrofit = new Retrofit.Builder()
                //设置基站地址(基站地址+描述网络请求的接口上面注释的Post地址,就是要上传文件到服务器的地址,
                // 这只是一种设置地址的方法,还有其他方式,不在赘述)
                .baseUrl("http://img.ph.126.net/T3xfDm6NpNk_MZfNrsDjYA==/")
                //设置委托,使用OKHttp联网,也可以设置其他的;
                .client(okHttpClient)
                //设置数据解析器,如果没有这个类需要添加依赖:
                .addConverterFactory(ScalarsConverterFactory.create())
                //设置支持rxJava
                // .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        //我在百度上找到一张图片,把他的地址拆分了一下,最后一个斜杠之前的url设置为了baseUrl,斜杠之后设置在这里;
        Call<ResponseBody> download = retrofit.create(RetrofitApi.class).download("3071736420860264427.jpg");
        download.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful()) {
                    boolean toDisk = writeResponseBodyToDisk(response.body());
                    if (toDisk) {
                        System.out.println("下载成功请查看");
                    } else {

                        System.out.println("下载失败,请稍后重试");
                    }
                } else {
                    System.out.println("服务器返回错误");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //连接失败,多数是网络不可用导致的
                System.out.println("网络不可用");
            }
        });
    }

    /**
     * 下载到本地
     *
     * @param body 内容
     * @return 成功或者失败
     */
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        String SD_HOME_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MyDownload/";
        try {
            //判断文件夹是否存在
            File files = new File(SD_HOME_DIR);
            if (!files.exists()) {
                //不存在就创建出来
                files.mkdirs();
            }
            //创建一个文件
            File futureStudioIconFile = new File(SD_HOME_DIR + "download.jpg");
            //初始化输入流
            InputStream inputStream = null;
            //初始化输出流
            OutputStream outputStream = null;

            try {
                //设置每次读写的字节
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                //请求返回的字节流
                inputStream = body.byteStream();
                //创建输出流，再此次可通过 fileSize 与现在写入磁盘的大小获取进度
                outputStream = new FileOutputStream(futureStudioIconFile);
                //进行读取操作
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    //进行写入操作
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                }
                //刷新
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    //关闭输入流
                    inputStream.close();
                }

                if (outputStream != null) {
                    //关闭输出流
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
