package com.metasequoia.manager.system.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.metasequoia.manager.system.base.sender.BroadcastListener;

import java.util.List;


public class WifiStateBroadcastReciver extends BroadcastListener<IWifiStateCallBack> {

    public WifiStateBroadcastReciver(Context context,String action) {
        super(context, action);
    }

    @Override
    public void onProcessBroadcast(Intent intent) {
        List<IWifiStateCallBack> listeners = getProcessListeners();
        for (IWifiStateCallBack callBack : listeners) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            callBack.onWifiStateChange(WifiState.doTranslateValue(state));
        }
    }


}
