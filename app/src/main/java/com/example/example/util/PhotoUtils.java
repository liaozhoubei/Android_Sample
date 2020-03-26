package com.example.example.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.ExternalStorageStats;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.example.example.BuildConfig;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtils {
    private static final String TAG = "PhotoUtils";

    /**
     * 调用相机进行拍照
     *
     * @param activity    当前activity
     * @param requestCode 调用系统相机请求码
     */
    public static Uri takePicture(Activity activity, int requestCode) {
        //调用系统相机(8.0需要在intent()里写相机路径)

        //拍照后原图回存入此路径下
        File camerafile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + File.separator + System.currentTimeMillis() + ".jpg");
        Intent intentCamera = new Intent();

        Uri cameraUri = CreateTakePhotoUri(activity, camerafile);  //拍照后照片存储路径
        // 若不使用以上方法，则在获取需要给应用授权
        // Uri fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".FileProvider", camerafile);
        //
        // List<ResolveInfo> resInfoList = getPackageManager()
        //         .queryIntentActivities(intentCamera, PackageManager.MATCH_DEFAULT_ONLY);
        // for (ResolveInfo resolveInfo : resInfoList) {
        //     String packageName = resolveInfo.activityInfo.packageName;
        //     //添加这一句表示对目标应用临时授权该Uri所代表的文件
        //     grantUriPermission(packageName, cameraUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
        //             | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // }

        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentCamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        activity.startActivityForResult(intentCamera, requestCode);
        return cameraUri;
    }


    /**
     * @param activity    当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openPic(Activity activity, int requestCode) {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,展示分类列表)
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @param activity    当前activity
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Activity activity, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 加入访问权限
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        activity.startActivityForResult(intent, requestCode);
    }

    public static Uri CreateTakePhotoUri(Context context, File file) {
        Uri newUri;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10以后使用这种方法
            String status = Environment.getExternalStorageState();
            // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                newUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            } else {
                newUri = context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
            }
        } else {
            newUri = createUri(context, file);
        }
        return newUri;

    }

    /**
     * 在低于 android N 的时候，就用以前的 Uri.fromFile(file);方式传递文件路径
     *
     * @param file
     * @return
     */
    public static Uri createUri(Context context, File file) {
        Uri newUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
            // 并且这样可以解决MIUI系统上拍照返回size为0的情况
            newUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", file);
        } else {
            // 在适配android 4.4
            newUri = Uri.fromFile(file);
        }
        return newUri;
    }

    /**
     * 读取uri所在的图片
     *
     * @param uri      图片对应的Uri
     * @param mContext 上下文对象
     * @return 获取图像的Bitmap
     */
    public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Android 10 之后不可以使用绝对路径
     *
     * @param context 上下文对象
     * @param uri     当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return pathHead + getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 保存图片到 /data/package/DCIM/cache 中
     * 请注意需要清理图片
     *
     * @param context
     * @param uri
     * @return
     */
    public static String saveBitmap(Context context, Uri uri) {
        String result = "";

        String status = Environment.getExternalStorageState();
        File imageCache;
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            imageCache = new File(externalFilesDir, "cache");
            if (!imageCache.exists()) {
                imageCache.mkdirs();
            }
        } else {
            File filesDir = context.getFilesDir();
            imageCache = new File(filesDir, "Environment.DIRECTORY_DCIM/cache");
            if (!imageCache.exists()) {
                imageCache.mkdirs();
            }
        }
        String image_cache = imageCache.getAbsolutePath();
        String fileName = System.currentTimeMillis() + ".JPG";
        File f = new File(image_cache, fileName);
        if (f.exists()) {
            f.delete();
        }
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap bitmapFromUri = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            FileOutputStream out = new FileOutputStream(f);
            bitmapFromUri.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            if (f.exists())
                result = f.getAbsolutePath();
//                Log.i(TAG, "已经保存");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ExifInterface exifInterface = new ExifInterface(fileDescriptor);
                // 只有jpg 格式才能获取 EXIF 信息
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                ExifInterface newexifInterface = new ExifInterface(result);
                newexifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(orientation));
                newexifInterface.saveAttributes();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 清理 /data/package/DCIM/cache 中的图片
     *
     * @param context
     */
    public static void clearImages(Context context) {
        String status = Environment.getExternalStorageState();
        File imageCache;
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
            imageCache = new File(externalFilesDir, "cache");
            if (!imageCache.exists()) {
                return;
            }
        } else {
            File filesDir = context.getFilesDir();
            imageCache = new File(filesDir, "Environment.DIRECTORY_DCIM/cache");
            if (!imageCache.exists()) {
                return;
            }
        }
        File[] files = imageCache.listFiles();
        if (files != null && files.length > 0) {
            for (File cache : files) {
                cache.delete();
            }
        }
    }
}