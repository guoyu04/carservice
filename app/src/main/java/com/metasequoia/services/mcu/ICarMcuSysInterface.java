package com.metasequoia.services.mcu;

import android.os.IBinder;
import android.os.IInterface;


/**
 * 暂时不用
 */
public interface ICarMcuSysInterface extends IInterface {
    int BIND_REGISTER_SYS = IBinder.FIRST_CALL_TRANSACTION;
    int BIND_UNREGISTER_SYS = IBinder.FIRST_CALL_TRANSACTION + 1;
    int BIND_NOTIFY_ACC_STATUS = IBinder.FIRST_CALL_TRANSACTION + 2;
    int BIND_NOTIFY_BACK_STATUS = IBinder.FIRST_CALL_TRANSACTION + 3;//倒车信号
    int BIND_NOTIFY_ILL_STATUS = IBinder.FIRST_CALL_TRANSACTION + 4;//大灯信号
    int BIND_SET_SYS_SLEEP = IBinder.FIRST_CALL_TRANSACTION + 5;//设置系统开始休眠事件
    int BIND_SET_SYS_READY = IBinder.FIRST_CALL_TRANSACTION + 6;//设置系统启动完成事件
    int BIND_SET_SYS_REBOOT = IBinder.FIRST_CALL_TRANSACTION + 7;//设置系统重启事件
    int BIND_SET_BRIGHTNESS = IBinder.FIRST_CALL_TRANSACTION + 8;//设置系统亮度
    int BIND_NOTIFY_BRIGHTNESS = IBinder.FIRST_CALL_TRANSACTION + 9;//通知亮度改变
}
