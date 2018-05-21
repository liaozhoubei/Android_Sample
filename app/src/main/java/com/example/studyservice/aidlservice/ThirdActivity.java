package com.example.studyservice.aidlservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.studyservice.ICallback;
import com.example.studyservice.IService;
import com.example.studyservice.R;

public class ThirdActivity extends AppCompatActivity {
    private static final String TAG = ThirdActivity.class.getSimpleName();
    private IService mService;
    ServiceConnection connection = new ServiceConnection() {
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
    };

    /**
     * service的回调方法
     */
    private ICallback.Stub mCallback = new ICallback.Stub() {

        @Override
        public void showResult(final int result) {
            Log.d(TAG, " result : " + result);
            final String r = "result:"+result;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_t_view.setText(r);
                }
            });
        }
    };
    private TextView tv_t_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        tv_t_view = (TextView) findViewById(R.id.tv_t_view);
        bindService(new Intent(this, TestMyService.class),
                connection , BIND_AUTO_CREATE);
    }

    public void finishThis(View view){
        if(mService!=null){
            try {
                mService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "", e);
            }
        }
        unbindService(connection);
        finish();
    }

    public void getInfo(View view){
    }
}
