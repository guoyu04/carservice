package com.metasequoia.manager.radio;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.metasequoia.manager.radio.bean.Frequency;
import com.metasequoia.manager.radio.bean.RadioArea;
import com.metasequoia.manager.radio.listener.OnDataChangeListener;
import com.metasequoia.services.radio.IRadioClient;
import com.metasequoia.services.radio.IRadioService;

import java.util.List;

public class RadioManager {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "RadioManager";
    public static final int MSG_SET_STATE = 1;

    private static RadioManager mInstance;
    //服务stub
    private static IRadioService mService = null;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    OnDataChangeListener mOnDataChangeListener;

    private final Looper mMainLooper;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                dispatchMsg(msg.what,msg.arg1, msg.obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });


    RadioManager(Looper looper) {
        //mService = service;
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
    }

    /**
     * Retrieve the global RadioManager instance, creating it if it
     * doesn't already exist.
     */
    public static RadioManager getInstance(Context context) {
        synchronized (RadioManager.class) {
            if (mInstance == null) {
                Log.i(LOG_TAG, "getInstance getService..");
                IBinder b = ServiceManager.getService("com.metasequoia.services.radio.RadioService");
                mService = IRadioService.Stub.asInterface(b);

                mInstance = new RadioManager(Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    /**
     * Private optimization: retrieve the global RadioManager instance,
     * if it exists.
     */
    public static RadioManager peekInstance() {
        return mInstance;
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private IRadioService getServiceLocked() {
        if (mService == null) {
            Log.i(LOG_TAG, "mService is null retry...");
            tryConnectToServiceLocked();
        }
        return mService;
    }

    /**
     * 尝试连接到服务端，并把代理设置到服务端
     */
    private void tryConnectToServiceLocked() {
        IBinder iBinder = ServiceManager.getService("com.metasequoia.services.radio.RadioService");
        if (iBinder == null) {
            Log.i(LOG_TAG, "iBinder is null return...");
            return;
        }
        IRadioService service = IRadioService.Stub.asInterface(iBinder);
        try {
            final int stateFlags = service.addClient(mClient);
            setStateLocked(stateFlags);
            Log.i(LOG_TAG, "connected to mService");
            mService = service;
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "RadioService is dead", re);
        }
        if(DEBUG) Log.d(LOG_TAG,"tryConnectToServiceLocked:"+mService.toString());
    }

    /**
     * 初始化radio模块
     */
    public int initRadio(){
        IRadioService service = getServiceLocked();
        if(service == null) {
            Log.i(LOG_TAG, "reconnecting.....");
            return -1;
        }
        try {

            service.initRadio(21);
            return 1;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Can't call requestAudioFocus() on RadioService:", e);
            return -1;
        }
    }

    /**
     * while setBand or Area ; init new state of radio
     * @throws RemoteException
     */
    public void init() throws RemoteException {
        mService.init();
    }


    public void play() throws RemoteException{
        mService.play();
    }

    public void AM() throws RemoteException {
        mService.AM();
    }

    public void FM() throws RemoteException {
        mService.FM();
    }

    public int uninitRadio() throws  RemoteException {
        return mService.uninitRadio();
    }

    /**
     * 打开天线电源
     * @return
     * 1 : 打开成功
     * -1 : 打开失败
     * 0 : 不支持此功能
     * @throws RemoteException
     */
    public int openAntenna() throws RemoteException {
        return mService.openAntenna();
    }

    /**
     * 关闭天线电源
     * @return
     * 1 : 打开成功
     * -1 : 打开失败
     * 0 : 不支持此功能
     * @throws RemoteException
     */
    public int closeAnteena() throws RemoteException {
        return mService.closeAntenna();
    }

    /**
     * 设置收音机地区
     * @param areaId
     * @return
     * @throws RemoteException
     */
    public int setAreaId(int areaId) throws RemoteException {
        return mService.setAreaId(areaId);
    }

    /**
     * 步进设置频率(例如：frequency + 10)
     * @throws RemoteException
     */
    public void seek() throws RemoteException {
        mService.seek();
    }

    /**
     * 获取收音机范围信息,FM/AM频点范围，步进大小等
     * @return
     * @throws RemoteException
     */
    public RadioArea[] getArea() throws RemoteException {
        return mService.getArea();
    }


    /**
     * 通过地区id获取收音机范围信息;例如id : PublicDef.RADIO_AREA_CHINA
     * @param areaId
     * @return
     * @throws RemoteException
     */
    public RadioArea[] getAreaById(int areaId) throws RemoteException {
        return mService.getAreaById(areaId);
    }

    /**
     * 搜台是异步操作，实际返回值通过回调
     * @throws RemoteException
     */
    public void search() throws RemoteException {
        mService.search();    //搜台是异步操作，实际返回值通过回调
    }

    /**
     * 向前向后搜台
     * @param direction
     * @throws RemoteException
     */
    public void searchByDirection(boolean direction) throws RemoteException {
        mService.searchByDirection(direction);  //搜台是异步操作，实际返回值通过回调
    }

    /**
     * stop scan
     * @return
     * 1: stop succ
     * -1 : not scanning or request error
     * 0 : stop false
     * @throws RemoteException
     */
    public int stopSearch() throws RemoteException {
        return  mService.stopSearch();
    }

    /**
     * 设置立体声
     * @param enable
     * true ： open
     * false ： close
     * @throws RemoteException
     */
    public void setStereo(boolean enable) throws RemoteException {
         mService.setStereo(enable);
    }

    /**
     * 设置远程/近程
     * @param dx
     * 0 : 远程
     * 1 : 近程
     * @throws RemoteException
     */
    public void setRadioDX(int dx) throws RemoteException {
        mService.setRadioDX(dx);
    }

    /**
     * get whether stereo is support; stereo : 立体声
     * @return
     * true : support
     * false : not support
     * @throws RemoteException
     */
    public boolean isSupportStereo() throws RemoteException {
        return  mService.isSupportStereo();
    }

    /**
     * 获取立体声信息
     * @return
     * true : 打开
     * false : 关闭
     * @throws RemoteException
     */
    public boolean getStereo() throws RemoteException {
        return mService.getStereo();
    }

    /**
     * 获取远近程信息
     * @return
     * true : 远程
     * false : 近程
     * @throws RemoteException
     */
    public boolean getRadioDX() throws RemoteException {
        return mService.getRadioDX();
    }

    /**
     * get whether RadioDX is support ; DX : 远程/近程
     * @return
     * true : support
     * false : not support
     */
    public boolean isSupportRadioDX() {
        return false;
    }

    /**
     * get the current scanning state;
     * @return
     * true : scanning
     * false : not scan
     * @throws RemoteException
     */
    public boolean isScanning() throws RemoteException {
        return mService.isScanning();
    }

    public void setFrequency(int freq) throws RemoteException {
        mService.setFrequency(freq);
    }

    /**
     * get current BandType : AM or FM
     * @return
     * PublicDef.RADIO_FM or PublicDef.RADIO_AM
     * @throws RemoteException
     */
    public int getBand() throws RemoteException {
        return mService.getBand();
    }

    /**
     * set Volume
     * @param vol
     * @return
     * 1 : success
     * -1 : false
     * 0 : not support
     */
    public int setVolume(int vol) throws RemoteException {
        return mService.setVolume(vol);        /*** 是否有效？？？？？？  C中Vol是bandid of am or fm  ***/
    }

    /**
     * set Mute true or false; can use to pause radio
     * @param mute
     * true : mute
     * false : close mute
     * @return
     * 1 : success
     * -1 : setMute false
     * 0 : not support
     * @throws RemoteException
     */
    public int setMute(boolean mute) throws RemoteException {
        return mService.setMute(mute);
    }

    /**
     * is support band type (am, fm...)
     * @param bandType
     * PublicDef.RADIO_FM, PublicDef.RADIO_AM
     * @return
     * true : support
     * false : not support
     */
    public boolean isSupportBand(int bandType) throws RemoteException {
        return mService.isSupportBand(bandType);
    }

    /**
     * while scanning ,is callback points real-time?
     * @return
     * true : callback points real-time
     * false : not callback
     */
    public boolean isSupportScaningCallback() throws RemoteException {
        return mService.isSupportScaningCallback();
    }


//    /**
//     * 获取频点信号状态，获取频点是否有效
//     * @param freq
//     * @return
//     * @throws RemoteException
//     */
//    public int getSignalStatus(int freq) throws RemoteException {
//        return mService.getSignalStatus(freq);
//    }

    /**
     * 是否支持设置信号强度
     * @return
     * true : 支持
     * false : 不支持
     */
    public boolean isSupportSetRssi() throws RemoteException {
        return mService.isSupportSetRssi();
    }

    /**
     * 获取收音机信号强度，结果以回调方式返回
     */
    public void getRssi() throws RemoteException {
        mService.getRssi();
    }

    /**
     * 设置信号强度，结果以回调方式返回
     * @param fmCustCfgs
     * @param values
     */
    public void setRssi(List<String> fmCustCfgs, int values) throws RemoteException {
        mService.setRssi(fmCustCfgs, values);
    }

    /**
     * 是否支持开关天线电源
     * @return
     * true : 支持
     * false : 不支持
     */
    public boolean isSupportAntenna() throws RemoteException {
        return mService.isSupportAntenna();
    }

    /**
     * 是否支持设置收音机信号强度
     * @return
     * true : 支持
     * false : 不支持
     */
    public boolean isSupportRssi() throws RemoteException {
        return mService.isSupportRssi();
    }

    /**
     * get Antenna states
     * @return
     * open : open
     * close : close
     * unknown : can not get states..
     */
    public String getAntennaState() throws RemoteException {
        return mService.getAntennaState();
    }

    /**
     * 获取频道列表
     * @return
     * @throws RemoteException
     */
    public Frequency[] getPrefabPrequency() throws RemoteException {
        Log.i(LOG_TAG, "getPrefabPrequency");
        return mService.getPrefabFrequency();
    }

    /**
     * get the current playing frequency
     * @return
     * @throws RemoteException
     */
    public Frequency getCurrentFrequency() throws RemoteException {
        return  mService.getCurrentFrequency();
    }

    /**
     * save RadioInfo to back service
     * @param frequency
     * @param exString
     */
    public void saveRadioInfo(Frequency frequency, String exString) { }

    /**
     * 处理消息
     * @param what 消息what
     * @param arg 消息obj
     */
    private void dispatchMsg(int what, int arg, Object obj){
        Log.i(LOG_TAG, "dispatchMsg....what:" + what +",arg:" + arg + ",obj:" + obj.toString());
        switch (what) {

            case MSG_SET_STATE:

                setStateLocked(arg);
                break;
        }
    }

    private void setStateLocked(int stateFlags) {
        if(DEBUG) Log.d(LOG_TAG,"setStateLocked="+stateFlags);
    }

    public void setOnSearchListener(OnDataChangeListener onSearchListener) {
        mOnDataChangeListener = onSearchListener;
    }

    /**
     * 服务端回调消息代理对象
     */
    private final IRadioClient.Stub mClient = new IRadioClient.Stub() {
        public void setState(int state) {
            // We do not want to change this immediately as the applicatoin may
            // have already checked that accessibility is on and fired an event,
            // that is now propagating up the view tree, Hence, if accessibility
            // is now off an exception will be thrown. We want to have the exception
            // enforcement to guard against apps that fire unnecessary accessibility
            // events when accessibility is off.
            //mHandler.obtainMessage(MSG_SET_STATE, state, 0).sendToTarget();
            mOnDataChangeListener.onRadioStateChanged(state);
        }

        public void setSearchResult(Frequency fre, int effect) {
            Log.i(LOG_TAG, "manager... setTest fre:" + fre + ",effect:" + effect);
            mOnDataChangeListener.onScanChanged(fre, effect);
        }

        @Override
        public void setCurrentFrequencyChange(Frequency frequency) throws RemoteException {
            Log.i(LOG_TAG, "setCurrentFrequencyChange frequency:" + frequency);
            mOnDataChangeListener.onCurrentFrequencyChanged(frequency);
        }

        @Override
        public void setSearchEnd(String result) throws RemoteException {
            Log.i(LOG_TAG, "setSearchEnd result:" + result);
            mOnDataChangeListener.onEnd(result);
        }

        @Override
        public void setIsStereoOn(boolean isStereoOn) throws RemoteException {
            Log.i(LOG_TAG, "setIsStereoOn " + isStereoOn);
            mOnDataChangeListener.onStereoChanged(isStereoOn);
        }

        @Override
        public void setIsRadioDX(boolean isNearOn) throws RemoteException {
            Log.i(LOG_TAG, "setIsRadioDX " + isNearOn);
            mOnDataChangeListener.onRadioDXChanged(isNearOn);
        }
    };


}
