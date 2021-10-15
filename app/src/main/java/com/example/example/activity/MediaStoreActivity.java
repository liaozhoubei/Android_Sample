package com.example.example.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.example.R;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Android10图片管理器示例
 */
public class MediaStoreActivity extends AppCompatActivity {
    private static final String TAG = "MediaStoreActivity";
    private RecyclerView rvMedia;
    private ArrayList<Uri> uriArrayList;
    private ImageView ivTestImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store);

        initView();
        rvMedia.setLayoutManager(new GridLayoutManager(this, 3));

        ContentResolver contentResolver = this.getContentResolver();
        uriArrayList = new ArrayList<>();

        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                // android 10 不可用
//            firstImgUri = Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
                long id = cursor.getLong(idColumn);
                // 通过id 查询器uri
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);
                uriArrayList.add(uri);
                if (uriArrayList.size() > 3) {
                    break;
                }
                Log.e(TAG, "img cursor data=" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        + ";\nimg cursor type=" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
            }
        }

        ImageAdapter imageAdapter = new ImageAdapter();
        imageAdapter.setDataList(uriArrayList);
        rvMedia.setAdapter(imageAdapter);
    }


    public void dumpImageMetaData(Uri uri) {

        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
    }


    private void initView() {
        rvMedia = (RecyclerView) findViewById(R.id.rv_media);
        ivTestImg = (ImageView) findViewById(R.id.iv_test_img);
    }

    public void test(View view) {
        Bitmap image = null;
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
//            ContentProviderClient providerClient = context.getContentResolver().acquireContentProviderClient(uri);
//            ParcelFileDescriptor descriptor = providerClient.openFile(uri, "r");
//            Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.INTERNAL_CONTENT_URI,144588);
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uriArrayList.get(0), "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            ivTestImg.setImageBitmap(image);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (parcelFileDescriptor != null)
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        Log.e(TAG, "test: over---");
    }

    class ImageAdapter extends RecyclerView.Adapter<RecyclerHolder> {
        private List<Uri> dataList = new ArrayList<>();

        public void setDataList(List<Uri> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MediaStoreActivity.this).inflate(R.layout.item_media, parent, false);
            return new RecyclerHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
            try {
                Uri uri = dataList.get(position);
                holder.imageView.setImageURI(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        private RecyclerHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }
}
