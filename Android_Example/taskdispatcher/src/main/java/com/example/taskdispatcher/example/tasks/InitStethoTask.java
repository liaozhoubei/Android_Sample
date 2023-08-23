package com.example.taskdispatcher.example.tasks;

import android.os.Handler;
import android.os.Looper;

import com.example.taskdispatcher.task.Task;
import com.facebook.stetho.Stetho;

/**
 * 异步的Task
 */
public class InitStethoTask extends Task {

    @Override
    public void run() {

        Handler handler = new Handler(Looper.getMainLooper());
        Stetho.initializeWithDefaults(mContext);
    }
}
