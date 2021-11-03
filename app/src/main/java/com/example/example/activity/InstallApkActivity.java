package com.example.example.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;

import com.example.example.BuildConfig;
import com.example.example.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;


/**
 * 安装软件
 *
 */
public class InstallApkActivity extends AppCompatActivity {
    private static final String TAG = "InstallApkActivity";
    private static final int INSTAKK_APK = 144;
    private static final int InstallError = 145;
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/installapk.apk";
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == INSTAKK_APK){
                installAPK();
            }else {
                Toast.makeText(InstallApkActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_apk);
        initView();
    }

    private void initView() {
        Button btnInstallApk = (Button) findViewById(R.id.btn_install_apk);
        btnInstallApk.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int read = PermissionChecker.checkSelfPermission(getApplication(), permissions[0]);
            int write = PermissionChecker.checkSelfPermission(getApplication(), permissions[1]);
            if (read == PermissionChecker.PERMISSION_GRANTED && write == PermissionChecker.PERMISSION_GRANTED){
                // Have permission, do the thing!
                Toast.makeText(getApplication(), "已拥有读写权限", Toast.LENGTH_LONG).show();
                copyApkToDownload();
            }else {
                // Request one permission
                permissionLauncher.launch(permissions);

            }
        }
    };

    /**
     * 模拟下载 apk
     * 将需要安装的 apk 拷贝到本地
     */
    private void copyApkToDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    inputStream = getResources().openRawResource(R.raw.install_apk);
//                    File parentFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Download");
                    File parentFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                    if (!parentFile.exists()){
                        parentFile.mkdirs();
                    }
                    File file = new File(path);
                    if (!file.exists()) {
                        ///storage/sdcard/Download/installapk.apk
                        if (file.createNewFile()){
                            fileOutputStream = new FileOutputStream(new File(path));
                            int len = 0;
                            byte[] bytes = new byte[1024];
                            while ((len = inputStream.read(bytes)) != -1) {
                                fileOutputStream.write(bytes, 0, len);
                            }
                            fileOutputStream.flush();
                            handler.sendEmptyMessage(INSTAKK_APK);
                        }else {
                            handler.sendEmptyMessage(InstallError);
                        }
                    }else {
                        handler.sendEmptyMessage(INSTAKK_APK);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(InstallError);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }



    private void installAPK() {
        //版本判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //获取是否有安装未知来源应用的权限
            boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                // 显示调用要求打开安装未知来源 apk 权限，避免 crash 。虽然直接调用 startInstall 也会打开权限页面
                Toast.makeText(this, "请打开安装未知来源应用的权限", Toast.LENGTH_LONG).show();
                //没有权限 设置你的app包名
                String packageName = this.getPackageName();
                Log.e(TAG, "installAPK: " + packageName);
                Uri packageURI = Uri.parse("package:" + packageName);
                // 打开相应应用权限页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                activityResultLauncher.launch(intent);
            } else {
                Toast.makeText(this, "有未知来源权限", Toast.LENGTH_LONG).show();
                startInstall(path);
            }
        } else {
            Toast.makeText(this, "直接安装", Toast.LENGTH_LONG).show();
            startInstall(path);
        }
    }

    private void startInstall(String path) {
        Log.e(TAG, "startInstall: " + path);
        //在上面已经获取到apk存储路径
        //跳转安装
        Intent intentInstall = new Intent();
        intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentInstall.setAction(Intent.ACTION_VIEW);

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0 以后要用 FileProvider 的方式传递文件路径
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".FileProvider", new File(path));
            intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intentInstall);
    }

    private ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                installAPK();
            }else {
                Log.e(TAG, "onActivityResult: 申请未知来源权限失败 :" + result );
            }
        }
    });

    private final ActivityResultLauncher permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Set<String> keySet = result.keySet();
            for (String key:keySet) {
                Boolean aBoolean = result.get(key);
                Log.e(TAG, "onActivityResult: key=" + key + "  value="+ aBoolean );
            }
            Toast.makeText(getApplication(), "已拥有读写权限", Toast.LENGTH_LONG).show();
            copyApkToDownload();
        }
    });

}
