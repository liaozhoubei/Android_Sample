package com.example.taskdispatcher.example;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import androidx.multidex.MultiDex;

import com.example.taskdispatcher.TaskDispatcher;
import com.example.taskdispatcher.example.bean.NewsItem;
import com.example.taskdispatcher.example.tasks.GetDeviceIdTask;
import com.example.taskdispatcher.example.tasks.InitAMapTask;
import com.example.taskdispatcher.example.tasks.InitBuglyTask;
import com.example.taskdispatcher.example.tasks.InitFrescoTask;
import com.example.taskdispatcher.example.tasks.InitJPushTask;
import com.example.taskdispatcher.example.tasks.InitStethoTask;
import com.example.taskdispatcher.example.tasks.InitUmengTask;
import com.example.taskdispatcher.example.tasks.InitWeexTask;

public class MyApplication extends Application {

    private String mDeviceId;
    private static Application mApplication;
    private boolean DEV_MODE = true;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        TaskDispatcher.init(this);
        TaskDispatcher dispatcher = TaskDispatcher.createInstance();
        dispatcher
                .addTask(new InitAMapTask())
                .addTask(new InitStethoTask())
                .addTask(new InitWeexTask())
                .addTask(new InitBuglyTask())
                .addTask(new InitFrescoTask())
                .addTask(new InitJPushTask())
                .addTask(new InitUmengTask())
                .addTask(new GetDeviceIdTask())
                .start();

        dispatcher.await();
        initStrictMode();
    }

    private void initStrictMode() {
        if (DEV_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls() //API等级11，使用StrictMode.noteSlowCode
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()// or .detectAll() for all detectable problems
                    .penaltyLog() //在Logcat 中打印违规异常信息
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .setClassInstanceLimit(NewsItem.class, 1)
                    .detectLeakedClosableObjects() //API等级11
                    .penaltyLog()
                    .build());
        }
    }

    public void setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
    }

    public String getmDeviceId() {
        return mDeviceId;
    }

    public static Application getApplication() {
        return mApplication;
    }

}
