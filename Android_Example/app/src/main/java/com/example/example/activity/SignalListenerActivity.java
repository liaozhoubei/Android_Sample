package com.example.example.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import static java.sql.DriverManager.println;

import com.example.example.R;

/**
 * 手机信号，wifi 信号，蓝牙状态监听
 */
public class SignalListenerActivity extends AppCompatActivity {
    private String TAG = "SignalListener";
    private TextView mTvSignal;
    private StringBuilder stringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_listener);
        mTvSignal = findViewById(R.id.tv_signal);
        stringBuilder = new StringBuilder();

        registerBluetoothReceive();
        phoneStateListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            registerWifiCallback();
        }
    }

    private void registerBluetoothReceive() {
        // 初始化广播
        IntentFilter intentFilter = new IntentFilter();
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        // 注册广播
        registerReceiver(bluetoothReceive, intentFilter);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            int state = bluetoothAdapter.getState();
            Log.d(TAG, "registerBluetoothReceive: " + state);
            stringBuilder.append("BluetoothReceive:");
            stringBuilder.append(state);
            stringBuilder.append("\n");
        }
    }

    private void setText(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvSignal.setText(text);
            }
        });
    }

    private String getString(String str, Object o) {
        stringBuilder.append(str);
        stringBuilder.append(o);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    // 手机网络监听
    private void phoneStateListener() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(new PhoneStateListener() {

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                int level = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    level = signalStrength.getLevel();
                }
                Log.d(TAG, "onSignalStrengthsChanged: level " + level);
                setText(getString("phone:onSignalStrengthsChanged: level: ", level));
            }

            @Override
            public void onServiceStateChanged(ServiceState state) {
                Log.d(TAG, "onServiceStateChanged: " + state.toString());
//                STATE_IN_SERVICE
//                STATE_OUT_OF_SERVICE
//                STATE_EMERGENCY_ONLY
//                STATE_POWER_OFF
                setText(getString("phone:onServiceStateChanged:", state.getState()));
            }

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                Log.d(TAG, "onDataConnectionStateChanged: state " + state + " networkType " + networkType);
                switch (state) {
                    case TelephonyManager.DATA_DISCONNECTED: {
                        // 数据网络端口，ip通信不可用
                        setText(getString("phone:onDataConnectionStateChanged:", "TelephonyManager.DATA_DISCONNECTED"));
                        break;
                    }
                    case TelephonyManager.DATA_CONNECTING: {
                        // 数据连接状态：正在建立数据连接
                        setText(getString("phone:onDataConnectionStateChanged:", "TelephonyManager.DATA_CONNECTING"));
                        break;
                    }
                    case TelephonyManager.DATA_CONNECTED: {
                        // 数据连接状态：已连接。 IP 流量应该可用
                        setText(getString("phone:onDataConnectionStateChanged:", "TelephonyManager.DATA_CONNECTED"));
                        break;
                    }
                    case TelephonyManager.DATA_SUSPENDED: {
                        // 数据连接状态：暂停。连接已建立，但 IP 通信暂时不可用。
                        // 例如，在 2G 网络中当语音呼叫到达时，数据活动可能会暂停
                        setText(getString("phone:onDataConnectionStateChanged:", "TelephonyManager.DATA_SUSPENDED"));
                        break;
                    }
                    default:
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE | PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    /**
     * <p>Must hold {@code <uses-permission android:name="android.permission.BLUETOOTH" />}</p>
     * <p>Must hold {@code <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />}</p>
     */
    BroadcastReceiver bluetoothReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON: {
                        Log.d(TAG, "蓝牙正在打开");
                        setText(getString("bluetoothReceive:", "蓝牙正在打开"));
                        break;
                    }
                    case BluetoothAdapter.STATE_ON: {
                        Log.d(TAG, "蓝牙已经打开");
                        setText(getString("bluetoothReceive:", "蓝牙已经打开"));
                        break;
                    }
                    case BluetoothAdapter.STATE_TURNING_OFF: {
                        Log.d(TAG, "蓝牙正在关闭");
                        setText(getString("bluetoothReceive:", "蓝牙正在关闭"));
                        break;
                    }
                    case BluetoothAdapter.STATE_OFF: {
                        Log.d(TAG, "蓝牙已经关闭");
                        setText(getString("bluetoothReceive:", "蓝牙已经关闭"));
                        break;
                    }
                    default:
                        break;
                }
            } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                Log.d(TAG, "蓝牙设备已连接");
                setText(getString("bluetoothReceive:", "蓝牙设备已连接"));
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.d(TAG, "蓝牙设备已断开");
                setText(getString("bluetoothReceive:", "蓝牙设备已断开"));
            }
        }
    };

    /**
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerWifiCallback() {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull @NotNull Network network) {
                super.onAvailable(network);
                Log.d(TAG, "onAvailable: " + network);
                setText(getString("onAvailable:", network));
            }

            @Override
            public void onLosing(@NonNull @NotNull Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
                Log.d(TAG, "onLosing: " + network);
                setText(getString("onLosing:", network));
            }

            @Override
            public void onLost(@NonNull @NotNull Network network) {
                super.onLost(network);
                Log.d(TAG, "onLost: " + network);
                setText(getString("onLost:", network));
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                Log.d(TAG, "onUnavailable: ");
                setText(getString("onUnavailable:", ""));
            }

            @Override
            public void onCapabilitiesChanged(@NonNull @NotNull Network network, @NonNull @NotNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                Log.d(TAG, "onCapabilitiesChanged: " + network);
                setText(getString("onCapabilitiesChanged:", network));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    int signalStrength = networkCapabilities.getSignalStrength();
                    Log.d(TAG, "onCapabilitiesChanged:signalStrength: " + signalStrength);
                    setText(getString("onCapabilitiesChanged:signalStrength：", signalStrength));
                }

                // 表明此网络连接成功验证
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                    ) {
                        // 使用WI-FI
                        int wifiLevel = getWifiLevel();
                        setText(getString("onCapabilitiesChanged:", wifiLevel));
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    ) {
                        // 使用蜂窝网络
                    } else {
                        // 未知网络，包括蓝牙、VPN、LoWPAN
                    }
                }
            }

            @Override
            public void onLinkPropertiesChanged(@NonNull @NotNull Network network, @NonNull @NotNull LinkProperties linkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties);
                Log.d(TAG, "onLinkPropertiesChanged: " + network);
                setText(getString("onLinkPropertiesChanged:", network));
            }

            @Override
            public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
                super.onBlockedStatusChanged(network, blocked);
                Log.d(TAG, "onBlockedStatusChanged: " + network);
                setText(getString("onBlockedStatusChanged:", network));
            }
        });
    }

    /**
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     */
    private int getWifiLevel() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int signalLevel = -1;
        if (wifiInfo.getBSSID() != null) {
            //wifi名称
            String ssid = wifiInfo.getSSID();
            //wifi信号强度
            signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
            //wifi速度
            int speed = wifiInfo.getLinkSpeed();
            //wifi速度单位
            String units = WifiInfo.LINK_SPEED_UNITS;
            String info = "ssid=" + ssid + ",signalLevel=" + signalLevel + ",speed=" + speed + ",units=" + units;
            Log.d(TAG, "getWifiLevel: " + info);
            setText(getString("getWifiLevel:", info));
        } else {
            // no network currently connected
            signalLevel = -1;
        }
        return signalLevel;
    }

}