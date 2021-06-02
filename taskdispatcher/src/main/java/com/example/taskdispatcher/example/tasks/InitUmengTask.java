package com.example.taskdispatcher.example.tasks;

import com.example.taskdispatcher.task.Task;
import com.umeng.commonsdk.UMConfigure;

public class InitUmengTask extends Task {

    @Override
    public void run() {
        UMConfigure.init(mContext, "58edcfeb310c93091c000be2", "umeng",
                UMConfigure.DEVICE_TYPE_PHONE, "1fe6a20054bcef865eeb0991ee84525b");
    }
}
