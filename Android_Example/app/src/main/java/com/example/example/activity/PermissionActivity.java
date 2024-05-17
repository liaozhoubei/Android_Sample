package com.example.example.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.example.MainActivity;
import com.example.example.R;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;



public class PermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "PermissionActivity";
    private static final int RC_PERMISSION = 122;

    // android 10L (API level 29) 及以下申请权限
    String[] permissions10 = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    // android 12L (API level 32) 及以下申请权限
    @RequiresApi(api = Build.VERSION_CODES.R)
    String[] permissions12 = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    // android 13 (API level 33) 及以上申请权限
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    String[] permissions13 = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
    };
    // android 14 (API level 34) 及以上申请权限
    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    String[] permissions14 = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permission);
//        getWindow().getDecorView().setBackground(null);
        // 仅仅是用来展示一些起屏页
        SystemClock.sleep(2000);

        requestPermissionTask();
    }


    @AfterPermissionGranted(RC_PERMISSION)
    private void requestPermissionTask() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14及以上部分照片和视频访问权限
            permissions= permissions14;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13及以上完整照片和视频访问权限
            permissions= permissions13;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Android 12及以下完整本地读写访问权限
            permissions= permissions12;
        }else {
            permissions = permissions10;
        }


        if (EasyPermissions.hasPermissions(this, permissions)) {
            // Have permission, do the thing!
            Toast.makeText(this, "获得权限", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, "允许权限请求",
                    RC_PERMISSION, permissions);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        for (String perm : perms) {
            Log.e(TAG, "onPermissionsDenied:" + requestCode + ":" + perm);
        }

        Toast.makeText(this, "您拒绝了权限", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
