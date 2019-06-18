package com.example.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<ActivityBean> activityBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv_mylistview);
        initdata();
    }

    private void initdata() {
        activityBeanList = new ArrayList<>();
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), PackageManager.GET_ACTIVITIES);
            for (ActivityInfo activity : packageInfo.activities) {
                Class aClass = Class.forName(activity.name);

                if (aClass.getSimpleName().equals("PermissionActivity")
                        || aClass.getSimpleName().equals("AppSettingsDialogHolderActivity")
                        || aClass.getSimpleName().equals("MainActivity")) {
                    continue;
                }
                activityBeanList.add(new ActivityBean(aClass));
            }
            MyAdapter myAdapter = new MyAdapter(activityBeanList);
            listView.setAdapter(myAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(MainActivity.this, activityBeanList.get(position).getaClass()));
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
            TextView viewById = convertView.findViewById(R.id.tv_activity_name);
            ActivityBean item = getItem(position);
            viewById.setText(item.getaClass().getSimpleName());
            return convertView;
        }
    }
}
