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

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.metasequoia.services.mcu.McuSysManager;
import com.metasequoia.services.wifi.WifiAutoConnectManager;
import com.metasequoia.manager.constent.Common;
import com.metasequoia.manager.system.setter.SettingsConfig;

/**
 * 车厂接口服务管理类:系统设置服务,车速、里程等
 * Created by guoyu on 2020/8/2.
 */
public class CustomCarSysService extends ICustomCarSysService.Stub implements McuSysManager.CustomCarSysRunningListener {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "SystemService";
    public static final int MSG_GET_TOP_PACKAGE = 22;
    private Context mContext;
    private final ContentResolver mContentResolver;

    private WifiAutoConnectManager mWifiAutoConnectManager;
    //private SettingsObserver mSettingsObserver;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();
    private final Object mSettingsLock = new Object();

    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private String mTopPackage ="";
    /**
     * 对manager回调通知
     */
    private final RemoteCallbackList<ICustomCarSysClient> mClients = new RemoteCallbackList<>();

    public CustomCarSysService(Context context) {
        if(DEBUG) Log.d(LOG_TAG, "CustomSysService:"+"create");
        mContext = context;
        mContentResolver = context.getContentResolver();
        mHandlerThread = new HandlerThread("CustomSysService", 1);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                dispatchMsg(message.what,message.arg1, message.obj);
                return false;
            }
        });
        McuSysManager.getInstance().setSysRunningListener(this);
        //mSettingsObserver = new SettingsObserver();
        sendTopPackage();

        mWifiAutoConnectManager = WifiAutoConnectManager.getInstance();
        mWifiAutoConnectManager.init(mContext);
        //setVolume(mSettingsObserver.mVolume, 0);
        //CustomCarSysManager.getInstance().setCarMcuVolumeListener(this);
    }

    @Override
    public int addClient(ICustomCarSysClient client) {
        synchronized (mLock) {
            if(DEBUG) Log.d(LOG_TAG, "addClient:"+client);
            mClients.register(client);
        }
        return 0;
    }

    @Override
    public int sendSpeak(String data){
        if(DEBUG) Log.d(LOG_TAG, "sendSpeak:"+data);
        int ret = McuSysManager.getInstance().sendSpeak(data);
        return ret;
    }

    @Override
    public int setNavOpenState(boolean isOpen){
        if(DEBUG) Log.d(LOG_TAG, "setNavOpenState:"+isOpen);
        int ret = McuSysManager.getInstance().setNavOpenState(isOpen? Common.DEV_POWER_ON:Common.DEV_POWER_OFF);
        return ret;
    }

    @Override
    public int setBleOpenState(boolean isOpen){
        if(DEBUG) Log.d(LOG_TAG, "setBleOpenState:isOpen="+isOpen);
        int ret =McuSysManager.getInstance().setBleOpenState(isOpen? Common.DEV_POWER_ON:Common.DEV_POWER_OFF);
        return ret;
    }

    @Override
    public int setFMOpenState(boolean isOpen){
        if(DEBUG) Log.d(LOG_TAG, "setFMOpenState:state="+isOpen);
        int ret = McuSysManager.getInstance().setFMOpenState(isOpen? Common.DEV_POWER_ON:Common.DEV_POWER_OFF);
        return ret;
    }

    //设置人脸认证状态
    @Override
    public int setFaceState(int state){
        if(DEBUG) Log.d(LOG_TAG, "setFaceState:state="+state);
        int ret = McuSysManager.getInstance().setFaceState(state);
        return ret;
    }
    //设置评分等级
    @Override
    public int setGrading(int grade){
        if(DEBUG) Log.d(LOG_TAG, "setGrading:state="+grade);
        int ret = McuSysManager.getInstance().setGrading(grade);
        return ret;
    }
    //目的地周边优惠信息
    @Override
    public int setDestPerInfo(int type){
        if(DEBUG) Log.d(LOG_TAG, "setDestPerInfo:type="+type);
        int ret = McuSysManager.getInstance().setDestPerInfo(type);
        return ret;
    }
    //订单信息
    @Override
    public int setOrderInfo(int type){
        if(DEBUG) Log.d(LOG_TAG, "setOrderInfo:type="+type);
        int ret = McuSysManager.getInstance().setOrderInfo(type);
        return ret;
    }
    /**
     * 获取档位状态
     * @return 位状态
     */
    @Override
    public int getCarGear() {
        return McuSysManager.getInstance().getCarGear();
    }
    /**
     * 获取车速
     * @return 车速
     */
    @Override
    public float getCarSpeed(){
        return McuSysManager.getInstance().getCarSpeed();
    }
    /**
     * 获取转速
     * @return 转速
     */
    @Override
    public int getRotateSpeed(){
        return McuSysManager.getInstance().getRotateSpeed();
    }
    /**
     * 发送状态回调到代理端
     * @param gear 状态
     */
    private void sendCarGear2Remote(int gear) {
        if(DEBUG) Log.d(LOG_TAG, "sendCarGear2Remote:"+gear);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomCarSysClient client = mClients.getBroadcastItem(i);
                try {
                    client.onCarGear(gear);
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
     * 发送mcu车速回调到代理端
     * @param speed mcu车速
     */
    private void sendCarSpeed2Remote(float speed) {
        if(DEBUG) Log.d(LOG_TAG, "sendCarSpeed2Remote:"+speed);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomCarSysClient client = mClients.getBroadcastItem(i);
                try {
                    client.onCarSpeed(speed);
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
     * 发送mcu转速回调到代理端
     * @param speed mcu转速
     */
    private void sendRotateSpeed2Remote(int speed) {
        if(DEBUG) Log.d(LOG_TAG, "sendRotateSpeed2Remote:"+speed);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomCarSysClient client = mClients.getBroadcastItem(i);
                try {
                    client.onRotateSpeed(speed);
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
     * 一键报警按键 1松开，0按下
     * @param key 按键
     * @param state 状态
     */
    private void sendKeyEvent2Remote(int key, int state) {
        if(DEBUG) Log.d(LOG_TAG, "sendKeyEvent2Remote key="+key+";state="+state);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                ICustomCarSysClient client = mClients.getBroadcastItem(i);
                try {
                    client.onKeyEvent(key, state);
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
            case MSG_GET_TOP_PACKAGE:
                sendTopPackage();
                break;
        }
    }

    /**
     * 设置当前前台应用，如果前台应用改变则设置到mcu
     */
    private void sendTopPackage(){
        String topPck = getTopAppPackageName(mContext);
        if(!TextUtils.isEmpty(topPck) && !topPck.equals(mTopPackage)){
            mTopPackage = topPck;
            McuSysManager.getInstance().setTopPackage(mTopPackage);
            Log.d(LOG_TAG,"current:"+mTopPackage);
        }
        Message msg = mHandler.obtainMessage(MSG_GET_TOP_PACKAGE);
        mHandler.sendMessageDelayed(msg, 500);

    }
    public static String getTopAppPackageName(Context context) {
        String packageName = "";
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            final long end = System.currentTimeMillis();
            final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService( Context.USAGE_STATS_SERVICE);
            if (null == usageStatsManager) {
                return packageName;
            }
            final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
            if (null == events) {
                return packageName;
            }
            UsageEvents.Event usageEvent = new UsageEvents.Event();
            UsageEvents.Event lastMoveToFGEvent = null;
            while (events.hasNextEvent()) {
                events.getNextEvent(usageEvent);
                if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastMoveToFGEvent = usageEvent;
                }
            }
            if (lastMoveToFGEvent != null) {
                packageName = lastMoveToFGEvent.getPackageName();
            }
        }
        return packageName;
    }

    /**
     * mcu档位状态
     * @param gear mcu档位
     */
    @Override
    public void onMcuCarGear(int gear){
        sendCarGear2Remote(gear);
    }

    /**
     * mcu车速
     * @param speed 车速
     */
    @Override
    public void onMcuCarSpeed(float speed){
        sendCarSpeed2Remote(speed);
    }
    /**
     * mcu转速
     * @param speed 转速
     */
    @Override
    public void onMcuRotateSpeed(int speed){
        sendRotateSpeed2Remote(speed);
    }

    @Override
    public void onWifiAccountChanged(String account) {
        mWifiAutoConnectManager.setWifiAccount(account);
    }

    @Override
    public void onWifiPwdChanged(String pwd) {
        mWifiAutoConnectManager.setWifiPwd(pwd);
    }

    /**
     * key事件
     * @param key 按键
     */
    @Override
    public void onMcuKeyEvent(int key, int state){
        sendKeyEvent2Remote(key, state);
    }

    private class SettingsObserver extends ContentObserver {

        private int mVolume = 0;
        SettingsObserver() {
            super(new Handler());
            mContentResolver.registerContentObserver(Settings.System.getUriFor(SettingsConfig.SETTINGS_VOLUME), false, this);
            mVolume = getSettingsVolume();
        }

        private void setSettingsVolume(int volume){
            mVolume = volume;
            Settings.System.putInt(mContext.getContentResolver(), SettingsConfig.SETTINGS_VOLUME, volume);
        }
        private int getSettingsVolume(){
            return Settings.System.getInt(mContentResolver, SettingsConfig.SETTINGS_VOLUME,SettingsConfig.SETTINGS_VOLUME_DEFAULT);
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (mSettingsLock) {
            }
        }
    }
}



