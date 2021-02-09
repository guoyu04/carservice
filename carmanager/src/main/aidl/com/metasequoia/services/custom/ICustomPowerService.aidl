// ICustomPowerService.aidl
package com.metasequoia.services.custom;

// Declare any non-default types here with import statements
import com.metasequoia.services.custom.ICustomPowerClient;

interface ICustomPowerService {
    int addClient(ICustomPowerClient client);
    int getAccState();//获取车辆状态
    float getBatteryPower();//获取剩余电量
    int getBatteryState();//获取电池状态
    int getTotalMileage();//获取总里程
    int getRemMileage();//获取剩余里程

    String getDeviceID();//获取device id

    float getBatteryVoltage();//获取电池电压
}
