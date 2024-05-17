package com.example.example;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    private ListView listView;
    private List<ActivityBean> activityBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONObject jsonObject = new JSONObject();
        listView = findViewById(R.id.lv_mylistview);
        initdata();
    }

    private void initdata() {
        activityBeanList = new ArrayList<>();
        try {
            //-------------------------------------------------------------------------
//            LauncherApps mLauncherApps =(LauncherApps)
//                    Utils.getApp().getSystemService(Context.LAUNCHER_APPS_SERVICE);
//            List<LauncherActivityInfo> activityList = mLauncherApps.getActivityList(getPackageName(), Process.myUserHandle());
//            for (LauncherActivityInfo info:activityList) {
//                ComponentName componentName = info.getComponentName();
//                boolean activityEnabled = mLauncherApps.isActivityEnabled(componentName, Process.myUserHandle());
//                Log.e("MainActivity", "initdata: "+info.getName() +  " activityEnabled:"+ activityEnabled);
//            }
            //----------------------------------------------------
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), PackageManager.GET_ACTIVITIES);
            for (ActivityInfo activityInfo : packageInfo.activities) {
                Class aClass = null;
                try {
                    if (activityInfo.name.contains("PermissionActivity")
                            || activityInfo.name.contains("AppSettingsDialogHolderActivity")
                            || activityInfo.name.contains("MainActivity")
                            || activityInfo.name.contains("LanguageActivity")) {
                        continue;
                    }
                    aClass = Class.forName(activityInfo.name);
                    Log.d(TAG, "loadActivity: " + activityInfo.name );
                    if (activityInfo.name.startsWith("com.example.example")){
                        activityBeanList.add(new ActivityBean(aClass));
                    }
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "error: " + activityInfo.name );
                    e.printStackTrace();
                }

            }
            //-------------------------------------------------------------------------------------
            // 按首字母排序
            Collections.sort(activityBeanList, new Comparator<ActivityBean>() {
                @Override
                public int compare(ActivityBean o1, ActivityBean o2) {
                    return o1.getaClass().getSimpleName().compareTo(o2.getaClass().getSimpleName());
                }
            });
            MyAdapter myAdapter = new MyAdapter(activityBeanList);
            listView.setAdapter(myAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(MainActivity.this, activityBeanList.get(position).getaClass()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    class ActivityBean {
        Class aClass;

        public ActivityBean(Class aClass) {
            this.aClass = aClass;
        }

        public Class getaClass() {
            return aClass;
        }

        public void setaClass(Class aClass) {
            this.aClass = aClass;
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<ActivityBean> list;

        public MyAdapter(List<ActivityBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ActivityBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list, parent, false);
            TextView viewById = convertView.findViewById(R.id.tv_item_name);
            ActivityBean item = getItem(position);
            viewById.setText(item.getaClass().getSimpleName());
            return convertView;
        }
    }
}
