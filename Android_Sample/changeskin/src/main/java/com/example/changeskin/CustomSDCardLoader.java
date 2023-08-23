package com.example.changeskin;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

import skin.support.load.SkinSDCardLoader;
import skin.support.utils.SkinFileUtils;

public class CustomSDCardLoader extends SkinSDCardLoader {
    public static final int SKIN_LOADER_STRATEGY_SDCARD = Integer.MAX_VALUE;

    @Override
    protected String getSkinPath(Context context, String skinName) {
        // 根据自定义的路径加载资源
        File filesDir;
        // Android 29 + 有存储限制，只能读取特定目录
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            filesDir = context.getExternalFilesDir(null);
        }else {
            filesDir = Environment.getExternalStorageDirectory();
        }
        return new File(filesDir, skinName).getAbsolutePath();
    }

    @Override
    public int getType() {
        return SKIN_LOADER_STRATEGY_SDCARD;
    }
}
