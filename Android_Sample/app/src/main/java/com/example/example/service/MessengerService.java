package com.example.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.example.example.bean.MessageSource;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessengerService extends Service {

    /*
     这个Handler负责接收Activity的Message，收到一个Message时，通过获取Message的replayTo得到一个Messenger实例，
     使用这个Messenger向Activity发送Message。
     This Handler is in charge of receiving Messages sending from Activity. When it receiving a
     Message, get The replayTo which is a Messenger instance from this Message. Using this Messenger
     to send Message to Activity.
     */
    private Handler mActMsgHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MessageSource.MSG_CREATE_INT:
                    createAndSendInt(msg.replyTo);
                    break;
                case MessageSource.MSG_CREATE_FLOAT:
                    createAndSendFloat(msg.replyTo);
                    break;
                case MessageSource.MSG_CREATE_STRING:
                    createAndSendString(msg.replyTo);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /*
      这个Messenger用于向Activity发送Message。
      This Messenger is used to send Message to Activity.
     */
    private Messenger mSendMessenger = new Messenger(mActMsgHandler);

    /*
     假设有耗时的操作需要异步进行。
     Suppose we have long-running jobs and execute asynchronously.
     */
    private Executor mExecutor = Executors.newCachedThreadPool();

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSendMessenger.getBinder();
    }

    private void createAndSendInt(final Messenger messenger) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                int ret = random.nextInt();
                String str = "Give you a int: " + ret;
                send(MessageSource.MSG_CREATE_INT, str, messenger);

            }
        });
    }

    private void createAndSendFloat(final Messenger messenger) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Random random = new Random();
                float ret = random.nextFloat();
                String str = "Give you a float: " + ret;
                send(MessageSource.MSG_CREATE_FLOAT, str, messenger);
            }
        });
    }

    private void createAndSendString(final Messenger messenger) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int len = MessageSource.STRING_SOURCE_ARRAY.length;
                Random random = new Random();
                int index = random.nextInt(len);
                String ret = MessageSource.STRING_SOURCE_ARRAY[index];
                String str = "Give you a string: " + ret;
                send(MessageSource.MSG_CREATE_STRING, str, messenger);
            }
        });
    }

    private void send(int type, String str, Messenger messenger) {
        Message msg = new Message();
        msg.what = type;
        msg.obj = str;
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}