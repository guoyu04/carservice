/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metasequoia.services.custom;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.metasequoia.services.mcu.McuSysManager;
import com.metasequoia.manager.constent.Common;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.services.custom.ICustomPowerClient;

/**
 * power服务管理类:背光，当前电量等
 * Created by guoyu on 2019/3/30.
 */
public class CustomPowerService extends ICustomPowerService.Stub implements McuSysManager.CustomCarSysPowerListener {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "CustomPowerService";
    public static final int MSG_SETTING_BRIGHTNESS_CHANGE = 1;
    private Context mContext;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();
    private final Object mSettingsLock = new Object();
    /**
     * 对manager回调通知
     */
    private final RemoteCallbackList<ICustomPowerClient> mClients = new RemoteCallbackList<>();

    private final ContentResolver mContentResolver;
    private SettingsObserver mSettingsObserver;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    public CustomPowerService(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
        mHandlerThread = new HandlerThread("CustomPowerService", 1);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                dispatchMsg(message.what,message.arg1, message.obj);
                return false;
            }
        });

        mSettingsObserver = new SettingsObserver();
        McuSysManager.getInstance().setSysPowerListener(this);
    }

    @Override
    public int addClient(ICustomPowerClient client) {
        synchronized (mLock) {
            if(DEBUG) Log.d(LOG_TAG, "addClient:"+client);
            mClients.register(client);
        }
        return 0;
    }

    /**
     * 获取车辆状态
     * @return 车辆状态
     */
    @Override
    public int getAccState(){
        return McuSysManager.getInstance().getAccState();
    }
    /**
     * 获取剩余电量
     * @return 剩余电量
     */
    @Override
    public float getBatteryPower(){
        return McuSysManager.getInstance().getBatteryPower();
    }
    /**
     * 获取电池状态
     * @return 电池状态
     */
    @Override
    public int getBatteryState(){
        return McuSysManager.getInstance().getBatteryState();
    }

    /**
     * 获取总里程
     * @return 总里程
     */
    @Override
    public int getTotalMileage(){
        return McuSysManager.getInstance().getTotalMileage();
    }
    /**
     * 获取剩余里程
     * @return 剩余里程
     */
    public int getRemMileage() {
        return McuSysManager.getInstance().getRemMileage();
    }

    /**
     * 获取剩余里程
     * @return 剩余里程
     */
    public String getDeviceID() {
        return McuSysManager.getInstance().getDeviceID();
    }

    /**
     * 获取电瓶电压
     * @return 电瓶电压
     */
    @Override
    public float getBatteryVoltage(){
        return McuSysManager.getInstance().getBatVoltage();
    }

    /**
     * MCU acc 状态信号
     * @param state  acc on(Common.DEV_POWER_ON) /  acc off(Common.DEV_POWER_OFF)
     */
    @Override
    public void onMcuAccChange(int state) {
        sendAccChange2Remote(state);
        mSettingsObserver.setAccState(state);
    }

    /**
     * mcu电池电量
     * @param battery
     */
    @Override
    public void onMcuBatteryPower(float battery){
        sendBatPower2Remote(battery);
    }

    /**
     * mcu电池状态
     * @param state
     */
    @Override
    public void onMcuBatteryState(int state){
        sendBatState2Remote(state);
    }

    /**
     * mcu总里程
     * @param mileage 总里程
     */
    @Override
    public void onMcuTotalMileage(int mileage){
        sendTotalMileage2Remote(mileage);
    }

    /**
     * mcu剩余里程
     * @param mileage 剩余里程
     */
    @Override
    public void onMcuRemMileage(int mileage){
        sendRemMileage2Remote(mileage);
    }

    @Override
    public void onDeviceIDChanged(String devID){
        sendDevID2Remote(devID);
    }

    /**
     * mcu电瓶电压
     * @param voltage 电瓶电压
     */
    @Override
    public void onMcuBatVoltage(float voltage){
        sendBatVoltage2Remote(voltage);
    }

    /**
     * 发送ACC状态回调到代理端
     * @param state acc 状态
     */
    private void sendAccChange2Remote(int state) {
        if(DEBUG) Log.d(LOG_TAG, "sendAccChange2Remote:"+state);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomPowerClient client = mClients.getBroadcastItem(i);
                try {
                    client.onAccStateChange(state);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    /**
     * 发送电池电量回调到代理端
     * @param battery 电池电量
     */
    private void sendBatPower2Remote(float battery) {
        if(DEBUG) Log.d(LOG_TAG, "sendBatPower2Remote:"+battery);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomPowerClient client = mClients.getBroadcastItem(i);
                try {
                    client.onBatteryPower(battery);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    /**
     * 发送电池状态回调到代理端
     * @param state 电池 状态
     */
    private void sendBatState2Remote(int state) {
        if(DEBUG) Log.d(LOG_TAG, "sendBatPower2Remote:"+state);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomPowerClient client = mClients.getBroadcastItem(i);
                try {
                    client.onBatteryState(state);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    /**
     * 发送mcu总里程回调到代理端
     * @param mileage mcu总里程
     */
    public void sendTotalMileage2Remote(int mileage) {
        if(DEBUG) Log.d(LOG_TAG, "sendTotalMileage2Remote:"+mileage);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomPowerClient client = mClients.getBroadcastItem(i);
                try {
                    client.onTotalMileage(mileage);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    /**
     * 发送mcu剩余里程回调到代理端
     * @param mileage mcu剩余里程
     */
    public void sendRemMileage2Remote(int mileage) {
        if(DEBUG) Log.d(LOG_TAG, "sendRemMileage2Remote:"+mileage);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomPowerClient client = mClients.getBroadcastItem(i);
                try {
                    client.onRemMileage(mileage);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    /**
     * 发送mcu id回调到代理端
     * @param devID mcu id
     */
    private void sendDevID2Remote(String devID) {
        if(DEBUG) Log.d(LOG_TAG, "sendDevID2Remote:"+devID);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomPowerClient client = mClients.getBroadcastItem(i);
                try {
                    client.onDeviceID(devID);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    /**
     * 发送电池电压回调到代理端
     * @param voltage 电池电压
     */
    private void sendBatVoltage2Remote(float voltage) {
        if(DEBUG) Log.d(LOG_TAG, "sendBatVoltage2Remote:"+voltage);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomPowerClient client = mClients.getBroadcastItem(i);
                try {
                    client.onBatteryVoltage(voltage);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    /**
     * 处理消息
     * @param what 消息what
     * @param arg 消息obj
     */
    private void dispatchMsg(int what, int arg, Object obj){
        switch (what) {
            case MSG_SETTING_BRIGHTNESS_CHANGE:
                //setBrightness(arg);
                break;
        }
    }

    private class SettingsObserver extends ContentObserver {
        private int mAcc = 0;
        SettingsObserver() {
            super(new Handler());
            //mContentResolver.registerContentObserver(Settings.System.getUriFor(SettingsConfig.SETTINGS_ACC), false, this);
            //mContentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, this);
        }

        private void setAccState(int state){
            Settings.System.putInt(mContext.getContentResolver(), SettingsConfig.SETTINGS_ACC, state);
        }

        private int getAccState(){
            return Settings.System.getInt(mContentResolver, SettingsConfig.SETTINGS_ACC, Common.ACC_STATE_OFF);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (mSettingsLock) {
                int value = getAccState();
                if(mAcc != value){
                    setAccState(value);
                }
            }
        }
    }
}
