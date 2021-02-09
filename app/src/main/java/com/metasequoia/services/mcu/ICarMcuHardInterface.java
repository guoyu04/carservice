package com.metasequoia.services.mcu;

import android.os.IBinder;
import android.os.IInterface;

public interface ICarMcuHardInterface extends IInterface {
    int BIND_REGISTER_MCU = IBinder.FIRST_CALL_TRANSACTION;
    int BIND_UNREGISTER_MCU = IBinder.FIRST_CALL_TRANSACTION + 1;
    int BIND_REQ_MCU_VER = IBinder.FIRST_CALL_TRANSACTION + 2;//请求MCU版本信息
    int BIND_REQ_MCU_UPDATE = IBinder.FIRST_CALL_TRANSACTION + 3;//请求MCU版本更新
    int BIND_NOTIFY_MCU_VER = IBinder.FIRST_CALL_TRANSACTION + 4;//回复MCU版本
    int BIND_NOTIFY_MCU_UPDATE_STATE = IBinder.FIRST_CALL_TRANSACTION + 5;//回复MCU更新状态
}
