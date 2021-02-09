package com.metasequoia.manager.constent;

import android.util.Log;

import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.utils.SystemPropertiesProxy;

public class SystemConfig {
    public static final int CAR_SERIES_AXX = 1;
    public static final int CAR_SERIES_BXX = 2;
    public static final int CAR_SERIES_CXX = 3;
    public static int CAR_SERIES;

    public static void initConfig(){
        String fotaDevice = SystemPropertiesProxy.get(SettingsConfig.SETTINGS_FOTA_DEVICE);
        Log.d("CarSettingManager", "SystemConfig="+ fotaDevice);
        if("AXX".equals(fotaDevice)){
            CAR_SERIES = SystemConfig.CAR_SERIES_AXX;
        }else if("BXX".equals(fotaDevice)){
            CAR_SERIES = SystemConfig.CAR_SERIES_BXX;
        }else if("CXX".equals(fotaDevice)){
            CAR_SERIES = SystemConfig.CAR_SERIES_CXX;
        }else {
            CAR_SERIES = CAR_SERIES_AXX; //default
        }
        SettingsConfig.initConfig();
    }

}
