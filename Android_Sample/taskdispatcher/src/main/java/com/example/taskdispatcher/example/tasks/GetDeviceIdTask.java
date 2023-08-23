package com.example.taskdispatcher.example.tasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.example.taskdispatcher.example.MyApplication;
import com.example.taskdispatcher.task.Task;


public class GetDeviceIdTask extends Task {

    private String mDeviceId;

    @Override
    public void run() {
        MyApplication app = (MyApplication) mContext;
        mDeviceId = Settings.System.getString(mContext.getContentResolver(), Settings.System.ANDROID_ID);
        app.setDeviceId(mDeviceId);
    }

}
