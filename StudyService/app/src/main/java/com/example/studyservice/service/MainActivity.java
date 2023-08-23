package com.example.studyservice.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.studyservice.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Intent servier = null;
    private connection conn = null;
    private Iservice mybind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startserver = (Button) findViewById(R.id.start_server);
        startserver.setOnClickListener(this);

        Button stopserver = (Button) findViewById(R.id.stop_server);
        stopserver.setOnClickListener(this);

        Button bindserver = (Button) findViewById(R.id.bind_server);
        bindserver.setOnClickListener(this);

        Button bindmethod = (Button) findViewById(R.id.bind_method);
        bindmethod.setOnClickListener(this);

        Button unbindserver = (Button) findViewById(R.id.unbind_server);
        unbindserver.setOnClickListener(this);

        servier = new Intent(this, TestService.class);
        conn = new connection();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.start_server:
                startService(servier);
                Log.i("MainActivity", "开启服务成功");
                System.out.println("MainActivity"+ "开启服务成功");
                Toast.makeText(getApplicationContext(), "开启服务了", Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_server:
                stopService(servier);
                Log.i("MainActivity", "取消服务成功");
                System.out.println("MainActivity"+ "取消服务成功");
                break;
            case R.id.bind_server:
                bindService(servier, conn, BIND_AUTO_CREATE);
                break;
            case R.id.bind_method:
                mybind.calleat();
                break;
            case R.id.unbind_server:
                unbindService(conn);
                System.out.println("MainActivity"+ "取消服务成功按钮");
                break;
        }
    }


    private class connection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mybind = (Iservice) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
