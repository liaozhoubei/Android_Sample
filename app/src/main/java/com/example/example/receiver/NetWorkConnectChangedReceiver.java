package com.example.example.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

public class NetWorkConnectChangedReceiver extends BroadcastReceiver {

    private static final String TAG = NetWorkConnectChangedReceiver.class.getSimpleName();

    private WifiState mWifiState;

    private final int NONE = -1;
    private final int MOBILE = 0;
    private final int ETHERNET = 1;
    private final int WIFI = 2;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            // 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.i(TAG, "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }

        // 监听wifi的连接状态即是否连上了一个有效无线路由
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                // 获取联网状态的NetWorkInfo对象
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                //获取的State对象则代表着连接成功与否等状态
                NetworkInfo.State state = networkInfo.getState();
                //判断网络是否已经连接
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                Log.i(TAG, "isConnected:" + isConnected);
                if (isConnected) {

                } else {

                }
            }
        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    Log.i(TAG, getConnectionType(info.getType()) + " 连上");
                    // 发送UI Event
                    mWifiState.OnWifiStateChange(getConnectionType(info.getType()), true);
                } else {
                    Log.i(TAG, getConnectionType(info.getType()) + " 断开");
                    mWifiState.OnWifiStateChange(getConnectionType(info.getType()), false);
                }
            }
        }
    }

    private int getConnectionType(int type) {
//         1 为以太网络， 2 为无线网络 ， 0 网络不可用
        int connType = -1;
        if (type == ConnectivityManager.TYPE_MOBILE) {
            // 3g网络
            connType = MOBILE;
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = WIFI;
        } else if (type == ConnectivityManager.TYPE_ETHERNET) {
            connType = ETHERNET;
        }
        return connType;
    }

    public void setWifiState(WifiState wifiState) {
        mWifiState = wifiState;
    }

    public interface WifiState {
        void OnWifiStateChange(int type, boolean isConnected);
    }
}
