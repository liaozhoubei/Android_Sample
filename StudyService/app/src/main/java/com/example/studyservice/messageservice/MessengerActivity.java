package com.example.studyservice.messageservice;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studyservice.R;

/**
 * 实现了如下功能，有一个Activity，上面有三个Button和一个TextView，点击任何一个button，
 * Activity的send messenger就会向Service发送一个message，
 * 这个message的replayTo，是Activity的另外一个Messenger， 叫receive messenger。
 * service收到message以后。进行相应的处理，例如生成一个integer，float，string，
 * 把处理结果通过receive messenger发送出去，Activity就会收到这个消息。
 */
public class MessengerActivity extends AppCompatActivity {

    /*
     标记是否已经绑定Service。
     Marking whether the service has been bound.
     */
    private boolean bServiceConnected;

    /*
     这个Messenger用于向Service发送Message。
     This Messenger is used to send message to service.
     */
    private Messenger mSendMessenger;

    /*
     这个Messenger用于接收服务器发送的Message。里面通过 mHandler 接收信息
     This Messenger is used to receive message from service.
     */
    private Messenger mReceiveMessenger;


    private TextView mMessageText;

    /*
     处理从Service收到的Message。mHandler被设置于 mReceiveMessenger 中
     Handling Messages received from service.
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MessageSource.MSG_CREATE_FLOAT:
                    String strI = (String) msg.obj;
                    mMessageText.setText(strI);
                    break;
                case MessageSource.MSG_CREATE_INT:
                    String strF = (String) msg.obj;
                    mMessageText.setText(strF);
                    break;
                case MessageSource.MSG_CREATE_STRING:
                    String strS = (String) msg.obj;
                    mMessageText.setText(strS);
                    break;
                case MessageSource.MSG_CREATE_BUNDLE:
                    // 从服务器中接收返回来的 bundle
                    Bundle bundle = msg.getData();
                    String string = bundle.getString(MessageSource.MSG_KEY_BUNDLE);
                    mMessageText.setText(string);
                    break;
                default:
                    break;
            }
            return false;
        }
    }) ;

    private void setText(String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageText.setText(str);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        mMessageText = (TextView) findViewById(R.id.message_from_service);

        Button createIntBt = (Button) findViewById(R.id.let_service_create_int);
        createIntBt.setOnClickListener(onClickListener);

        Button createFloatBt = (Button) findViewById(R.id.let_service_create_float);
        createFloatBt.setOnClickListener(onClickListener);

        Button createStringBt = (Button) findViewById(R.id.let_service_create_string);
        createStringBt.setOnClickListener(onClickListener);

        Button createBundleBt = (Button) findViewById(R.id.let_service_create_bundle);
        createBundleBt.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.let_service_create_int:
                    handleButtonClick(MessageSource.MSG_CREATE_INT);
                    break;
                case R.id.let_service_create_float:
                    handleButtonClick(MessageSource.MSG_CREATE_FLOAT);
                    break;
                case R.id.let_service_create_string:
                    handleButtonClick(MessageSource.MSG_CREATE_STRING);
                    break;
                case R.id.let_service_create_bundle:
                    handleButtonClick(MessageSource.MSG_CREATE_BUNDLE);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!bServiceConnected) {
            bindService();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mServiceConnection);
    }

    private void handleButtonClick(int type) {
        if (bServiceConnected) {
            Message msg = new Message();
            msg.what = type;
            // 设置要返回信息给 mReceiveMessenger
            msg.replyTo = mReceiveMessenger;
            try {
                mSendMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Service has not been bound.", Toast.LENGTH_SHORT).show();
        }
    }



    private void bindService() {
        Intent intent = new Intent(getApplicationContext(), MessengerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Toast.makeText(MessengerActivity.this, "onServiceConnected", Toast.LENGTH_SHORT).show();
            bServiceConnected = true;
            mSendMessenger = new Messenger(iBinder);
            mReceiveMessenger = new Messenger(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(MessengerActivity.this, "onServiceDisconnected", Toast.LENGTH_SHORT).show();

            bServiceConnected = false;
            mSendMessenger = null;
            mReceiveMessenger = null;
        }
    };
}
