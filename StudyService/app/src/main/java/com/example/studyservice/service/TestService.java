package com.example.studyservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 演示同一进程的服务
 */
public class TestService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("我在onbind");
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        System.out.println("我在oncreate");
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("我在onstartConnand");
        return super.onStartCommand(intent, flags, startId);
    }


    public void eat() {
        Toast.makeText(getApplicationContext(), "toast开始吃东西了", Toast.LENGTH_SHORT).show();
    }
    private void play() {
        Toast.makeText(getApplicationContext(), "toast一起玩", Toast.LENGTH_SHORT).show();

    }


    private  class MyBinder extends Binder implements Iservice{
        // 重写Iservice中的calleat()方法
        public void calleat(){
            eat();
        }
        // 调用TestService中的play()方法
        public void callPlay(){
            play();
        }
    }


}
