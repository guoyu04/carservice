package com.metasequoia.constent;

import android.content.Intent;

/**
 * 常量类
 * Created by guoyu on 2020/7/9.
 */
public class ActionConstent {


    /**
     * D车机acc on action
     */
    public static final String D_ACTION_ACC_ON = "com.nwd.action.ACTION_MCU_STATE_CHANGE";
    /**
     * D车机acc状态参数
     */
    public static final String D_ACC_EXTRA_STATE = "extra_mcu_state";

    /**
     * C车机acc on action
     */
    public static final String C_ACTION_ACC_ON = Intent.ACTION_POWER_CONNECTED;

    /**开机状态-开机*/
    public static final byte D_BOOT_STATE_START = 0x01;
    public static final byte D_BOOT_UNKOWN = 0xf;
}
