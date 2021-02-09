package com.metasequoia.manager.custom;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.metasequoia.manager.constent.Common;
import com.metasequoia.manager.constent.ProductContext;
import com.metasequoia.services.custom.ICustomCarSysClient;
import com.metasequoia.services.custom.ICustomCarSysService;


public class CustomCarSysManager {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "CustomCarSysManager";
    public static final int MSG_REC_ACC_STATE = 2;
    public static final int MSG_REC_BAT_POWER = 3;
    public static final int MSG_REC_BAT_STATE = 4;
    public static final int MSG_REC_CAR_GEAR = 6;

    public static final int MSG_REC_CAR_SPEED = 7;//车速
    public static final int MSG_REC_ROTATE_SPEED= 8;//转速
    public static final int MSG_REC_TOTAL_MIL= 9;//总里程
    public static final int MSG_REC_REM_MIL= 10;//剩余里程
    public static final int MSG_REC_KEY_EVENT= 11;//按键事件
    private static CustomCarSysManager mInstance;
    //服务stub
    private static ICustomCarSysService mService;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private final Looper mMainLooper;
    /** @see SysManagerThread */
    private SysManagerThread mSystemThread;
    /** @see SysManagerHandler */
    private SysManagerHandler mSysManagerHandler;
    private CarRunningListener mCarRunningListener;
    private CustomCarSysClient mClient;
    CustomCarSysManager(Looper looper) {
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
        createSystemThread();
    }

    /**
     * Retrieve the global CustomCarSysManager instance, creating it if it
     * doesn't already exist.
     */
    public static CustomCarSysManager getInstance() {
        synchronized (CustomCarSysManager.class) {
            if (mInstance == null) {
                mInstance = new CustomCarSysManager(Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    /**
     * Private optimization: retrieve the global CustomCarSysManager instance,
     * if it exists.
     */
    public static CustomCarSysManager peekInstance() {
        return mInstance;
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private  ICustomCarSysService getServiceLocked() {
        if (mService == null) {
            tryConnectToServiceLocked();
        }
        return mService;
    }

    /**
     * 尝试连接到服务端，并把代理设置到服务端
     */
    private void tryConnectToServiceLocked() {
        IBinder iBinder = ServiceManager.getService(ProductContext.SERVICE_NAME_CUSTOM_SYS);
        if (iBinder == null) {
            return;
        }

        ICustomCarSysService service = ICustomCarSysService.Stub.asInterface(iBinder);
        try {
            mService = service;
            if(mCarRunningListener != null){
                addClient2Remote();
            }
            //添加死亡通知
            iBinder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    if(mService != null){
                        mService = null;
                        mClient = null;
                    }
                }
            }, 0);
        } catch (RemoteException re) {
            Log.e(LOG_TAG, "CustomCarSysService is dead", re);
            mService = null;
            mClient = null;
        }
        if(DEBUG) Log.d(LOG_TAG,"tryConnectToServiceLocked:"+mService.toString());
    }

    private void addClient2Remote() throws RemoteException{
        if(mClient == null && mService != null){
            mClient = new CustomCarSysClient();
            mService.addClient(mClient);
        }
    }

    /**
     * 处理消息
     * @param what 消息what
     * @param arg1 消息obj
     */
    private void dispatchMsg(int what, int arg1, int arg2, Object obj){
        switch (what) {
            case MSG_REC_CAR_GEAR:
                onRecCarGear(arg1);
                break;
            case MSG_REC_CAR_SPEED:
                onRecCarSpeed((Float)obj);
                break;
            case MSG_REC_ROTATE_SPEED:
                onRecRotateSpeed(arg1);
                break;
            case MSG_REC_KEY_EVENT:
                onRecKeyEvent(arg1, arg2);
                break;
        }
    }


    /**
     * mcu档位状态
     * @param gear mcu档位
     */
    private void onRecCarGear(int gear){
        if(DEBUG) Log.d(LOG_TAG,"onRecCarGear="+gear);
        if(mCarRunningListener != null){
            mCarRunningListener.onCarGear(gear);
        }

    }

    /**
     * mcu车速
     * @param speed 车速
     */
    private void onRecCarSpeed(float speed){
        if(DEBUG) Log.d(LOG_TAG,"onRecCarSpeed="+speed);
        if(mCarRunningListener != null){
            mCarRunningListener.onCarSpeed(speed);
        }
    }
    /**
     * mcu转速
     * @param speed 转速
     */
    private void onRecRotateSpeed(int speed){
        if(DEBUG) Log.d(LOG_TAG,"onRecRotateSpeed="+speed);
        if(mCarRunningListener != null){
            mCarRunningListener.onRotateSpeed(speed);
        }
    }

    /**
     * 一键报警按键 1松开，0按下
     * @param key 按键
     * @param state 状态
     */
    private void onRecKeyEvent(int key, int state){
        if(DEBUG) Log.d(LOG_TAG,"onRecKeyEvent key="+key+";state="+state);
        if(mCarRunningListener != null){
            mCarRunningListener.onKeyEvent(key, state);
        }
    }

    /**
     * 发送语音
     * @param data 语音
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int sendSpeak(String data){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG,"sendSpeak="+data);
                return getServiceLocked().sendSpeak(data);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 设置导航打开状态
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setNavOpenState(boolean isOpen) {
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG,"setNavOpenState="+isOpen);
               return getServiceLocked().setNavOpenState(isOpen);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 设置蓝牙打开状态
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setBleOpenState(boolean isOpen) {
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG,"setBleOpenState:isOpen="+isOpen);
                return getServiceLocked().setBleOpenState(isOpen);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 设置FM打开状态
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setFMOpenState(boolean isOpen){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG, "setFMOpenState:isOpen="+isOpen);
                return getServiceLocked().setFMOpenState(isOpen);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    /**
     * 设置人脸认证状态
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setFaceState(int state){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG, "setFaceState:state="+state);
                return getServiceLocked().setFaceState(state);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    /**
     * 设置评分等级
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setGrading(int grade){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG, "setGrading:grade="+grade);
                return getServiceLocked().setGrading(grade);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    /**
     * 目的地周边优惠信息
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setDestPerInfo(int type){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG, "setDestPerInfo:type="+type);
                return getServiceLocked().setDestPerInfo(type);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    /**
     * 订单信息
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setOrderInfo(int type){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG, "setOrderInfo:type="+type);
                return getServiceLocked().setOrderInfo(type);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 获取档位状态
     * @return 位状态
     */
    public int getCarGear() {
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getCarGear();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 获取车速
     * @return 车速
     */
    public float getCarSpeed(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getCarSpeed();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 获取转速
     * @return 转速
     */
    public int getRotateSpeed(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getRotateSpeed();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    /**
     * 设置系统运行状态监听
     * @param listener 监听
     */
    public void setCarRunningListener(CarRunningListener listener){
        mCarRunningListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    public interface CarRunningListener {
        /**
         * mcu档位状态
         * @param gear mcu档位
         */
        void onCarGear(int gear);

        /**
         * mcu车速
         * @param speed 车速
         */
        void onCarSpeed(float speed);
        /**
         * mcu转速
         * @param speed 转速
         */
        void onRotateSpeed(int speed);

        /**
         * 一键报警按键 1松开，0按下
         * @param key 按键
         * @param state 状态
         */
        void onKeyEvent(int key, int state);
    }
    
    /**
     * 服务端回调消息代理对象
     */
    private class CustomCarSysClient extends ICustomCarSysClient.Stub{
        /**
         * mcu档位状态 Common.CAR_GEAR_P Common.CAR_GEAR_R Common.CAR_GEAR_N Common.CAR_GEAR_F
         * @param gear mcu档位
         */
        public void onCarGear(int gear){
            mSysManagerHandler.obtainMessage(MSG_REC_CAR_GEAR, gear, 0).sendToTarget();
        }

        /**
         * mcu车速
         * @param speed 车速
         */
        public void onCarSpeed(float speed){
            mSysManagerHandler.obtainMessage(MSG_REC_CAR_SPEED, 0, 0, speed).sendToTarget();
        }
        /**
         * mcu转速
         * @param speed 转速
         */
        public void onRotateSpeed(int speed){
            mSysManagerHandler.obtainMessage(MSG_REC_ROTATE_SPEED, speed, 0).sendToTarget();
        }

        /**
         * 一键报警按键
         * @param key 按键 01 key1 02 key2 03 key3
         * @param state 状态 Common.KEY_STATE_UP松开，Common.KEY_STATE_DOWN按下
         */
        public void onKeyEvent(int key, int state){
            mSysManagerHandler.obtainMessage(MSG_REC_KEY_EVENT, key, state).sendToTarget();
        }
    }

    //----------------------------------------------------------------------------------------------
    //handler
    private void createSystemThread() {
        mSystemThread = new SysManagerThread();
        mSystemThread.start();
    }

    /** Thread that handles native System control. */
    private class SysManagerThread extends Thread {
        SysManagerThread() {
            super("SysManagerThread");
        }

        @Override
        public void run() {
            // Set this thread up so the handler will work on it
            Looper.prepare();

            synchronized(CustomCarSysManager.this) {
                mSysManagerHandler = new SysManagerHandler();
            }

            // Listen for volume change requests that are set by VolumePanel
            Looper.loop();
        }
    }

    /** Handles internal volume messages in separate volume thread. */
    private class SysManagerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                dispatchMsg(msg.what,msg.arg1, msg.arg2, msg.obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

