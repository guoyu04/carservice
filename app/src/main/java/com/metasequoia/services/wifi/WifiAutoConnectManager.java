package com.metasequoia.services.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.metasequoia.services.mcu.McuSysManager;

import java.util.List;

public class WifiAutoConnectManager {

    private static final String TAG = "WifiAutoConnect";
    private static final int  WIFI_OPENED = 0;
    private static final int WIFI_ACCOUNT_CHANGED = 1;
    private static boolean connectFlag = false;

    private static WifiAutoConnectManager mWifiAutoConnectManager;

    private WifiManager mWifiManager;
    private Context mContext;
    Handler mWifiHandler;
    SharedPreferences sp;

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                String action = intent.getAction();
                if(action != null && WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            Log.i(TAG, "wifi is disabled");
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            Log.i(TAG, "wifi is enabled");
                            abortBroadcast();
                            getWifiAccount();
                            break;
                    }
                }
            }
        }
    };


    public static WifiAutoConnectManager getInstance() {
        if(mWifiAutoConnectManager == null) {
            synchronized (WifiAutoConnectManager.class) {
                mWifiAutoConnectManager = new WifiAutoConnectManager();
            }
        }
        return mWifiAutoConnectManager;
    }

    private WifiAutoConnectManager() {}

    public void init(Context context) {

        mContext = context;

        mWifiHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WIFI_OPENED:
                        getWifiAccount();
                        break;
                    case WIFI_ACCOUNT_CHANGED:
                        getWifiAccount();
                        break;
                }
            }
        };

        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        sp = mContext.getSharedPreferences("wifi_config", Context.MODE_PRIVATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(wifiStateReceiver, filter);

        openWifi();

    }

    public void destory() {
        mContext.unregisterReceiver(wifiStateReceiver);
        mWifiHandler.removeCallbacksAndMessages(null);
    }

    private WifiConfiguration checkSSID(String SSID) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for(WifiConfiguration config : configs) {
            if(config.SSID.equals(SSID)) {
                Log.i(TAG, "SSID:" + SSID + " is exist");
                return config;
            }
        }
        return null;
    }

    /**
     * Create Wifi Configuration
     * @param WifiAccount
     * @param WifiPasswd
     * @param type
     * @return
     */
    private WifiConfiguration CreateWifiConfig(String WifiAccount, String WifiPasswd, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + WifiAccount + "\"";
        WifiConfiguration mWifiConfiguration = checkSSID(config.SSID);
        if(mWifiConfiguration != null) {
            mWifiManager.removeNetwork(mWifiConfiguration.networkId);
        }

        /** wifi has no passwd **/
        if(type == 1) {
            Log.i(TAG, "create wifi configuration no passwd");
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if(type == 2) {  //wep
            Log.i(TAG, "create wifi configuration wep");
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + WifiPasswd + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if(type == 3) {  //wps
            Log.i(TAG, "create wifi configuration wps");
            config.preSharedKey = "\"" + WifiPasswd + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private void connectWifi(String WifiAccount, String WifiPasswd) {
        WifiConfiguration config = CreateWifiConfig(WifiAccount, WifiPasswd, 3);
        int id = mWifiManager.addNetwork(config);
        Log.i(TAG, "config id:" + id);
        mWifiManager.enableNetwork(id, true);
        mWifiManager.saveConfiguration();

        connectFlag = true;
        saveLocalWifiAcccount(WifiAccount, WifiPasswd);
    }

    private void saveLocalWifiAcccount(String WifiAccount, String WifiPasswd) {
        sp.edit().putString("wifi_account", WifiAccount).commit();
        sp.edit().putString("wifi_passwd", WifiPasswd).commit();
    }

    private void getLocalWifiAccount() {
        String wifiAccount = sp.getString("wifi_account", "");
        String wifiPasswd = sp.getString("wifi_passwd", "");
        if(wifiAccount != "" && wifiPasswd != "") {
            connectWifi(wifiAccount, wifiPasswd);
        }
    }

    /**
     * get wifi Account and Passwd
     * Account and Passwd is asynchronous, and maybe got Null
     */
    private void getWifiAccount() {
        String WifiAccount = McuSysManager.getInstance().getWifiAccount();
        String WifiPasswd = McuSysManager.getInstance().getWifiPwd();

        Log.i(TAG, "getWifiAccount and passwd:" + WifiAccount + "," + WifiPasswd);
        if(WifiAccount != "" && WifiPasswd!= "") {
            mWifiHandler.removeMessages(WIFI_ACCOUNT_CHANGED);
            connectWifi(WifiAccount, WifiPasswd);
        } else {

            if(!connectFlag)
                getLocalWifiAccount();

            //mWifiHandler.removeMessages(WIFI_ACCOUNT_CHANGED);
            mWifiHandler.sendEmptyMessageDelayed(WIFI_ACCOUNT_CHANGED, 1000);
        }
    }

    /**
     * open wifi, if opned already,do nothing
     */
    private void openWifi() {
        Log.i(TAG, "open wifi...");
        if(!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        } else {
            getWifiAccount();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (mNetworkInfo != null && mNetworkInfo.isConnected());
    }

    public void setWifiAccount(String account) {
        if(account != null) {
            String passwd = McuSysManager.getInstance().getWifiPwd();
            if(passwd != null) {
                mWifiHandler.sendEmptyMessageDelayed(WIFI_ACCOUNT_CHANGED, 500);
            }
        }
    }

    public void setWifiPwd(String pwd) {
        if(pwd != null) {
            String account = McuSysManager.getInstance().getWifiAccount();
            if(account != null) {
                mWifiHandler.sendEmptyMessageDelayed(WIFI_ACCOUNT_CHANGED, 500);
            }
        }
    }
}
