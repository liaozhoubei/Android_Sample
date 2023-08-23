package com.example.changeskin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "PermissionActivity";
    private static final int RC_PERMISSION = 122;
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permission);
//        getWindow().getDecorView().setBackground(null);
        try {
            // 仅仅是用来展示一些起屏页
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        requestPermissionTask();
    }



    @AfterPermissionGranted(RC_PERMISSION)
    private void requestPermissionTask() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            // Have permission, do the thing!
            Toast.makeText(this, "为所欲为", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SettingActivity.class));
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
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        Toast.makeText(this, "您拒绝了权限", Toast.LENGTH_SHORT).show();
        finish();
    }
}