package com.example.example.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * 获取网络状态
 * 判断当前网络有没有联网
 * 并且判断是有线还是无线
 * api 21 可用，以太网和wifi 同时链接时，拔掉以太网无法无法检测到wifi
 */
public class NetWorkUtils {
    private static String TAG = "NetWorkUtils";
    private final Context mContext;
    private ConnectivityManager mConnectivityManager;
    private WifiState mWifiState;

    public NetWorkUtils(Context context, WifiState wifiState) {
        mContext = context;
        mWifiState = wifiState;


    }

    public void registerNewWorkCallaback(){
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mWifiState!= null){
            int type = getNetWorkType();
            mWifiState.OnWifiStateChange(type);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            mConnectivityManager.registerNetworkCallback(request,mNetworkCallback);
        }
    }

    public void unRegisterNewWorkCallback(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);

            int type = getNetWorkType();
            Toast.makeText(mContext, "onAvailable", Toast.LENGTH_SHORT).show();
            mWifiState.OnWifiStateChange(type);
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            int netWorkType = getAvailableNetWorkType();
            Log.e(TAG, "onLost: " + netWorkType);
            Toast.makeText(mContext, "state:"+ netWorkType, Toast.LENGTH_SHORT).show();
            mWifiState.OnWifiStateChange(netWorkType);
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            // 表明此网络连接成功验证
//            NetworkCapabilities.NET_CAPABILITY_VALIDATED  api 23 可用
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                Log.e(TAG, "onCapabilitiesChanged: "  );
            Toast.makeText(mContext, "onCapabilitiesChanged", Toast.LENGTH_SHORT).show();
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                    // 使用WI-FI
                    mWifiState.OnWifiStateChange(2);
                    Log.e(TAG, "onCapabilitiesChanged: 2"  );
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    // 使用以太网
                    mWifiState.OnWifiStateChange(1);
                    Log.e(TAG, "onCapabilitiesChanged: 1"  );
                } else {
                    // 未知网络，包括蓝牙、VPN、LoWPAN
                    Log.e(TAG, "onCapabilitiesChanged: no"  );
                }
            }
        }
    };

    /**
     *
     * @return 1 为以太网络， 2 为无线网络 ， 0 网络不可用
     */
    private int getNetWorkType(){
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null) {
            return 0;
        }
        boolean iscon = info.isAvailable();
        Log.d(TAG, "网络连接 =" + iscon + "，连接方式：" + info.getType() + " ," + info.getTypeName());
        if (!iscon) {
            return 0;
        }
        if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
            return 1;
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            return 2;
        } else {
            return 0;
        }
    }

    private int getAvailableNetWorkType(){
        NetworkInfo ethNetInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            // 以太网可用
            return 1;
        }else if (wifiInfo != null && wifiInfo.isConnected()){
            // wifi 可用
            return 2;
        }else {
            return 0;
        }

    }

    public void setWifiState(WifiState wifiState) {
        mWifiState = wifiState;
    }

    public interface WifiState{
        void OnWifiStateChange(int type);
    }
}
