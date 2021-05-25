package com.example.taskdispatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * 启动优化之启动器： https://blog.csdn.net/my_csdnboke/article/details/104131797
 */
public class MainActivity extends AppCompatActivity {

    /*3.在代码中将主题设置回来*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}