package com.metasequoia.manager.system.setter;

import com.metasequoia.manager.constent.SystemConfig;

//6.0位于/data/system/users/0/settings_system.xml
public class SettingsConfig {
    public static final String SETTINGS_FOTA_DEVICE = "ro.fota.device";
    /**
     * 白天/黑夜字段
     */
    /**
     * 白天/黑夜模式切换settings provider字段
     */
    public static String SETTINGS_BRIGHTNESS_MODE;

    /**
     * 自动亮度
     */
    public static String SETTINGS_BRIGHTNESS_AUTO;

    /**
     * 白天settings provider字段
     */
    public static String SETTINGS_BRIGHTNESS_DAY;
    /**
     * 黑夜settings provider字段
     */
    public static String SETTINGS_BRIGHTNESS_NIGHT;

    /**
     * 最大亮度值
     */
    public static int SETTINGS_BRIGHTNESS_VALUE_MAX;

    /**
     * 白天默认亮度
     */
    public static int SETTINGS_BRIGHTNESS_DAY_DEFAULT;

    /**
     * 晚上默认亮度
     */
    public static int SETTINGS_BRIGHTNESS_NIGHT_DEFAULT;


    /**
     * 音量设置字段
     */
    public static String SETTINGS_VOLUME;

    /**
     * 最大音量值
     */
    public static int SETTINGS_VOLUME_VALUE_MAX;

    /**
     * 默认音量
     */
    public static int SETTINGS_VOLUME_DEFAULT;

    /**
     * acc状态
     */
    public static String SETTINGS_ACC;
    /**
     * acc状态
     */
    public static String SETTINGS_FM_FREQ;
    /**
     * 系统属性字段
     */
    //sn
    public static String SETTINGS_GSM_SERIAL;
    public static String SETTINGS_FOTA_OEM;
    public static String SETTINGS_FOTA_PLATFORM;
    public static String SETTINGS_FOTA_TYPE;
    public static String SETTINGS_FOTA_VERSION;

    public static void initConfig(){
            switch (SystemConfig.CAR_SERIES){
                case SystemConfig.CAR_SERIES_AXX:
                    initAXXXConfig();
                    break;
                case SystemConfig.CAR_SERIES_BXX:
                    initBXXConfig();
                    break;
                case SystemConfig.CAR_SERIES_CXX:
                    initCXXConfig();
                    break;
                default:
                    SETTINGS_BRIGHTNESS_MODE = "";
                    SETTINGS_BRIGHTNESS_DAY = "screen_brightness";
                    SETTINGS_BRIGHTNESS_NIGHT = "screen_brightness";
                    SETTINGS_BRIGHTNESS_AUTO = "screen_brightness_mode";
                    SETTINGS_VOLUME = "Volume";
                    SETTINGS_VOLUME_VALUE_MAX = 100;
                    SETTINGS_VOLUME_DEFAULT = 80;
                    SETTINGS_BRIGHTNESS_VALUE_MAX = 255;
                    SETTINGS_BRIGHTNESS_DAY_DEFAULT = 210;
                    SETTINGS_BRIGHTNESS_NIGHT_DEFAULT = 130;
                    SETTINGS_GSM_SERIAL = "gsm.serial";
                    SETTINGS_FOTA_OEM = "ro.fota.oem";
                    SETTINGS_FOTA_PLATFORM = "ro.fota.platform";
                    SETTINGS_FOTA_TYPE = "ro.fota.type";
                    SETTINGS_FOTA_VERSION = "ro.fota.version";
            }
    }
    private static void initCXXConfig(){
        SETTINGS_BRIGHTNESS_MODE = "brightness_day_night";
        SETTINGS_BRIGHTNESS_DAY = "mcu_back_light_day";
        SETTINGS_BRIGHTNESS_NIGHT = "mcu_back_light_night";
        SETTINGS_BRIGHTNESS_AUTO = "mcu_back_light_state";//标准
        SETTINGS_VOLUME = "mcu_system_volume";
        SETTINGS_VOLUME_VALUE_MAX = 40;
        SETTINGS_VOLUME_DEFAULT = 20;

        SETTINGS_BRIGHTNESS_VALUE_MAX = 100;
        SETTINGS_BRIGHTNESS_DAY_DEFAULT = 80;
        SETTINGS_BRIGHTNESS_NIGHT_DEFAULT = 60;
        SETTINGS_ACC = "power_acc";
        SETTINGS_FM_FREQ = "fm_freq";
        SETTINGS_GSM_SERIAL = "gsm.serial";
        SETTINGS_FOTA_OEM = "ro.fota.oem";
        SETTINGS_FOTA_PLATFORM = "ro.fota.platform";
        SETTINGS_FOTA_TYPE = "ro.fota.type";
        SETTINGS_FOTA_VERSION = "ro.fota.version";
    }
    private static void initBXXConfig(){
        SETTINGS_BRIGHTNESS_MODE = "brightness_day_night";
        SETTINGS_BRIGHTNESS_DAY = "brightness_day";
        SETTINGS_BRIGHTNESS_NIGHT = "brightness_night";
        SETTINGS_BRIGHTNESS_AUTO = "screen_brightness_mode";//标准
        /*SETTINGS_VOLUME = "Volume";*/
        SETTINGS_VOLUME = "volume_ring_speaker";
        SETTINGS_VOLUME_VALUE_MAX = 100;
        SETTINGS_VOLUME_DEFAULT = 60;

        SETTINGS_BRIGHTNESS_VALUE_MAX = 255;
        SETTINGS_BRIGHTNESS_DAY_DEFAULT = 210;
        SETTINGS_BRIGHTNESS_NIGHT_DEFAULT = 130;
        SETTINGS_ACC = "power_acc";
        SETTINGS_FM_FREQ = "fm_freq";
        SETTINGS_GSM_SERIAL = "gsm.serial";
        SETTINGS_FOTA_OEM = "ro.fota.oem";
        SETTINGS_FOTA_PLATFORM = "ro.fota.platform";
        SETTINGS_FOTA_TYPE = "ro.fota.type";
        SETTINGS_FOTA_VERSION = "ro.fota.version";
    }

    private static void initAXXXConfig(){
        SETTINGS_BRIGHTNESS_MODE = "PvetecBackLightMode";
        SETTINGS_BRIGHTNESS_DAY = "PvetecBackLightDayTime";
        SETTINGS_BRIGHTNESS_NIGHT = "PvetecBackLightNight";
        SETTINGS_VOLUME = "PvetecVolume";
        SETTINGS_BRIGHTNESS_AUTO = "";
        SETTINGS_VOLUME_VALUE_MAX = 100;
        SETTINGS_VOLUME_DEFAULT = 60;
        SETTINGS_BRIGHTNESS_VALUE_MAX = 100;
        SETTINGS_BRIGHTNESS_DAY_DEFAULT = 85;
        SETTINGS_BRIGHTNESS_NIGHT_DEFAULT = 50;
        SETTINGS_ACC = "power_acc";
        SETTINGS_GSM_SERIAL = "gsm.serial";
        SETTINGS_FOTA_OEM = "ro.fota.oem";
        SETTINGS_FOTA_PLATFORM = "ro.fota.platform";
        SETTINGS_FOTA_TYPE = "ro.fota.type";
        SETTINGS_FOTA_VERSION = "ro.fota.version";
    }
}
