package com.metasequoia.manager.system.brightness;

import android.content.Context;
import android.provider.Settings;

import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.system.base.sender.ISender;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.system.setter.SettingsIntListener;
import com.metasequoia.manager.system.state.BooleanState;
import com.metasequoia.manager.system.state.BrightnessMode;

/**
 * 黑夜模式设置类
 * Created by guoyu on 2020/8/12.
 */
public class BrightnessNightSetterSender extends SettingsIntListener implements ISender<Integer> {
    public static final String TAG = "BrightnessNightSetter";
    /**
     * 当前白天/黑夜模式
     */
    private BrightnessMode mBrightnessMode = BrightnessMode.DAYTIME;
    private BooleanState mAutoBrightness = BooleanState.TRUE;

    public BrightnessNightSetterSender(Context context) {
        super(context, SettingsConfig.SETTINGS_BRIGHTNESS_NIGHT);
    }

    @Override
    public void send(Integer arg) {
        this.setValue(arg);
    }

    public void setBrightness(int value){

        if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
            //TODO
        }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
            //TODO
        }else {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
        }
        if(mBrightnessMode == BrightnessMode.NIHGT){
            send(value);
        }
    }

    public int getBrightness(int defValue){
        return getValue(defValue);
    }

    public void setBrightnessMode(BrightnessMode state){
        mBrightnessMode = state;
    }

    public void setBrightnessAuto(BooleanState state){
        mAutoBrightness = state;
    }

}
