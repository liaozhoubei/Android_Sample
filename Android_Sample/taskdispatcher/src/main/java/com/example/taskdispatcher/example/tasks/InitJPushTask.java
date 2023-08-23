package com.example.taskdispatcher.example.tasks;

import android.util.Log;

import com.example.taskdispatcher.example.MyApplication;
import com.example.taskdispatcher.task.Task;

import cn.jpush.android.api.JPushInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 需要在getDeviceId之后执行
 * 执行有依赖关系的任务
 */
public class InitJPushTask extends Task {

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> task = new ArrayList<>();
        task.add(GetDeviceIdTask.class);
        return task;
    }

    @Override
    public void run() {
        MyApplication app = (MyApplication) mContext;
        Log.e(TAG, "run: InitJPushTask DeviceId:" + app.getDeviceId() );
//        JPushInterface.init(mContext);
//        JPushInterface.setAlias(mContext, 0, app.getDeviceId());
    }

}
