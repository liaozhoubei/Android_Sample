package com.example.taskdispatcher.example.tasks.delayinittask;


import com.example.taskdispatcher.example.utils.LogUtils;
import com.example.taskdispatcher.task.MainTask;

public class DelayInitTaskA extends MainTask {

    @Override
    public void run() {
        // 模拟一些操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.i("DelayInitTaskA finished");
    }
}
