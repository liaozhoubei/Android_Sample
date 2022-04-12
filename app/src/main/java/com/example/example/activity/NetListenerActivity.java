package com.example.example.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.R;
import com.example.example.receiver.NetWorkConnectChangedReceiver;

/**
 * https://blog.csdn.net/qq_20451879/article/details/106124725
 */
public class NetListenerActivity extends AppCompatActivity {

    private TextView mTvWifistate;
    private StringBuffer sb = new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_listener);
        initView();
        NetWorkConnectChangedReceiver netWorkConnectChangedReceiver = new NetWorkConnectChangedReceiver();
        netWorkConnectChangedReceiver.setWifiState(new NetWorkConnectChangedReceiver.WifiState() {
            @Override
            public void OnWifiStateChange(int type, boolean isConnected) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateNetState(isConnected, type);
                    }
                });
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkConnectChangedReceiver, intentFilter);
    }

    private void updateNetState(boolean isConnected, int type) {
        String connect = isConnected ? "链接" : "断开";
        if (type == 1) {
            sb.append("以太网:" + connect);
            sb.append("1");
            sb.append("\n");
        } else if (type == 2) {
            sb.append("wifi:" + connect);
            sb.append("2");
            sb.append("\n");
        } else if (type == 0) {
            sb.append("网络不可用:" + connect);
            sb.append("0");
            sb.append("\n");
        } else {
            sb.append("无状态:" + connect);
            sb.append("0");
            sb.append("\n");
        }
        mTvWifistate.setText(sb.toString());
    }

    private void initView() {
        mTvWifistate = (TextView) findViewById(R.id.tv_wifistate);
    }
}