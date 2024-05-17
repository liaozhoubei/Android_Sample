package com.example.example.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.R;
import com.example.example.util.FileUtils;
import com.example.example.util.PhotoUtils;
import com.example.example.util.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

//

/**
 * Android 7.0 以后 获取相册照片以及相机拍照返回的方式发送变化
 * 通过手机自带的文件系统已无法获取路径，可实现简单的文件浏览器来获取路径
 * 可查看 https://blog.csdn.net/lmj623565791/article/details/72859156 了解 FileProvide 相关知识
 * <p>
 * Android 10 以后，getExternalFilesDir()  为私有目录，可以获取路径，其他文件夹路径均无法获取
 */
public class FileProvideActivity extends AppCompatActivity {

    private static final String TAG = "FileProvideActivity";
    private final int GET_PHOTO_FROM_CAMERA = 115;
    private final int GET_PHOTO_FROM_GALLERY = 114;
    private final int GET_PHOTO_FROM_CROP = 113;
    private final int REQUEST_CODE_SELECT_FILE = 112;
    private static final int READ_REQUEST_CODE = 42;
    private ImageView ivImg;
    private Uri cameraUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_provide);
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                PhotoUtils.clearImages(getBaseContext());
            }
        }).start();
    }

    private void initView() {
        ivImg = (ImageView) findViewById(R.id.iv_img);
    }

    // 获取相册照片
    public void GetPic(View view) {
        PhotoUtils.openPic(this, GET_PHOTO_FROM_GALLERY);
    }

    // 拍照
    public void TakePhoto(View view) {
        cameraUri = PhotoUtils.takePicture(this, GET_PHOTO_FROM_CAMERA);
    }

    public void GetFile(View view) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            FileUtils.performFileSearch(this, READ_REQUEST_CODE);
        } else {
            // 在目标版本为 7.0 之前可用系统文件浏览器获取路径
            // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // intent.setType("*/*");
            // intent.addCategory(Intent.CATEGORY_OPENABLE);
            //
            // intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // startActivityForResult(Intent.createChooser(intent, "选择文件"), REQUEST_CODE_SELECT_FILE);

            // 在 Android 7.0 以后基本无法通过系统文件浏览器获取到文件路径，
            // 可实现一个简单的文件浏览器获取文件路径
            startActivity(new Intent(this, FileListActivity.class));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_PHOTO_FROM_CAMERA && resultCode != Activity.RESULT_CANCELED) {
            Log.e("FileProvideActivity", "onActivityResult: 拍摄照片");
            if (data.getData() == null) {
                Log.e(TAG, "onActivityResult capture photo: if you set output path , data will return null ");
            }
            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) return;
            // 把原图显示到界面上
            File cachefile = new File(getExternalCacheDir(), "face-cropped");
            if (!cachefile.exists()) {
                try {
                    cachefile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Uri cropImageUri = PhotoUtils.createUri(this, cachefile);
            PhotoUtils.cropImageUri(this, cameraUri, cropImageUri, 60, 60, 480, 480, GET_PHOTO_FROM_CROP);

        } else if (requestCode == GET_PHOTO_FROM_GALLERY && resultCode == Activity.RESULT_OK
                && null != data) {
            // 从相册获取图片
            try {
//                ivImg.setImageURI(data.getData());

                getBitmapFromGallery(FileProvideActivity.this, data);
//                Bitmap bitmapFromUri = FileUtils.getBitmapFromUri(FileProvideActivity.this, data.getData());
//                ivImg.setImageBitmap(bitmapFromUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GET_PHOTO_FROM_CROP && resultCode == Activity.RESULT_OK) {
            // 裁剪后获取图片
            Bitmap bitmap = getBitmepAfterCrop(FileProvideActivity.this, data.getData());
            if (bitmap == null) return;
            ivImg.setImageBitmap(bitmap);
            Log.e("FileProvideActivity", "onActivityResult: 裁剪照片");

        } else if (requestCode == REQUEST_CODE_SELECT_FILE && resultCode == Activity.RESULT_OK) {
            // 选择文件
            getDocument(data);

        } else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The ACTION_OPEN_DOCUMENT intent was sent with the request code
            // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
            // response to some other intent, and the code below shouldn't run at all.

            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Uri: " + uri.getPath());
                Log.i(TAG, "Uri: " + uri.toString());
                FileUtils.dumpFileMetaData(FileProvideActivity.this, uri);
//                String s = readTextFromUri(uri);
//                Log.i(TAG, "info: " + s);
            }
        }

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "onActivityResult 返回失败", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onActivityResult: result error" + resultCode);
        }
        Log.e(TAG, "onActivityResult: is result ok =" + (resultCode == Activity.RESULT_OK));

    }


    /**
     * Android 10 由于文件权限的关系不能使用图片路径直接加载手机储存卡内的图片，除非图片是在应用的私有目录下
     * 获取相册中的照片
     *
     * @param context
     * @param data
     */
    private void getBitmapFromGallery(Context context, Intent data) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // 先将图片保存到私有目录中，然后再进行裁剪
            String imagePath = PhotoUtils.saveBitmap(this, data.getData());
            File realfile = new File(imagePath);
            Uri newUri = PhotoUtils.createUri(context, realfile);

            Uri cropImageUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                String mimeType = getContentResolver().getType(newUri);
                String imageName = System.currentTimeMillis() / 1000 + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                contentValues.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
                cropImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else {
                File cachefile = new File(getExternalCacheDir(), "face-cropped");
                cropImageUri = Uri.fromFile(cachefile);
            }

            PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, 480, 480, GET_PHOTO_FROM_CROP);
//            ivImg.setImageURI(Uri.fromFile(new File(imagePath)));
        } else {
            // 设置需要裁剪的缓存路径
            File cachefile = new File(getExternalCacheDir(), "face-cropped");
            Uri cropImageUri = Uri.fromFile(cachefile);
            // 解析真实的图片路径
            String path = PhotoUtils.getPath(this, data.getData());
            String realPath = Uri.parse(path).getPath();
            File realfile = new File(realPath);
            Uri newUri = PhotoUtils.createUri(context, realfile);

            PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, 480, 480, GET_PHOTO_FROM_CROP);
        }

    }

    // 获取以及裁剪的图片
    private Bitmap getBitmepAfterCrop(Context context, Uri uri) {
        // 从缓存路径中查找图片
//        File cachefile = new File(getExternalCacheDir(), "face-cropped");
//        Uri cropImageUri = PhotoUtils.createUri(context,cachefile);
//        Uri cropImageUri = Uri.fromFile(cachefile);
        Bitmap bitmap = PhotoUtils.getBitmapFromUri(uri, this);
        if (bitmap == null) {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
            return null;
        }
        return bitmap;
    }


    /**
     * 获取另一个应用分享的文件内容，对于 kitkat 版本，无法通过 ContentResolver 获取文件路径
     * 7.0 以后已经无法在获取文件的真实路径，只能通过获取其文件句柄，然后写入设置的文件夹中
     *
     * @param data
     * @deprecated Android 10 不适用,无法获取路径
     */
    private void getDocument(Intent data) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            String path = PhotoUtils.getPath(this, data.getData());
            Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
            Log.e("FileProvideActivity", "getDocument: " + path);
            int index = path.lastIndexOf("/");
            String substring = path.substring(index + 1, path.length());
            Log.e("FileProvideActivity", "substring: " + substring);
            String realPath = path.replace("file:///", "");
            boolean b = Utils.ExportFileToAnother(new File(realPath), substring);
        } else {
            getDocumentAboveKitKat(data);
        }
    }

    /**
     * Android 4.4 以后，从 ContentResolver 中获取文件内容
     *
     * @param data
     */
    private void getDocumentAboveKitKat(Intent data) {
        Cursor returnCursor = null;
        try {
            Uri returnUri = data.getData();

            if (returnUri == null) {
                Toast.makeText(this, "returnUri 为null", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentResolver contentResolver = getContentResolver();
            String mimeType = contentResolver.getType(returnUri);
            returnCursor =
                    contentResolver.query(returnUri, null, null, null, null);

            if (returnCursor == null) {
                Toast.makeText(this, "returnCursor 为null ", Toast.LENGTH_SHORT).show();
                return;
            }
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            int document_id = returnCursor.getColumnIndex("document_id");
            returnCursor.moveToFirst();
            String fileName = returnCursor.getString(nameIndex);    // 文件名，有后缀 如  mydocument.pdf
            if (document_id > 0) {
                // 不要使用 document_id 列，因为其会对不同的文件返回不同的东西，如图片会是 image:60， 文件
                // 可能为  raw:/storage/emulated/0/Download/5.docx 或者 primary:Android/data/.nomedia 亦或 documentId:primary:Movies/1.xlsx
                // 读取没有权限的文件会直接 crash
                String documentId = returnCursor.getString(document_id);
            }

            Log.e("FileProvider", "nameIndex:" + nameIndex + "  mimeType:" + mimeType);
            // android 7.0 只能获取文件描述符进行读写
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(returnUri, "r");
            if (parcelFileDescriptor == null) {
                FileDescriptor fd = parcelFileDescriptor.getFileDescriptor();

                boolean b = Utils.ExportFileToAnotherByFileDescriptor(fd, fileName);
                if (b) {
                    Toast.makeText(this, "文件已导出至：" + Environment.getExternalStorageDirectory() + File.separator + "Temp", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "文件导出失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "parcelFileDescriptor 为 null 访问文件失败", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "访问文件失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (returnCursor != null)
                returnCursor.close();
        }
    }

// 打开文件


}