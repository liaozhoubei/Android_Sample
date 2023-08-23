package com.example.example.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    /**
     * 把文件导出
     *
     * @param fd
     * @param fileName
     */
    public static boolean ExportFileToAnotherByFileDescriptor(FileDescriptor fd, String fileName) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fis = new FileInputStream(fd);
            int len = 0;
            byte[] b = new byte[1024];
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Temp");
            if (!file.exists()) {
                file.mkdirs();
            }
            File rf = new File(file.getAbsoluteFile() + File.separator + fileName);
            if (rf.exists()) {
                rf.delete();
            }
            rf.createNewFile();
            fos = new FileOutputStream(rf);
            while ((len = fis.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    public static boolean ExportFileToAnother(File sourcefile, String fileName) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fis = new FileInputStream(sourcefile);
            int len = 0;
            byte[] b = new byte[1024];
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Temp");
            if (!file.exists()) {
                file.mkdirs();
            }
            File rf = new File(file.getAbsoluteFile() + File.separator + fileName);
            if (rf.exists()) {
                rf.delete();
            }
            rf.createNewFile();
            fos = new FileOutputStream(rf);
            while ((len = fis.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.flush();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * 隐藏状态栏和底部虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
