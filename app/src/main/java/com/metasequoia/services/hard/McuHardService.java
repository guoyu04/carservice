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

package com.metasequoia.services.hard;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.metasequoia.manager.mcu.MCUVersionInfo;
import com.metasequoia.services.hard.IMcuHardClient;
import com.metasequoia.services.hard.IMcuHardService;
import com.metasequoia.services.mcu.CarMcuHardManager;

/**
 * audio服务管理类:音量设置等
 * Created by guoyu on 2020/8/2.
 */
public class McuHardService extends IMcuHardService.Stub implements CarMcuHardManager.CarMcuHardListener {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "AudioService";

    private Context mContext;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    /**
     * 对manager回调通知
     */
    private final RemoteCallbackList<IMcuHardClient> mClients = new RemoteCallbackList<>();

    public McuHardService(Context context) {
        if(DEBUG) Log.d(LOG_TAG, "McuHardService:"+"create");
        mContext = context;
        mHandlerThread = new HandlerThread("McuHardService", 1);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                dispatchMsg(message.what,message.arg1, message.obj);
                return false;
            }
        });
        CarMcuHardManager.getInstance().setCarMcuHardListener(this);
    }

    /**
     * 请求MCU版本信息，异步回调形式返回
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    @Override
    public int requestMcuVersion(){
        return CarMcuHardManager.getInstance().requestMcuVersion();
    }

    /**
     * 请求mcu更新
     * @param binPath mcu文件路径
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    @Override
    public int requestMcuUpdate(String binPath){
       return CarMcuHardManager.getInstance().requestMcuUpdate(binPath);
    }

    @Override
    public int addClient(IMcuHardClient client) {
        synchronized (mLock) {
            if(DEBUG) Log.d(LOG_TAG, "addClient:"+client);
            mClients.register(client);
        }
        return 0;
    }

    /**
     * MCU acc 状态信号
     * @param versionInfo 版本信息
     */
    @Override
    public void onMcuVersionInfo(MCUVersionInfo versionInfo){
        sendMcuVersion2Remote(versionInfo.softVersion, versionInfo.hardVersion);
    }

    /**
     * MCU 更新状态
     * @param state 成功(Common.RTN_SUCCESS) /  失败(Common.RTN_FAIL)
     */
    @Override
    public void onMcuUpdateState(int state){
        sendMcuUpdateState2Remote(state);
    }

    /**
     * 发送状态回调到代理端
     * @param clientState 状态
     */
    public void sendMcuUpdateState2Remote(int clientState) {
        if(DEBUG) Log.d(LOG_TAG, "sendMcuUpdateState2Remote:"+clientState);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                IMcuHardClient client = mClients.getBroadcastItem(i);
                try {
                    client.onMcuUpdateState(clientState);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    public void sendMcuVersion2Remote(String soft, String hard) {
        if(DEBUG) Log.d(LOG_TAG, "sendMcuVersion2Remote:soft="+soft+"; hard"+hard);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                IMcuHardClient client = mClients.getBroadcastItem(i);
                try {
                    client.onMcuVersionInfo(soft, hard);
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
        }
    }
}



