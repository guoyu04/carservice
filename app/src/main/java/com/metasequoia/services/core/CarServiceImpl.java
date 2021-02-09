package com.metasequoia.services.core;

import android.content.Context;
import android.os.ServiceManager;

import com.metasequoia.services.radio.RadioService;
import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.constent.ProductContext;
import com.metasequoia.services.custom.CustomCarSysService;
import com.metasequoia.services.hard.McuHardService;
import com.metasequoia.services.custom.CustomPowerService;
import com.metasequoia.services.mcu.CarMcuHardManager;
import com.metasequoia.services.mcu.McuSysManager;

/**
 * 服务类容器，主要初始化所有服务，并通过ServiceManager添加到系统服务中
 * Created by guoyu on 2020/8/2.
 */
public class CarServiceImpl {
    /**
     * radio服务
     */
    private RadioService mRadioService;


    /**
     * power服务
     */
    private CustomPowerService mPowerService;

    /**
     * MCU hard 服务
     */
    private McuHardService mMcuHardService;

    private CustomCarSysService mCustomSysService;
    public CarServiceImpl(){

    }

    /**
     * 初始化所有服务
     * @param context 系统Context
     */
    public void initCarService(Context context) {
        //初始化配置
        SystemConfig.initConfig();
        if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX) {
            //TODO mRadioService = new RadioService(context);
            //ServiceManager.addService(ProductContext.SERVICE_NAME_FMRADIO, mRadioService);
            mCustomSysService = new CustomCarSysService(context);
            ServiceManager.addService(ProductContext.SERVICE_NAME_CUSTOM_SYS, mCustomSysService);
            //初始化power
            mPowerService = new CustomPowerService(context);
            ServiceManager.addService(ProductContext.SERVICE_NAME_POWER, mPowerService);
            //初始化MCU
            mMcuHardService = new McuHardService(context);
            ServiceManager.addService(ProductContext.SERVICE_NAME_MCU, mMcuHardService);

            initMcuService(context);
        }
    }

    private void initMcuService(Context context){
        McuSysManager.getInstance().init(context);
        CarMcuHardManager.getInstance().init(context);
    }

    //TODO test
    public void testRadio(){
        mRadioService.sendStateToClients(233);
    }
}
