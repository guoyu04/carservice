package com.metasequoia.manager.system.brightness;

import android.content.Context;

import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.system.setter.SettingStateListener;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.system.state.BooleanState;
import com.metasequoia.manager.system.state.BrightnessMode;

/**
 * 白天/黑夜模式切换设置
 */
public class BrightnessModeSetterSender extends SettingStateListener<BrightnessMode> {
    public static final String TAG = "BrightnessMode";
    public BrightnessModeSetterSender(Context context) {
        super(context, SettingsConfig.SETTINGS_BRIGHTNESS_MODE, BrightnessMode.DAYTIME);
        //TODO
    }

    public BrightnessMode getBrightnessMode(){
        return getState();
    }

    public void setBrightnessMode(BrightnessMode mode){
        setState(mode);
    }
}
