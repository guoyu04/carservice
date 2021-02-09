package com.metasequoia.manager.system.brightness;

import android.content.Context;

import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.system.setter.SettingStateListener;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.system.state.BooleanState;
import com.metasequoia.manager.system.state.BrightnessMode;

/**
 * 自动亮度字段设置
 */
public class BrightnessAutoSetterSender extends SettingStateListener<BooleanState> {
    public BrightnessAutoSetterSender(Context context) {
        super(context, SettingsConfig.SETTINGS_BRIGHTNESS_AUTO, BooleanState.TRUE);
        //TODO
    }

    public BooleanState getBrightnessAuto(){
        return getState();
    }

    public void setBrightnessAudo(BooleanState state){
        setState(state);
    }
}
