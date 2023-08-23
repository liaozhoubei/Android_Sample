package com.example.studyservice.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.example.studyservice.ICallback;
import com.example.studyservice.IService;

public class TestMyService extends Service {
    private static final String TAG = TestMyService.class.getSimpleName();
    // 提供远程调用的回调
    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();

    private IService.Stub mBinder = new IService.Stub() {

        @Override
        public void unregisterCallback(ICallback cb){
            if(cb != null) {
                // 取消注册
                mCallbacks.unregister(cb);
            }
        }

        @Override
        public void start() throws RemoteException {

        }

        @Override
        public void registerCallback(ICallback cb){
            if(cb != null) {
                // 注册回调对象
                mCallbacks.register(cb);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        doingBack();
        super.onCreate();
    }



    @Override
    public void onDestroy() {
        mCallbacks.kill();
        super.onDestroy();
    }

    private void callBack(int j) {
        int N = mCallbacks.beginBroadcast();
        try {
            for (int i = 0; i < N; i++) {
                // 给每个回调对象发送通知
                mCallbacks.getBroadcastItem(i).showResult(j);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
        // 结束发送通知
        mCallbacks.finishBroadcast();
    }

    private int index = 0;
    private void doingBack(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 一些耗时操作，操作完后调用activity方法
                SystemClock.sleep(3000);
                while (true){
                    callBack(index);
                    index++;
                    SystemClock.sleep(1000);
                }

            }
        }).start();
    }
}
