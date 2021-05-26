package com.example.changeskin;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;

import java.io.File;

import skin.support.SkinCompatManager;

public class SettingActivity extends AppCompatActivity {
    String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    }

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }


    public void startOther(View view) {
        startActivity(new Intent(this, ChangeActivity.class));
    }

    public void resetSkin(View view) {
        Toast.makeText(this, "恢复默认资源", Toast.LENGTH_SHORT).show();
        SkinCompatManager.getInstance().restoreDefaultTheme();
    }

    public void sd_changeSkin(View view) {
        Log.e(TAG, "sd_changeSkin: " + Environment.getExternalStorageDirectory() );
        String path = "skinnight-debug.skin";

        File filesDir;
        // Android 29 + 有存储限制，只能读取特定目录
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            filesDir = getExternalFilesDir(null);
        }else {
            filesDir = Environment.getExternalStorageDirectory();
        }
        File file = new File(filesDir, path);
        if (file.exists()){
            Toast.makeText(this, "从 SD 卡中加载资源", Toast.LENGTH_SHORT).show();
            // 加载SD卡
            SkinCompatManager.getInstance().loadSkin(path,
                    skinLoaderListener, CustomSDCardLoader.SKIN_LOADER_STRATEGY_SDCARD);
        }else {
            Log.e(TAG, "changeSkin: 需要先把资源包放在 /sdcard 目录下：" + file.getAbsolutePath() );
            Toast.makeText(this, "在SD卡中无法找到资源", Toast.LENGTH_SHORT).show();
        }

    }

    public void asset_changeSkin(View view) {

        Toast.makeText(this, "从Asset 中加载资源", Toast.LENGTH_SHORT).show();
        // 加载asset
        SkinCompatManager.getInstance().loadSkin("skinday-debug.skin",
                skinLoaderListener, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
    }

    SkinCompatManager.SkinLoaderListener skinLoaderListener =  new SkinCompatManager.SkinLoaderListener() {
        @Override
        public void onStart() {
            Log.e(TAG, "onStart: ");
        }

        @Override
        public void onSuccess() {
            Log.e(TAG, "onSuccess: ");
        }

        @Override
        public void onFailed(String errMsg) {
            Log.e(TAG, "onFailed: " + errMsg);
        }
    };
}