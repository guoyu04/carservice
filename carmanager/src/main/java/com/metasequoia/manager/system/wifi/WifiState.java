package com.metasequoia.manager.system.wifi;

import android.net.wifi.WifiManager;

import com.metasequoia.manager.system.state.IntState;

public enum WifiState implements IntState<WifiState> {
    WIFI_STATE_UNKNOWN(WifiManager.WIFI_STATE_UNKNOWN),
    WIFI_STATE_DISABLED(WifiManager.WIFI_STATE_DISABLED),
    WIFI_STATE_DISABLING(WifiManager.WIFI_STATE_DISABLING),
    WIFI_STATE_ENABLED (WifiManager.WIFI_STATE_ENABLED),
    WIFI_STATE_ENABLING(WifiManager.WIFI_STATE_ENABLING);

    private int mValue = -1;

    WifiState(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    @Override
    public WifiState translateValue(int value) {
        return doTranslateValue(value);
    }

    public static WifiState doTranslateValue(int value) {
        if (value == WIFI_STATE_DISABLED.getValue()) return WIFI_STATE_DISABLED;
        if (value == WIFI_STATE_DISABLING.getValue()) return WIFI_STATE_DISABLING;
        if (value == WIFI_STATE_ENABLED.getValue()) return WIFI_STATE_ENABLED;
        if (value == WIFI_STATE_ENABLING.getValue()) return WIFI_STATE_ENABLING;
        return WIFI_STATE_UNKNOWN;
    }
}
