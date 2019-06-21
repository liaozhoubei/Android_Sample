package com.example.example.util;

import android.os.Environment;

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
}
