package com.metasequoia.manager.constent;

public class Common {
    public static final int RTN_SUCCESS = (0);      // 成功 正常

    public static final int RTN_FAIL = (-1);       // 失败 异常

    public static final int DEV_POWER_ON = (1);        //电源开
    public static final int DEV_POWER_OFF = (0);        //电源关闭

    public static final int ACC_STATE_OFF = 0x01;//熄火
    public static final int ACC_STATE_ACC = 0x02;//ACC
    public static final int ACC_STATE_START = 0x03;//熄启动火

    public static final int CAR_GEAR_P = 0x01;//P档
    public static final int CAR_GEAR_R = 0x02;//R档
    public static final int CAR_GEAR_N = 0x03;//N档
    public static final int CAR_GEAR_D = 0x04;//D档

    public static final int KEY_STATE_DOWN = 0x00;//key按下
    public static final int KEY_STATE_UP = 0x01;//key松开

    public static final int AUDIO_VOL_MUTE = 0;      //静音
    public static final int AUDIO_VOL_NO_MUTE = 1;      //非静音
    //音源输入通道
    public static final int AUDIO_CHANNEL_FM = 0;      //FM输入
    public static final int AUDIO_CHANNEL_MEDIA = 1;      //media输入
}
