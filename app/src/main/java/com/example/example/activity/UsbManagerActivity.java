package com.example.example.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.R;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileInputStream;
import me.jahnen.libaums.core.partition.Partition;


/**
 * usb 库
 * https://github.com/magnusja/libaums
 * 参考资料：
 * https://blog.csdn.net/woshiliuzemin/article/details/88531427
 * https://zhuanlan.zhihu.com/p/44973627
 * https://blog.csdn.net/UserNamezhangxi/article/details/108632199
 * https://blog.csdn.net/qq_29924041/article/details/80141514
 * 读取U盘文件存在的问题：
 * 1.解析apk应用包后，拔出U盘对导致崩溃，初步判断是由于资源引用的问题，暂未查到解决方法
 */
public class UsbManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ACTION_USB_PERMISSION = "com.example.example.USB_PERMISSION";
    private Context mContext;
    static String TAG = "UsbManagerActivity";
    UsbMassStorageDevice[] storageDevices;
    PendingIntent mPendingIntent;
    private Button mBtnDetectUsb;
    private TextView mTvFileListTitle;
    private ListView mLvFileList;
    private ArrayList<MyUsbFile> mFilelist;
    private UsbFileAdapter adapter;
    private UsbFile mUsbFileRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_manager);
        mContext = this;
        initView();
        initdata();
    }



    private void initView() {
        mBtnDetectUsb = (Button) findViewById(R.id.btn_detect_usb);
        mTvFileListTitle = (TextView) findViewById(R.id.tv_file_list_title);
        mLvFileList = (ListView) findViewById(R.id.lv_file_list);

        mBtnDetectUsb.setOnClickListener(this);

        mTvFileListTitle.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_detect_usb:
                readDeviceList();
                break;
            case R.id.tv_file_list_title:
                if (mUsbFileRoot!= null){
                    UsbFile parent = mUsbFileRoot.getParent();
                    if (parent!= null){
                        readFile(parent);
                    }
                }
                break;
            default:
                break;
        }
    }


    private void initdata() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        String absolutePath = externalStorageDirectory.getAbsolutePath();
//        currentPath = absolutePath;
        mTvFileListTitle.setText("/");
        mFilelist = new ArrayList<>();
        adapter = new UsbFileAdapter(mFilelist);
        mLvFileList.setAdapter(adapter);
        mLvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyUsbFile item = adapter.getItem(position);
                boolean directory = item.file.isDirectory();
                if (directory) {
                    readFile(item.file);
                } else {
                    long length = item.file.getLength();
                    if (length < 1024 * 1024 * 1024 * 5) {
                        Log.i(TAG, item.file.getName());
                        Toast.makeText(getApplication(), "start copy file", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "run: start copy file");
                                readTxtFromUDisk(item.file);
                            }
                        }).start();
                    } else {
                        Toast.makeText(getApplication(), "file is too big", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void readDeviceList() {
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        returnMsg("开始去读Otg设备");
        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(mContext);
        // TODO: 2023/8/22 未验证 
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_UPDATE_CURRENT);
        if (storageDevices.length == 0) {
            returnMsg("没有检测到U盘s");
            return;
        }
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbManager.hasPermission(device.getUsbDevice())) {
                returnMsg("检测到有权限，延迟1秒开始读取....");
//                try {
//                    Thread.sleep(1000 );
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                readDevice(device);
            } else {
                returnMsg("检测到有设备，但是没有权限，申请权限....");
                usbManager.requestPermission(device.getUsbDevice(), mPendingIntent);
            }
        }
    }

    private void returnMsg(String s) {
        Log.e(TAG, s);
    }

    private void readFile(UsbFile root) {
        mUsbFileRoot = root;
        mTvFileListTitle.setText(root.getAbsolutePath());
        ArrayList<UsbFile> mUsbFiles = new ArrayList<>();
        try {
            for (UsbFile file : root.listFiles()) {
                mUsbFiles.add(file);
            }
            Collections.sort(mUsbFiles, new Comparator<UsbFile>() {//简单排序 文件夹在前 文件在后
                @Override
                public int compare(UsbFile oFile1, UsbFile oFile2) {
                    if (oFile1.isDirectory()) return -1;
                    else return 1;
                }
            });
            mFilelist.clear();
            for (UsbFile f : mUsbFiles) {
                mFilelist.add(new MyUsbFile(f, f.getName()));
            }
            adapter.notifyDataSetChanged();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readDevice(UsbMassStorageDevice device) {
        try {
            device.init();
            Partition partition = device.getPartitions().get(0);
            Log.i(TAG, "------------partition---------");
            Log.i(TAG, "VolumnLobel:" + partition.getVolumeLabel());
            Log.i(TAG, "blockSize:" + partition.getBlockSize() + "");
            FileSystem currentFs = partition.getFileSystem();
            Log.i(TAG, "------------FileSystem---------");
            UsbFile root = currentFs.getRootDirectory();
            String deviceName = currentFs.getVolumeLabel();
            Log.i(TAG, "volumnLable:" + deviceName);
            Log.i(TAG, "chunkSize:" + currentFs.getChunkSize());
            Log.i(TAG, "freeSize:" + currentFs.getFreeSpace());
            Log.i(TAG, "OccupiedSpcace:" + currentFs.getOccupiedSpace());
            Log.i(TAG, "capacity" + currentFs.getCapacity());
            Log.i(TAG, "rootFile:" + root.toString());
            returnMsg("正在读取U盘" + deviceName);
            readFile(root);
        } catch (IOException e) {
            e.printStackTrace();
            returnMsg("读取失败:" + e.getMessage());
        } finally {
        }
    }


    /**
     * 读取U盘文件的内容
     *
     * @param usbFile
     */
    private void readTxtFromUDisk(UsbFile usbFile) {
        UsbFile descFile = usbFile;
        //读取文件内容
        InputStream is = new UsbFileInputStream(descFile);
        //读取秘钥中的数据进行匹配
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            File parentFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            File file = new File(parentFile, usbFile.getName());
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            Log.e(TAG, "readTxtFromUDisk: " + file.getAbsolutePath());

            bufferedInputStream = new BufferedInputStream(is);
            fileOutputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = bufferedInputStream.read(bytes)) != -1) {
                Log.e(TAG, "readTxtFromUDisk: len = " + len);
                fileOutputStream.write(bytes, 0, len);
            }
            fileOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    void close() {
        for (UsbMassStorageDevice s:storageDevices){
//            if (s.getUsbDevice() == mUsbDeviceRemove)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    s.getPartitions().stream().close();
                }
        }
    }


    private void registerUsbState(Context context) {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        usbFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        usbFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        usbFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
//        usbFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        usbFilter.addDataScheme("file");

        registerReceiver(mSdcardReceiver, usbFilter);
    }

    private void registerUsbPermission(Context context){
        IntentFilter usbFilter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbPermissionReceiver, usbFilter);
    }

    BroadcastReceiver mUsbPermissionReceiver  = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_USB_PERMISSION) ){
                readDeviceList();
            }
        }
    };

    /**
     * 检测U盘插拔的广播，此方法的问题在于只能再插拔时获取路径，已在设备上的无法找到
     */
    BroadcastReceiver mSdcardReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive: mSdcardReceiver "+ intent);
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) ) {
                Log.e(TAG, "onReceive: " + intent.getDataString());
                // 获取到具体的路径
                String path = intent.getDataString().split("file://")[1];
                Log.e(TAG, "onReceive: path :" + path );
                Log.e(TAG, "onReceive: getData.getPath : "+ intent.getData().getPath());
                Toast.makeText(context, "path:" + intent.getData().getPath(), Toast.LENGTH_SHORT).show();
                readDeviceList();
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                Log.e(TAG, "remove ACTION_MEDIA_REMOVED");
            }else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTABLE)){
                Log.e(TAG, "remove ACTION_MEDIA_UNMOUNTABLE");
            }else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)){
                Log.e(TAG, "remove ACTION_MEDIA_UNMOUNTED");
            }else if (action.equals(Intent.ACTION_MEDIA_EJECT)){
                Log.e(TAG, "remove ACTION_MEDIA_EJECT");
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        registerUsbState(this);
        registerUsbPermission(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSdcardReceiver);
        unregisterReceiver(mUsbPermissionReceiver);
    }

    class MyUsbFile {
        private UsbFile file;
        private String fileName;

        public MyUsbFile(UsbFile file, String fileName) {
            this.file = file;
            this.fileName = fileName;
        }

        public UsbFile getFile() {
            return file;
        }

        public void setFile(UsbFile file) {
            this.file = file;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    class UsbFileAdapter extends BaseAdapter {

        List<MyUsbFile> myFileList;

        public UsbFileAdapter(List<MyUsbFile> myFileList) {
            this.myFileList = myFileList;
        }

        @Override
        public int getCount() {
            return myFileList.size();
        }

        @Override
        public MyUsbFile getItem(int position) {
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
                convertView = LayoutInflater.from(UsbManagerActivity.this).inflate(R.layout.item_list, parent, false);
                holder = new Holder();
                holder.textView = convertView.findViewById(R.id.tv_item_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            MyUsbFile item = getItem(position);
            holder.textView.setText(item.getFileName());
            return convertView;
        }

        class Holder {
            TextView textView;
        }
    }
}