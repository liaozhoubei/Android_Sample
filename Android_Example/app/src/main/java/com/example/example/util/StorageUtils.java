package com.example.example.util;
import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StorageUtils {
    private static final String TAG = "StorageUtils";

    //定义GB的计算常量
    private static final int GB = 1024 * 1024 * 1024;
    //定义MB的计算常量
    private static final int MB = 1024 * 1024;
    //定义KB的计算常量
    private static final int KB = 1024;

    public static String bytes2kb(long bytes) {
        //格式化小数
        DecimalFormat format = new DecimalFormat("###.0");
        if (bytes / GB >= 1) {
            return format.format(bytes * 1.0f / GB) + "GB";
        } else if (bytes / MB >= 1) {
            return format.format(bytes * 1.0f / MB) + "MB";
        } else if (bytes / KB >= 1) {
            return format.format(bytes * 1.0f / KB) + "KB";
        } else {
            return bytes + "B";
        }
    }

    /*
    获取全部存储设备信息封装对象
     */
    public static ArrayList<Volume> getVolume(Context context) {
        ArrayList<Volume> list_storagevolume = new ArrayList<Volume>();

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            Method method_volumeList = StorageManager.class.getMethod("getVolumeList");

            method_volumeList.setAccessible(true);

            Object[] volumeList = (Object[]) method_volumeList.invoke(storageManager);
            if (volumeList != null) {
                Volume volume;
                for (int i = 0; i < volumeList.length; i++) {
                    try {
                        volume = new Volume();
                        volume.setPath((String) volumeList[i].getClass().getMethod("getPath").invoke(volumeList[i]));
                        volume.setRemovable((boolean) volumeList[i].getClass().getMethod("isRemovable").invoke(volumeList[i]));
                        volume.setState((String) volumeList[i].getClass().getMethod("getState").invoke(volumeList[i]));
                        list_storagevolume.add(volume);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.e("null", "-null-");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return list_storagevolume;
    }

    /**
     * SD卡剩余空间
     */
    public static long getSDFreeSize(String path) {
        try {
            // 取得SD卡文件路径
            StatFs sf = new StatFs(path);
            // 获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSizeLong();
            // 空闲的数据块的数量
            long freeBlocks = sf.getAvailableBlocksLong();
            // 返回SD卡空闲大小
            // return freeBlocks * blockSize; //单位Byte
            // return (freeBlocks * blockSize)/1024; //单位KB
            return (freeBlocks * blockSize);  // 单位GB
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "hello world");
        }
        return 0L;
    }

    /**
     * SD卡总容量
     */
    public static long getSDAllSize(String path) {
        try { // 取得SD卡文件路径
            StatFs sf = new StatFs(path);
            // 获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSizeLong();
            // 获取所有数据块数
            long allBlocks = sf.getBlockCountLong();
            // 返回SD卡大小
            // return allBlocks * blockSize; //单位Byte
            // return (allBlocks * blockSize)/1024; //单位KB
            return (allBlocks * blockSize); // 单位GB
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "hello world");
        }
        return 0L;
    }

    public static String getSDCardDir(Context context) {
        String sdcardDir = null;
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isSd = diskInfoClazz.getMethod("isSd");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    if (disk != null) {
                        if ((boolean) isSd.invoke(disk)) {
                            sdcardDir = (String) path.get(volumeInfo);
                            break;
                        }
                    }
                }
            }
            if (sdcardDir == null) {
                Log.w(TAG, "sdcardDir null");
                return null;
            } else {
                Log.i(TAG, "sdcardDir " + sdcardDir + File.separator);
                return sdcardDir + File.separator;
            }
        } catch (Exception e) {
            Log.i(TAG, "sdcardDir e " + e.getMessage());
            e.printStackTrace();
        }
        Log.w(TAG, "sdcardDir null");
        return null;
    }

    public static String getUsbDir(Context context) {
        String sdcardDir = null;
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");
            Method isUsb = diskInfoClazz.getMethod("isUsb");
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getType = volumeInfoClazz.getMethod("getType");
            Method getDisk = volumeInfoClazz.getMethod("getDisk");
            Field path = volumeInfoClazz.getDeclaredField("path");
            Method getVolumes = storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result = (List<Class<?>>) getVolumes.invoke(storageManager);
            for (int i = 0; i < result.size(); i++) {
                Object volumeInfo = result.get(i);
                Log.w(TAG, "disk path " + path.get(volumeInfo));
                if ((int) getType.invoke(volumeInfo) == 0) {
                    Object disk = getDisk.invoke(volumeInfo);
                    Log.w(TAG, "is usb " + isUsb.invoke(disk));
                    if (disk != null) {
                        if ((boolean) isUsb.invoke(disk)) {
                            sdcardDir = (String) path.get(volumeInfo);
                            break;
                        }
                    }
                }
            }
            if (sdcardDir == null) {
                Log.w(TAG, "usbpath null");
                return null;
            } else {
                Log.i(TAG, "usbpath " + sdcardDir + File.separator);
                return sdcardDir + File.separator;
            }
        } catch (Exception e) {
            Log.i(TAG, "usbpath e " + e.getMessage());
            e.printStackTrace();
        }
        Log.w(TAG, "usbpath null");
        return null;
    }

    /*
     存储设备信息封装类
     */
    public static class Volume {
        protected String path;
        protected boolean removable;
        protected String state;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isRemovable() {
            return removable;
        }

        public void setRemovable(boolean removable) {
            this.removable = removable;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
