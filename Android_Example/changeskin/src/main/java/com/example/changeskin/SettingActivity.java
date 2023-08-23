package com.example.changeskin;


import android.content.Intent;
import android.content.res.Resources;
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
import androidx.fragment.app.Fragment;

import java.io.File;

import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;

public class SettingActivity extends AppCompatActivity {
    String TAG = "SettingActivity";
    private ChangeResourceFragment changeResourceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        changeResourceFragment = (ChangeResourceFragment) getSupportFragmentManager().findFragmentById(R.id.change_resource);

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
        Log.e(TAG, "sd_changeSkin: " + Environment.getExternalStorageDirectory());
        String path = "skinnight-debug.skin";

        File filesDir;
        // Android 29 + 有存储限制，只能读取特定目录
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            filesDir = getExternalFilesDir(null);
        } else {
            filesDir = Environment.getExternalStorageDirectory();
        }
        File file = new File(filesDir, path);
        if (file.exists()) {
            Toast.makeText(this, "从 SD 卡中加载资源", Toast.LENGTH_SHORT).show();
            // 加载SD卡
            SkinCompatManager.getInstance().loadSkin(path,
                    skinLoaderListener, CustomSDCardLoader.SKIN_LOADER_STRATEGY_SDCARD);
        } else {
            Log.e(TAG, "changeSkin: 需要先把资源包放在 /sdcard 目录下：" + file.getAbsolutePath());
            Toast.makeText(this, "在SD卡中无法找到资源", Toast.LENGTH_SHORT).show();
        }

    }

    public void asset_changeSkin(View view) {

        Toast.makeText(this, "从Asset 中加载资源", Toast.LENGTH_SHORT).show();
        // 加载asset
        SkinCompatManager.getInstance().loadSkin("skinday-debug.skin",
                skinLoaderListener, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
    }

    SkinCompatManager.SkinLoaderListener skinLoaderListener = new SkinCompatManager.SkinLoaderListener() {
        @Override
        public void onStart() {
            Log.e(TAG, "onStart: ");
        }

        @Override
        public void onSuccess() {
            Log.e(TAG, "onSuccess: ");
            SkinCompatResources skinCompatResources = SkinCompatResources.getInstance();
            Resources resources = skinCompatResources.getSkinResources();
            try {
//                String appName = getResources().getResourceEntryName(R.string.app_name);
////                Resources resources = createPackageContext("com.example.testskin.day", 0).getResources();
//                int id = resources.getIdentifier(appName, "string", skinCompatResources.getSkinPkgName());
//                String name = resources.getString(id);
//                Log.e(TAG, "onSuccess appName: " + name);
//                changeResourceFragment.setTextView(name);


                String theme_name = getResources().getResourceEntryName(R.string.theme_name);
                int theme_name_id = resources.getIdentifier(theme_name, "string", skinCompatResources.getSkinPkgName());
                String themeName = resources.getString(theme_name_id);
                Log.e(TAG, "onSuccess themename : " + themeName);

                if (!"common_theme".equals(themeName)) {
                    changeResourceFragment.onLayout(themeName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailed(String errMsg) {
            Log.e(TAG, "onFailed: " + errMsg);
        }
    };
}