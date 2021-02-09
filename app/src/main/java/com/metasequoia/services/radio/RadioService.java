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

package com.metasequoia.services.radio;


import android.content.Context;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.metasequoia.services.radio.api.RadioApiImpl;
import com.metasequoia.services.radio.factory.Radio;
import com.metasequoia.services.radio.factory.RadioFactory;
import com.metasequoia.services.radio.factory.SharedPreferencesUtils;
import com.metasequoia.constent.ErrorCode;
import com.metasequoia.datebase.RadioDbManager;
import com.metasequoia.manager.radio.bean.Frequency;
import com.metasequoia.manager.radio.bean.PublicDef;
import com.metasequoia.manager.radio.bean.RadioArea;
import com.metasequoia.services.radio.IRadioClient;
import com.metasequoia.services.radio.IRadioService;
import com.metasequoia.manager.radio.listener.OnDataChangeListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * fm radio服务管理类
 * Created by guoyu on 2020/8/2.
 */
public class RadioService extends IRadioService.Stub {
    private static final boolean DEBUG = true;
    private static final String TAG = "RadioService";
    private Context mContext;

    private Frequency mCurrentFrequency = new Frequency(0,0,0,(String)null, 0, /*0,*/ (String)null);
    private Radio mRadio = null;
    private RadioApiImpl mRadioApi = new RadioApiImpl();
    SharedPreferencesUtils mSharedPre = null;
    private int mBandType = 1;  //FM or AM
    private int mAreaId = PublicDef.RADIO_AREA_CHINA;
    private RadioArea[] mRadioArea = new RadioArea[3];
    private int mRadioState = ErrorCode.FAIL;
    Frequency[] frequencies = null;
    private RadioDbManager dbManager;

    /**
     * 收音机状态(是否正在搜台)， for D  , mState = 2 : search...  ,mState = 1 :  nomal
     */
    private int mState;
    /** * 远程近程  true：远程 ;  false：近程 **/
    private boolean isNearOn;
    /** 有无立体声 **/
    private boolean isStereo = true;
    /** 开关立体声 **/
    private boolean isStereoOn;


    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    /**
     * 对manager回调通知initRadio:type
     */
    private final RemoteCallbackList<IRadioClient> mClients = new RemoteCallbackList<>();



    public RadioService(Context context) {
        if(DEBUG) Log.d(TAG, "RadioService:"+"create");
        mContext = context;
        mSharedPre = SharedPreferencesUtils.getInstance(mContext);
        dbManager = RadioDbManager.getInstance();
        dbManager.init(mContext);

        initRadioFactory();
        initRadioApi();
    }

    private int initRadioApi() {
        int num = 0;
        int ret = ErrorCode.FAIL;
        while(ret < 0 && num < 3) {
            ret = mRadioApi.init();
            Log.i(TAG, "initRadioApi result:" + ret);
            if(ret > 0) break;
            num++;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    this.cancel();
                }
            }, 2000);
        }

        mRadioState = ret;
        Log.i(TAG, "initRadioApi ret:" + ret);
        return ret;
    }

    private void initRadioFactory() {

        mBandType = mSharedPre.getIntValue(SharedPreferencesUtils.RADIO_BAND, 0)==2? PublicDef.RADIO_AM : PublicDef.RADIO_FM;
        mRadio = RadioFactory.getInstance().getRadio(mBandType);

        Log.i(TAG, "initRadioFactory, mBandType" + mBandType);
        mSharedPre.putIntValue(SharedPreferencesUtils.RADIO_BAND, mBandType);

        mAreaId = mSharedPre.getRadioAreaId(mContext);
        getArea();
    }

    @Override
    public void initRadio(int type) {
        if(DEBUG) Log.d(TAG, "initRadio ,type["+type+"]");
        if(mRadioState != 1) {
            initRadioFactory();
            int result = initRadioApi();
            Log.i(TAG, "initRadio result:" + result);
            sendStateToClients(result);
            return ;
        }
        mRadio.setRadioApi(mRadioApi);
        getPrefabFrequency();
        getArea();
        sendStateToClients(1);
    }

    public void play() {
        Log.i(TAG, "play.");
        setMute(false);
        String key = (mBandType == PublicDef.RADIO_FM) ? SharedPreferencesUtils.FM_FREQUENCY : SharedPreferencesUtils.AM_FREQUENCY;
        Log.i(TAG, "key:" + key);
        int frequency = mSharedPre.getIntValue(key, 0);
        if(frequency != 0) {
            setFrequency(frequency);
        } else if(frequencies != null && frequencies.length > 0){
            setFrequency(frequencies[0].getFrequency());
        } else if(mBandType == PublicDef.RADIO_FM){
            setFrequency(PublicDef.PREB_FREQUENCY_FM[mAreaId][0]);
        } else {
            setFrequency(PublicDef.PREB_FREQUENCY_AM[mAreaId][0]);
        }
    }


    public void init() {
        if(DEBUG) Log.i(TAG, "init...");
        if(mRadioApi!=null)
            mRadioApi.init();
    }

    public int uninitRadio() {
        if(DEBUG) Log.i(TAG, "uninitRadio.");
        if(mRadio != null)
            mRadio.uninit();  //stop scan...
        int result = mRadioApi.uninit();
        Log.i(TAG, "uninitRadio result:" + result);
        return result;
    }

    public int openAntenna() {
        if(DEBUG) Log.i(TAG, "openAntenna.");
        int result = mRadioApi.openAntenna();
        Log.i(TAG, "openAntenna result:" + result);
        return result;
    }

    public int closeAntenna() {
        if(DEBUG) Log.i(TAG, "closeAntenna.");
        int result =  mRadioApi.closeAntenna();
        Log.i(TAG, "closeAntenna result:" + result);
        return result;
    }

    @Override
    public void AM() {
        if(mRadio != null && mRadio.getId() == PublicDef.RADIO_AM) {
            Log.i(TAG, "AM...");
            mRadio.setRadioApi(mRadioApi);
            mSharedPre.putIntValue(SharedPreferencesUtils.RADIO_BAND, PublicDef.RADIO_AM);
            mBandType = PublicDef.RADIO_AM;
            return;
        }
        if(mRadio != null) {
            mRadio.uninit();
        }
        mRadio = RadioFactory.getInstance().getRadio(PublicDef.RADIO_AM);
        mRadio.setRadioApi(mRadioApi);
        mRadio.init();
        mBandType = PublicDef.RADIO_AM;
        getPrefabFrequency();
        mSharedPre.putIntValue(SharedPreferencesUtils.RADIO_BAND, PublicDef.RADIO_AM);
        play();
    }

    @Override
    public void FM() {
        if(mRadio != null && mRadio.getId() == PublicDef.RADIO_FM) {
            mRadio.setRadioApi(mRadioApi);
            mSharedPre.putIntValue(SharedPreferencesUtils.RADIO_BAND, PublicDef.RADIO_FM);
            mBandType = PublicDef.RADIO_FM;
            return;
        }
        if(mRadio != null)
            mRadio.uninit();
        mRadio = RadioFactory.getInstance().getRadio(PublicDef.RADIO_FM);
        mRadio.setRadioApi(mRadioApi);
        mRadio.init();
        mBandType = PublicDef.RADIO_FM;
        getPrefabFrequency();
        mSharedPre.putIntValue(SharedPreferencesUtils.RADIO_BAND, PublicDef.RADIO_FM);

        //todo setFrequency...play...
        play();
    }

    public int setAreaId(int areaId) {
        if(DEBUG) Log.i(TAG, "setAreaId areaId:" + areaId);
        if(mRadio == null)
            return -1;
        mAreaId = areaId;
        int result = mRadio.setAreaId(areaId);
        Log.i(TAG, "setAreaId result:" + result);
        return mAreaId;
    }

    public RadioArea[] getArea() {
        if(DEBUG) Log.i(TAG, "getArea.");
        mRadioArea[0] = PublicDef.sAreaMapFM.get(mAreaId);
        mRadioArea[1] = PublicDef.sAreaMapAM.get(mAreaId);
        mRadioArea[2] = new RadioArea(0, 0, 0, 0);

        return mRadioArea;
    }

    public RadioArea[] getAreaById(int areaId) {
        if(DEBUG) Log.i(TAG, "getAreaById. areaId:" + areaId);
        if(mRadio == null)
            return null;

        mRadioArea[0] = PublicDef.sAreaMapFM.get(areaId);
        mRadioArea[1] = PublicDef.sAreaMapAM.get(areaId);
        mRadioArea[2] = new RadioArea(0, 0, 0, 0);
        return mRadioArea;
    }

    /**
     * 获取频道列表
     **/
    public Frequency[] getPrefabFrequency() {
        Log.i(TAG, "getPrefabPrequency..");

        frequencies = null;
        List<Frequency> list = dbManager.queryAll(getBand() == PublicDef.RADIO_FM ? "FM" : "AM");
        if(list != null && list.size() >0) {
            Log.i(TAG, "getPrefabPrequency 0");
            frequencies = new Frequency[list.size()];
            for(int i = 0; i<list.size(); i++) {
                frequencies[i] = list.get(i);
                Log.i(TAG, "getPrefabPrequency quaryAll:" + list.get(i).toString());
            }
        } else {
            Log.i(TAG, "getPrefabPrequency 1");
            if(mBandType == PublicDef.RADIO_AM) {
                frequencies = new Frequency[PublicDef.PREB_FREQUENCY_AM[mAreaId].length];
                for (int i = 0; i < PublicDef.PREB_FREQUENCY_AM[mAreaId].length; i++) {
                    frequencies[i] = new Frequency(0, PublicDef.PREB_FREQUENCY_AM[mAreaId][i], mBandType, "UNKNOWN", i, "0");
                }
            } else {
               frequencies = new Frequency[PublicDef.PREB_FREQUENCY_FM[mAreaId].length];
                for (int i = 0; i < PublicDef.PREB_FREQUENCY_FM[mAreaId].length; i++) {
                    frequencies[i] = new Frequency(0, PublicDef.PREB_FREQUENCY_FM[mAreaId][i], mBandType,"UNKNOWN", i, "0");
                }
            }
        }
        return frequencies;
    }

    public Frequency getCurrentFrequency() {
        if(DEBUG) Log.i(TAG, "getCurrentFrequency.");
        return mCurrentFrequency;
    }

    @Override
    public void nextChannel() {
        if(DEBUG) Log.i(TAG, "nextChannel.");

        boolean flag = true;
        if(frequencies == null) getPrefabFrequency();

        for(int i = 0 ; i < frequencies.length; i++) {
            if(mCurrentFrequency.getFrequency() == frequencies[i].getFrequency()) {
                flag = false;
                if(i == (frequencies.length-1)) {
                    i = 0;
                } else {
                    i = i + 1;
                }
                setFrequency(frequencies[i].getFrequency());
            }
        }
        if(flag)
            setFrequency(frequencies[0].getFrequency());

    }

    @Override
    public void preChannel() {
        if(DEBUG) Log.i(TAG, "preChannel.");
        boolean flag = true;
        if(frequencies == null) getPrefabFrequency();
        for(int i = (frequencies.length -1) ; i >= 0 ; i--) {
            if(mCurrentFrequency.getFrequency() == frequencies[i].getFrequency()) {

                flag = false;
                if(i == 0) {
                    i = frequencies.length -1;
                } else {
                    i = i - 1;
                }
                setFrequency(frequencies[i].getFrequency());
            }
        }
        if(flag)
            setFrequency(frequencies[0].getFrequency());
    }

    @Override
    public void getRadioInfo() {
        if(DEBUG) Log.i(TAG, "getRadioInfo");
    }


    @Override
    public void sendRadioCommand(byte param0, byte param1) throws RemoteException {
        if(DEBUG) Log.i(TAG, "sendRadioCommand");
    }


    public void search() {
        Log.i(TAG, "search.");
        if(mRadio == null) {
            sendScanEnd("false");
        }
        Log.i(TAG, "search......");
        mRadio.scan(mContext, true, mOnDataChangeListener);
    }


    public void searchByDirection(boolean direction) {
        if(DEBUG) Log.i(TAG, "searchByDirection. direction" + direction);
        Log.i(TAG, "searchByDirection--");
        if(mRadio == null) {
            sendScanEnd("false");
        }

        mRadio.scan(mContext, direction, mOnDataChangeListener);
    }

    public int stopSearch() {
        if(DEBUG) Log.i(TAG, "stopScan.");
        if(mRadio == null)
            return -1;
        return mRadio.stopScan();
    }

    @Override
    public boolean isSupportStereo() {
        return isStereo;
    }

    public void setStereo(boolean enable) {
        if(DEBUG) Log.i(TAG, "setStereo. " + enable);
        if(mRadioApi == null)
            return;
        int result = mRadioApi.setStereo(enable);
        Log.i(TAG, "setStereo result:" + result);
        if(result == 1) {
            isStereoOn = enable;
            sendIsStereoOn(isStereoOn);
        }
    }

    /**
     * 远程0近程1
     * @param dx
     */
    public void setRadioDX(int dx) {
        if(DEBUG) Log.i(TAG, "setRadioDX. dx:" + dx);
        if(mRadioApi == null)
            return ;
        int result = mRadioApi.setRadioDX(dx);
        Log.i(TAG, "setRadioDX result:" + result);
        if(result == 1) {
            isNearOn = (dx == 0) ? true : false;
            sendIsRadioDX(isNearOn);
        }
    }

    /**
     * 获取立体声状态
     * @return
     * true：打开
     * false：关闭
     */
    public boolean getStereo() {
        return isStereoOn;
    }

    /**
     * 获取远近程
     * @return
     * true：远程
     * false：近程
     */
    public boolean getRadioDX() {
        return  isNearOn;
    }

    public boolean isScanning() {
        if(DEBUG) Log.i(TAG, "isScanning.");
        return mRadio.isScanning();
    }

    /**
     * 设置频点
     * @param freq
     */
    public void setFrequency(int freq) {
        if(DEBUG) Log.i(TAG, "setFrequency. freq:" + freq);
        if(mRadio == null)
            return;

        int result = mRadio.setFreq(freq);
        Log.i(TAG, "setFrequency result.." + result);
        if(result == 1) {
            if(mBandType == PublicDef.RADIO_FM) {
                mSharedPre.putIntValue(SharedPreferencesUtils.FM_FREQUENCY, freq);
            } else {
                mSharedPre.putIntValue(SharedPreferencesUtils.AM_FREQUENCY, freq);
            }
            mCurrentFrequency.setFrequency(freq);   ////to do...是否需要优化
            Log.i(TAG, "setFreq...........freq:" + freq);
            sendCurrentFrequencyChanged(mCurrentFrequency);
        }

    }

    public void seek(){
        if(DEBUG) Log.i(TAG, "seek");
        if(mRadio == null)
            return;
        if(mBandType == PublicDef.RADIO_FM) {
            int freq = mCurrentFrequency.getFrequency() + mRadioArea[0].getFrequencyStep();
            setFrequency(freq);
        } else if(mBandType == PublicDef.RADIO_AM) {
            int freq = mCurrentFrequency.getFrequency() + mRadioArea[1].getFrequencyStep();
            setFrequency(freq);
        }
    }

    public int getBand() {
        if(DEBUG) Log.i(TAG, "getBand.");
        if(mRadio == null)
            return -1;
        int result = mRadio.getId();
        Log.i(TAG, "getBand result:" + result);
        return result;
    }

    public int setVolume(int vol) {
        if(DEBUG) Log.i(TAG, "setVolume.");
        if(mRadioApi == null)
            return -1;
        if(vol > 100)  vol = 100;
        int result = mRadioApi.setVolume(vol);
        Log.i(TAG, "setVolume result:" + result);
        return result;
    }

    public int setMute(boolean mute) {
        if(mRadioApi == null)
            return -1;
        int result = mRadioApi.setMute(mute);
        Log.i(TAG, "setMute result:" + result);
        return result;
    }

    /**
     * get whether RadioDX is support ; DX : 远程/近程
     * @return
     * true : support
     * false : not support
     */
    public boolean isSupportRadioDx() {
        return true;
    }

    /**
     * is support band type (am, fm...)
     * @param bandType
     * PublicDef.RADIO_FM, PublicDef.RADIO_AM
     * @return
     * true : support
     * false : not support
     */
    public boolean isSupportBand(int bandType) {
        if(bandType == PublicDef.RADIO_FM) {
            return true;
        } else if (bandType == PublicDef.RADIO_AM){
            return true;
        } else {
            return false;
        }
    }

    /**
     * while scanning ,is callback points real-time?
     * @return
     * true : callback points real-time
     * false : not callback
     */
    public boolean isSupportScaningCallback() {
        return true;
    }

    /**
     * 是否支持设置信号强度
     * @return
     * true : 支持
     * false : 不支持
     */
    public boolean isSupportSetRssi() {
        return false;
    }

    /**
     * 获取收音机信号强度，结果以回调方式返回

     */
    public void getRssi() { }

    /**
     * 设置信号强度，结果以回调方式返回
     * @param fmCustCfgs
     * @param values
     */
    public void setRssi(List<String> fmCustCfgs, int values) { }

    /**
     * 是否支持开关天线电源
     * @return
     * true : 支持
     * false : 不支持
     */
    public boolean isSupportAntenna() {
        return true;
    }

    /**
     * 是否支持设置收音机信号强度
     * @return
     * true : 支持
     * false : 不支持
     */
    public boolean isSupportRssi() {
        return false;
    }

    /**
     * get Antenna states
     * @return
     * open : open
     * close : close
     * unknown : can not get states..
     */
    public String getAntennaState(){
        return "unknown";
    }

    /**
     * save RadioInfo to back service
     * @param frequency
     * @param exString
     */
    public void saveRadioInfo(Frequency frequency, String exString) { }

    public int getSignalStatus(int freq) {
        if(DEBUG) Log.i(TAG, "getSignalStatus.freq:" + freq);
        if(mRadio == null)
            return -1;
        int signal = mRadio.getSignalStatus(freq);
        Log.i(TAG, "getSignalStatus :" + signal);
        return signal;
    }

    @Override
    public int addClient(IRadioClient client) {
        synchronized (mLock) {
            if(DEBUG) Log.d(TAG, "addClient:"+client);
            mClients.register(client);
            BinderDeathRecipient mDeathRecipient = new BinderDeathRecipient(client);
            try {
                client.asBinder().linkToDeath(mDeathRecipient, 0);;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int removeClient(IRadioClient client) {
        synchronized (mLock) {
            if(DEBUG) Log.d(TAG, "remvoeClient:"+client);
            mClients.register(client);
        }
        return 0;
    }

    /**
     * 发送状态回调到代理端
     * @param clientState 状态
     * 成功：1
     * 失败：-1
     */
    public void sendStateToClients(int clientState) {
        if(DEBUG) Log.d(TAG, "sendStateToClients:"+clientState);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                IRadioClient client = mClients.getBroadcastItem(i);
                try {
                    client.setState(clientState);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }


    private void sendCurrentFrequencyChanged(Frequency frequency) {
        if(DEBUG) Log.d(TAG, "sendCurrentFrequencyChanged:"+frequency);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                IRadioClient client = mClients.getBroadcastItem(i);
                try {
                    client.setCurrentFrequencyChange(frequency);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }


    private void sendIsStereoOn(boolean isStereoOn) {
        if(DEBUG) Log.d(TAG, "sendIsStereoOn:"+ isStereoOn);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                IRadioClient client = mClients.getBroadcastItem(i);
                try {
                    client.setIsStereoOn(isStereoOn);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    private void sendIsRadioDX(boolean isNearOn) {
        if(DEBUG) Log.d(TAG, "sendIsRadioDX:"+ isNearOn);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                IRadioClient client = mClients.getBroadcastItem(i);
                try {
                    client.setIsRadioDX(isNearOn);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    private void sendScanEnd(String endStr) {
        if(DEBUG) Log.d(TAG, "sendScanEnd:"+endStr);
        try {
            final int userClientCount = mClients.beginBroadcast();
            for (int i = 0; i < userClientCount; i++) {
                IRadioClient client = mClients.getBroadcastItem(i);
                try {
                    client.setSearchEnd(endStr);
                } catch (RemoteException re) {
                    /* ignore */
                    re.printStackTrace();
                }
            }
        } finally {
            mClients.finishBroadcast();
        }
    }

    OnDataChangeListener mOnDataChangeListener = new OnDataChangeListener() {
        @Override
        public void onScanChanged(Frequency frequency, int effect) {
            if(DEBUG) Log.d(TAG, "onScanChanged effect:" + effect + " fre:" + frequency);
            try {
                final int userClientCount = mClients.beginBroadcast();
                for (int i = 0; i < userClientCount; i++) {
                    IRadioClient client = mClients.getBroadcastItem(i);
                    try {
                        client.setSearchResult(frequency, effect);
                    } catch (RemoteException re) {
                        /* ignore */
                        re.printStackTrace();
                    }
                }
            } finally {
                mClients.finishBroadcast();
            }
        }

        @Override
        public void onCurrentFrequencyChanged(Frequency frequency) { }

        @Override
        public void onEnd(String endStr) {
            Log.i(TAG, "onEnd....endStr:" + endStr);
            sendScanEnd(endStr);
        }

        @Override
        public void onStereoChanged(boolean isStereoOn) { }

        @Override
        public void onRadioDXChanged(boolean isNearOn) { }

        @Override
        public void onRadioStateChanged(int result) { }

        @Override
        public void onRssiCallback(boolean isRead, List<String> fmCustCfg, int rssi) {

        }
    };

    private final class BinderDeathRecipient implements IBinder.DeathRecipient {
        private IRadioClient mToken = null;

        BinderDeathRecipient(IRadioClient token) {
            Log.i(TAG, "......client token" + token + "," + token.toString());
            mToken = token;
        }
        @Override
        public void binderDied() {
            mClients.unregister(mToken);
        }
    }
}
