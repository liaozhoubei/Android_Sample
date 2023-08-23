package com.example.example.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

public class BitmapUtils {

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(-angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 从图片路径中获取压缩的bitmap
     * @param imgPath
     * @param pixelW
     * @param pixelH
     * @return
     */
    public static Bitmap compressImage(String imgPath, float pixelW, float pixelH)
    {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 将file形式的图片转为bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        //原图的宽高
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;
        float ww = pixelW;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh)
        {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 开始压缩图片，要读取整张图片内存，要将options.inJustDecodeBounds 设回false
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        return bitmap;
    }

    /**
     *
     * @param img   rgb 的数组
     * @param pixelW
     * @param pixelH
     * @return
     */
    public static Bitmap compressImage(byte[] img, float pixelW, float pixelH)
    {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 将file形式的图片转为bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0,img.length, newOpts);
        newOpts.inJustDecodeBounds = false;
        //原图的宽高
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;
        float ww = pixelW;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww)
        {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh)
        {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 开始压缩图片，要读取整张图片内存，要将options.inJustDecodeBounds 设回false
        bitmap = BitmapFactory.decodeByteArray(img, 0,img.length, newOpts);
        return bitmap;
    }


    /**
     * 等比压缩图片
     *
     * @param resBitmap 原图
     * @param desWidth  压缩后图片的宽度
     * @param desHeight 压缩后图片的高度
     * @return 压缩后的图片
     */
    public static Bitmap calculateInSampleSize(Bitmap resBitmap, int desWidth, int desHeight) {
        int resWidth = resBitmap.getWidth();
        int resHeight = resBitmap.getHeight();
        if (resHeight > desHeight || resWidth > desWidth) {
            // 计算出实际宽高和目标宽高的比率
            final float heightRatio = (float) desHeight / (float) resHeight;
            final float widthRatio = (float) desWidth / (float) resWidth;
            float scale = heightRatio < widthRatio ? heightRatio : widthRatio;
            return scale(resBitmap, scale);
        }
        return resBitmap;
    }

    /**
     * 等比压缩图片
     *
     * @param bitmap 原图
     * @param scale  压缩因子
     * @return 压缩后的图片
     */
    private static Bitmap scale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        int orientation = 0;
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() != 1) {
                return -1;
            }
            cursor.moveToFirst();
            orientation = cursor.getInt(0);
            cursor.close();
        }
        return orientation;
    }

    public static Bitmap rotateImage(Bitmap bmp, int degrees) {
        if (degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        return bmp;
    }
}
