package com.example.studyservice.callbackservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.example.studyservice.ICallback;
import com.example.studyservice.IService;
import com.example.studyservice.R;

/**
 * AIDL之service回调Activity
 * 演示注册服务后，由服务器调用 activity ,可以有多个 activity 调用服务
 */
public class SecondActivity extends AppCompatActivity {

    private static final String TAG = SecondActivity.class.getSimpleName();
    private IService mService;

    /**
     * service的回调方法
     */
    private ICallback.Stub mCallback = new ICallback.Stub() {

        @Override
        public void showResult(int result) {
            Log.d(TAG, " result : " + result);
            final String r = "result:"+result;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_s_view.setText(r);
                }
            });
        }
    };
    private TextView tv_s_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_callback);
        tv_s_view = (TextView) findViewById(R.id.tv_s_view);
        bindService(new Intent(this, CallbackService.class),
                new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        mService = IService.Stub.asInterface(service);
                        try {
                            mService.registerCallback(mCallback);
                            mService.start();
                        } catch (RemoteException e) {
                            Log.e(TAG, "", e);
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                }, BIND_AUTO_CREATE);
    }

    public void thirdActivity(View view){
        startActivity(new Intent(this, ThirdActivity.class));
    }

    public void getInfo(View view){

    }
}
