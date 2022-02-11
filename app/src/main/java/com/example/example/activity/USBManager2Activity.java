package com.example.example.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.example.R;
import com.example.example.util.DocumentsUtils;
import com.example.example.util.StorageUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 第二种读取USB的方式
 */
public class USBManager2Activity extends AppCompatActivity {
    private static final String TAG = "USBManager2Activity";
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbmanager2);
        verifyStoragePermissions(this,REQUEST_CODE);
        String sdPath = StorageUtils.getSDCardDir(this);
        if (sdPath != null) {
            String str = DocumentsUtils.getTreeUri(this,sdPath);
            if (TextUtils.isEmpty(str)) {
                showOpenDocumentTree(sdPath);
            }
        }
    }

    private void showOpenDocumentTree(String rootPath) {
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StorageManager sm = getSystemService(StorageManager.class);
            StorageVolume volume = sm.getStorageVolume(new File(rootPath));
            if (volume != null) {
                intent = volume.createAccessIntent(null);
            }
        }
        if (intent == null) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        }
        Log.d(TAG,"startActivityForResult...");
        startActivityForResult(intent, DocumentsUtils.OPEN_DOCUMENT_TREE_CODE);
    }

    /**
     * 读取USB信息
     * @param v
     */
    public void readUsb(View v) {
        String path = StorageUtils.getUsbDir(this);
        if (!TextUtils.isEmpty(path)) {
            File[] files = new File(path).listFiles();
            if (files != null) {
                for (File file : files) {
//                    if (file.getName().endsWith(".zip")) {
                    Log.d(TAG, "file path=" + file.getAbsolutePath());
//                    }
                }
            }
        } else {
            Log.d(TAG, "没有插入U盘");
        }
    }

    /**
     * 写入 TF 卡
     * 没有TF卡会导致崩溃
     * @param v
     */
    public void writeTf(View v) {
        String rootPath = StorageUtils.getSDCardDir(this);
        String urlStr = DocumentsUtils.getTreeUri(this, rootPath);
        Log.e(TAG, "onClick1: "+ urlStr );
        if (urlStr!= null){
            Uri uri = Uri.parse(urlStr);
            writeFile(this, uri);
            File dowloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            DocumentsUtils.doCopyFile(this,null,dowloadDirectory.getAbsolutePath()+"/helloWorld.apk","copyApk.apk");
        }

    }

    /**
     * 在外置TF卡根目录下创建目录
     */
    public void writeFile(Context context, Uri uri) {
        if (uri.getPath().isEmpty()) {
            String sdPath = StorageUtils.getSDCardDir(context);
            if (TextUtils.isEmpty(sdPath)) {
                Log.w(TAG, "init folder is null path");
                return;
            }
            String uriStr = DocumentsUtils.getTreeUri(context, sdPath);
            if (TextUtils.isEmpty(uriStr)) {
                return;
            }
            uri = Uri.parse(uriStr);
        }
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
        // 创建文件，并写入字符串
        if (!DocumentsUtils.isExist(documentFile, "hello")) {
            if (documentFile != null) {
                try {
                    DocumentFile fileHello = documentFile.createDirectory("hello");
                    DocumentFile fileText = fileHello.createFile("text/plain", "test.txt");
                    OutputStream out = null;
                    try {
                        out = context.getContentResolver().openOutputStream(fileText.getUri());
                        out.write("A long time ago...".getBytes());
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DocumentFile fileImage = fileHello.createFile("image/png", "luncher.png");
                    try {
                        out = context.getContentResolver().openOutputStream(fileImage.getUri());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher).compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        InputStream inputimage = new ByteArrayInputStream(baos.toByteArray());
                        byte[] bytes = new byte[1024 * 10];
                        int len = 0;
                        while ((len = inputimage.read(bytes)) != -1) {
                            out.write(bytes, 0, len);
                        }
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DocumentsUtils.OPEN_DOCUMENT_TREE_CODE:
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    String rootPath = StorageUtils.getSDCardDir(this);
                    DocumentsUtils.saveTreeUri(this, rootPath, uri);
                    final int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Log.d(TAG, "save tree uri = " + uri);
                    // 相当于将url存储下来，下次开启apk不在进行弹框权限请求
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                }
                break;
        }
    }

    private boolean verifyStoragePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int write = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            //检测是否有权限，如果没有权限，就需要申请
            if (write != PackageManager.PERMISSION_GRANTED ||
                    read != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                activity.requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, requestCode);
                return false;
            }
        }
        return true;
    }
}