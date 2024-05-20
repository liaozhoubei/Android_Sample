package com.example.studyservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.studyservice.aidlclient.AIDLClientActivity;
import com.example.studyservice.binderpool.BinderPoolActivity;
import com.example.studyservice.callbackservice.SecondActivity;
import com.example.studyservice.messageservice.MessengerActivity;
import com.example.studyservice.service.StartAndBindServiceActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStartBindActivity;
    private Button btnCallbaceActivity;
    private Button btnAidlActivity;
    private Button messagerActivity;
    private Button binderpoolActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnStartBindActivity = (Button) findViewById(R.id.btn_start_bind_activity);
        btnCallbaceActivity = (Button) findViewById(R.id.btn_callback_activity);
        btnAidlActivity = (Button) findViewById(R.id.btn_aidl_activity);

        btnStartBindActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StartAndBindServiceActivity.class));
            }
        });

        btnCallbaceActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        btnAidlActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AIDLClientActivity.class));
            }
        });
        messagerActivity = (Button) findViewById(R.id.messager_activity);
        messagerActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MessengerActivity.class));
            }
        });
        binderpoolActivity = (Button) findViewById(R.id.binderpool_activity);
        binderpoolActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BinderPoolActivity.class));
            }
        });
    }
}
