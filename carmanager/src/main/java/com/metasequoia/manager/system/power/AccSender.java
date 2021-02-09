package com.metasequoia.manager.system.power;

import android.content.Context;

import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.system.setter.SettingStateListener;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.system.state.BooleanState;

public class AccSender extends SettingStateListener<BooleanState> {
    public AccSender(Context context) {
        super(context, SettingsConfig.SETTINGS_ACC, BooleanState.TRUE);
    }

    public BooleanState getAccState(){
        return getState();
    }
}