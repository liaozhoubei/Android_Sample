package com.example.taskdispatcher.example.tasks.delayinittask;


import com.example.taskdispatcher.example.utils.LogUtils;
import com.example.taskdispatcher.task.MainTask;

public class DelayInitTaskB extends MainTask {

    @Override
    public void run() {
        // 模拟一些操作

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.i("DelayInitTaskB finished");
    }
}
