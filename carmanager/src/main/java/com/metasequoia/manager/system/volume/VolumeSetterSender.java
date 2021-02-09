package com.metasequoia.manager.system.volume;

import android.content.Context;
import android.util.Log;

import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.system.base.sender.ISender;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.system.setter.SettingsIntListener;
import com.metasequoia.manager.utils.Utils;

/**
 * 音量设置类
 * Created by guoyu on 2020/8/12.
 */
public class VolumeSetterSender extends SettingsIntListener implements ISender<Integer>, IVolumeModel {
    public static final String TAG = "VolumeSetterSender";
    public VolumeSetterSender(Context context) {
        super(context, SettingsConfig.SETTINGS_VOLUME);
        //TODO  INIT
    }

    /**
     * 执行设置到数据库操作
     * @param value 音量值
     */
    @Override
    public void send(Integer value) {
        this.setValue(value);
    }

    /**
     * 设置音量
     * @param value 音量值
     * @param flag FLAG_SHOW_IU
     */
    @Override
    public void setVolume(int value, int flag) {
        value = Utils.normalizeValue(value, 0, 100);
        value = value*SettingsConfig.SETTINGS_VOLUME_VALUE_MAX/100;

        Log.i(TAG, "setVolume: " + value+"; device="+SystemConfig.CAR_SERIES);

        if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
            //TODO
        }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
            //TODO
        }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_CXX){
            //TODO
        }else{
            this.send(value);
        }
    }

    /**
     * 设置音量
     * @param value 音量值
     */
    @Override
    public void setVolume(int value) {
        setVolume(value, 0);
    }

    /**
     * 格式化处理音量
     * @param value
     * @return
     */
    public static int normalizeValue(int value){
        //TODO
        return value;
    }

    /**
     * 获取音量
     * @return  value 音量值
     */
    @Override
    public int getVolume() {
        int value = 0;
        //TODO
        return value;
    }
}
