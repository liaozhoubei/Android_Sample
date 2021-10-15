package com.example.example.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个简单的文件目录浏览器
 */
public class FileListActivity extends AppCompatActivity {

    private static final String TAG = "FileListActivity";
    private TextView mTvFileListTitle;
    private ListView mLvFileList;
    private List<MyFile> mFilelist;
    private MyAdapter adapter;
    private String currentPath; // 现在所处目录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        initView();
        if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q){
            // android 11 管理存储卡示例
            boolean externalStorageManager = Environment.isExternalStorageManager();
            if (!externalStorageManager){
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                finish();
            }else {
                initdata();
                initListener();
            }
        }else {
            initdata();
            initListener();
        }
    }


    private void initdata() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        String absolutePath = externalStorageDirectory.getAbsolutePath();
        currentPath = absolutePath;
        mTvFileListTitle.setText(absolutePath);
        mFilelist = new ArrayList<>();
        List<MyFile> fileList = getFileList(externalStorageDirectory);
        if (fileList != null)
            mFilelist.addAll(fileList);
        adapter = new MyAdapter(mFilelist);
        mLvFileList.setAdapter(adapter);
    }

    /**
     * @param parentFile
     * @return
     * @deprecated 在Android 10 中已经无法使用此方法获取文件目录
     */
    private List<MyFile> getFileList(File parentFile) {
        List<MyFile> list = new ArrayList<>();
        if (parentFile.exists() && parentFile.isDirectory()) {
            File[] files = parentFile.listFiles();
            if (files == null) {
                return null;
            }
            for (File file : files) {
                if (file.exists() && !TextUtils.isEmpty(file.getName())) {
                    list.add(new MyFile(file, file.getName()));
                }
            }
        }
        return list;
    }

    private void initListener() {
        mTvFileListTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(mTvFileListTitle.getText().toString().trim());
                if (!file.exists()) {
                    Toast.makeText(FileListActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                File parentFile = file.getParentFile();
                if (!parentFile.exists() && !parentFile.canRead()) {
                    Toast.makeText(FileListActivity.this, "上级目录不存在或不可读取", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!parentFile.isDirectory()) {
                    Toast.makeText(FileListActivity.this, "上级目录不存在或不可读取", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<MyFile> fileList = getFileList(parentFile);
                if (fileList == null) {
                    Toast.makeText(FileListActivity.this, "上级目录不存在或不可读取", Toast.LENGTH_SHORT).show();
                    return;
                }
                mTvFileListTitle.setText(parentFile.getAbsolutePath());
                mFilelist.clear();
                mFilelist.addAll(fileList);
                adapter.notifyDataSetChanged();
            }
        });

        mLvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyFile item = adapter.getItem(position);
                getFile(item);

            }
        });
    }


    private void getFile(MyFile file) {
        File childfile = file.getFile();
        if (childfile.exists()) {
            // 如果是目录，进入下级目录
            if (childfile.isDirectory()) {
                List<MyFile> fileList = getFileList(childfile);
                if (fileList == null) {
                    Toast.makeText(FileListActivity.this, "上级目录不存在或不可读取", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFilelist.clear();
                mFilelist.addAll(fileList);
                mTvFileListTitle.setText(childfile.getAbsolutePath());
                adapter.notifyDataSetChanged();
            } else {
                if (childfile.canRead()) {
                    // 如果是可读文件，则返回
                    Toast.makeText(this, childfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "getFile: " + childfile.getAbsolutePath());
                    try {
                        FileInputStream fileInputStream = new FileInputStream(childfile.getAbsoluteFile());
                        byte[] b = new byte[1024];
                        fileInputStream.read(b);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "该文件无法读取", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "文件无法读取", Toast.LENGTH_SHORT).show();
        }
    }

    ;

    private void initView() {
        mTvFileListTitle = (TextView) findViewById(R.id.tv_file_list_title);
        mLvFileList = (ListView) findViewById(R.id.lv_file_list);
    }

    class MyFile {
        private File file;
        private String fileName;

        public MyFile(File file, String fileName) {
            this.file = file;
            this.fileName = fileName;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    class MyAdapter extends BaseAdapter {

        List<MyFile> myFileList;

        public MyAdapter(List<MyFile> myFileList) {
            this.myFileList = myFileList;
        }

        @Override
        public int getCount() {
            return myFileList.size();
        }

        @Override
        public MyFile getItem(int position) {
            return myFileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(FileListActivity.this).inflate(R.layout.item_list, parent, false);
                holder = new Holder();
                holder.textView = convertView.findViewById(R.id.tv_item_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            MyFile item = getItem(position);
            holder.textView.setText(item.getFileName());
            return convertView;
        }

        class Holder {
            TextView textView;
        }
    }
}
