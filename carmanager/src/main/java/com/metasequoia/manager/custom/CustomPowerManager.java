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
import com.metasequoia.services.custom.ICustomPowerClient;
import com.metasequoia.services.custom.ICustomPowerService;

public class CustomPowerManager {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "CustomPowerManager";
    public static final int DEV_POWER_ON = (1);        //电源开
    public static final int DEV_POWER_OFF = (0);        //电源关闭
    public static final int MSG_REC_ACC_STATE = 2;
    public static final int MSG_REC_BAT_POWER = 3;
    public static final int MSG_REC_BAT_STATE = 4;
    public static final int MSG_REC_CAR_GEAR = 6;

    public static final int MSG_REC_CAR_SPEED = 7;//车速
    public static final int MSG_REC_ROTATE_SPEED= 8;//转速
    public static final int MSG_REC_TOTAL_MIL= 9;//总里程
    public static final int MSG_REC_REM_MIL= 10;//剩余里程

    public static final int MSG_REC_DEV_ID= 11;//device id

    public static final int MSG_REC_BAT_VOLTAGE = 12;
    private static CustomPowerManager mInstance;
    //服务stub
    private static ICustomPowerService mService;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private final Looper mMainLooper;
    /** @see PowerSystemThread */
    private PowerSystemThread mPowerSystemThread;
    /** @see PowerHandler */
    private PowerHandler mPowerHandler;

    private McuPowerListener mMcuPowerListener;

    /**
     * 客户端代理
     */
    private CustomPowerClient mClient;
    CustomPowerManager(Looper looper) {
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
        createPowerSystemThread();
    }

    /**
     * Retrieve the global CustomPowerManager instance, creating it if it
     * doesn't already exist.
     */
    public static CustomPowerManager getInstance() {
        synchronized (CustomPowerManager.class) {
            if (mInstance == null) {
                mInstance = new CustomPowerManager(Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    /**
     * Private optimization: retrieve the global CustomPowerManager instance,
     * if it exists.
     */
    public static CustomPowerManager peekInstance() {
        return mInstance;
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private  ICustomPowerService getServiceLocked() {
        if (mService == null) {
            tryConnectToServiceLocked();
        }
        return mService;
    }

    /**
     * 尝试连接到服务端，并把代理设置到服务端
     */
    private void tryConnectToServiceLocked() {
        IBinder iBinder = ServiceManager.getService(ProductContext.SERVICE_NAME_POWER);
        if (iBinder == null) {
            return;
        }

        ICustomPowerService service = ICustomPowerService.Stub.asInterface(iBinder);
        try {
            if(mMcuPowerListener != null){
                addClient2Remote();
            }
            mService = service;
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
            Log.e(LOG_TAG, "CustomPowerService is dead", re);
            mService = null;
            mClient = null;
        }
        if(DEBUG) Log.d(LOG_TAG,"tryConnectToServiceLocked:"+mService.toString());
    }



    /**
     * 处理消息
     * @param what 消息what
     * @param arg 消息obj
     */
    private void dispatchMsg(int what, int arg, Object obj){
        switch (what) {
            case MSG_REC_ACC_STATE:
                onRecAccChange(arg);
                break;
            case MSG_REC_BAT_POWER:
                onRecBatteryPower((Float) obj);
                break;
            case MSG_REC_BAT_STATE:
                onRecBatteryState(arg);
                break;
            case MSG_REC_TOTAL_MIL:
                onRecTotalMileage(arg);
                break;
            case MSG_REC_REM_MIL:
                onRecRemMileage(arg);
                break;
            case MSG_REC_DEV_ID:
                onRecRemMileage(arg);
                break;
            case MSG_REC_BAT_VOLTAGE:
                break;
        }
    }

    private void onRecAccChange(int state) {
        if(DEBUG) Log.d(LOG_TAG,"onRecAccChange="+state);
        if(mMcuPowerListener != null){
            mMcuPowerListener.onAccStatusChange(state);
        }
    }

    /**
     * mcu电池电量
     * @param battery
     */
    private void onRecBatteryPower(float battery){
        if(DEBUG) Log.d(LOG_TAG,"onRecBatteryPower="+battery);
        if(mMcuPowerListener != null){
            mMcuPowerListener.onBatteryPower(battery);
        }
    }

    /**
     * mcu电池状态
     * @param state
     */
    private void onRecBatteryState(int state){
        if(DEBUG) Log.d(LOG_TAG,"onRecBatteryState="+state);
        if(mMcuPowerListener != null){
            mMcuPowerListener.onBatteryState(state);
        }
    }

    /**
     * mcu总里程
     * @param mileage 总里程
     */
    private void onRecTotalMileage(int mileage){
        if(DEBUG) Log.d(LOG_TAG,"onRecTotalMileage="+mileage);
        if(mMcuPowerListener != null){
            mMcuPowerListener.onTotalMileage(mileage);
        }
    }

    /**
     * mcu剩余里程
     * @param mileage 剩余里程
     */
    private void onRecRemMileage(int mileage){
        if(DEBUG) Log.d(LOG_TAG,"onRecRemMileage="+mileage);
        if(mMcuPowerListener != null){
            mMcuPowerListener.onRemMileage(mileage);
        }
    }

    /**
     * mcu device id
     * @param devID id
     */
    private void onRecDeviceID(String devID){
        if(DEBUG) Log.d(LOG_TAG,"onRecDeviceID="+devID);
        if(mMcuPowerListener != null){
            mMcuPowerListener.onDeviceID(devID);
        }
    }

    /**
     * mcu电池电压
     * @param voltage 电压
     */
    private void onRecBatteryVoltage(float voltage){
        if(DEBUG) Log.d(LOG_TAG,"onRecBatteryVoltage="+voltage);
        if(mMcuPowerListener != null){
            mMcuPowerListener.onBatteryVoltage(voltage);
        }
    }

    private void addClient2Remote() throws RemoteException{
        if(mClient == null && mService != null){
            mClient = new CustomPowerClient();
            mService.addClient(mClient);
        }
    }

    /**
     * 设置电源相关监听
     * @param listener 监听
     */
    public void setMcuPowerListener(McuPowerListener listener){
        mMcuPowerListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    /**
     * 获取acc状态
     * @return DEV_POWER_ON 开启  DEV_POWER_OFF 关闭
     */
    public int getAccState(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getAccState();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.ACC_STATE_OFF;
    }

    /**
     * 获取剩余电量
     * @return 剩余电量
     */
    public float getBatteryPower(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getBatteryPower();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 获取电池状态
     * @return 电池状态
     */
    public int getBatteryState(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getBatteryState();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    /**
     * 获取总里程
     * @return 总里程
     */
    public int getTotalMileage(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getTotalMileage();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 获取剩余里程
     * @return 剩余里程
     */
    public int getRemMileage() {
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getRemMileage();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取device id
     * @return id
     */
    public String getDeviceID(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getDeviceID();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取电瓶电压
     * @return 电瓶电压
     */
    public float getBatteryVoltage(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getBatteryVoltage();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    public interface McuPowerListener {
        /**
         * acc状态
         * @param state Common.ACC_STATE_OFF Common.ACC_STATE_ACC Common.ACC_STATE_START
         */
        void onAccStatusChange(int state);
        /**
         * mcu电池电量
         * @param battery
         */
        void onBatteryPower(float battery);

        /**
         * mcu电池状态
         * @param state
         */
        void onBatteryState(int state);

        /**
         * mcu总里程
         * @param mileage 总里程
         */
        void onTotalMileage(int mileage);

        /**
         * mcu剩余里程
         * @param mileage 剩余里程
         */
        void onRemMileage(int mileage);

        /**
         * device id
         * @param devID id
         */
        void onDeviceID(String devID);

        /**
         * mcu电池电压
         * @param voltage
         */
        void onBatteryVoltage(float voltage);
    }
    /**
     * 服务端回调消息代理对象
     */
    private class CustomPowerClient extends ICustomPowerClient.Stub{
        public void onAccStateChange(int state) {
            mPowerHandler.obtainMessage(MSG_REC_ACC_STATE, state, 0).sendToTarget();
        }

        /**
         * mcu电池电量
         * @param battery
         */
        public void onBatteryPower(float battery){
            mPowerHandler.obtainMessage(MSG_REC_BAT_POWER, 0, 0,(Float)battery).sendToTarget();
        }
        /**
         * mcu电池状态
         * @param state
         */
        public void onBatteryState(int state){
            mPowerHandler.obtainMessage(MSG_REC_BAT_STATE, state, 0).sendToTarget();
        }
        /**
         * mcu总里程
         * @param mileage 总里程
         */
        public void onTotalMileage(int mileage){
            mPowerHandler.obtainMessage(MSG_REC_TOTAL_MIL, mileage, 0).sendToTarget();
        }

        /**
         * mcu剩余里程
         * @param mileage 剩余里程
         */
        public void onRemMileage(int mileage){
            mPowerHandler.obtainMessage(MSG_REC_REM_MIL, mileage, 0).sendToTarget();
        }

        /**
         * device id
         * @param devID id
         */
        public void onDeviceID(String devID){
            mPowerHandler.obtainMessage(MSG_REC_DEV_ID, 0, 0, devID).sendToTarget();
        }

        /**
         * mcu电池电压
         * @param voltage 电压
         */
        public void onBatteryVoltage(float voltage){
            mPowerHandler.obtainMessage(MSG_REC_BAT_VOLTAGE, 0, 0,(Float)voltage).sendToTarget();
        }
    }

    //----------------------------------------------------------------------------------------------
    //Power handler
    private void createPowerSystemThread() {
        mPowerSystemThread = new PowerSystemThread();
        mPowerSystemThread.start();
    }

    /** Thread that handles native PowerSystem control. */
    private class PowerSystemThread extends Thread {
        PowerSystemThread() {
            super("PowerService");
        }

        @Override
        public void run() {
            // Set this thread up so the handler will work on it
            Looper.prepare();

            synchronized(CustomPowerManager.this) {
                mPowerHandler = new PowerHandler();
            }

            // Listen for volume change requests that are set by VolumePanel
            Looper.loop();
        }
    }

    /** Handles internal volume messages in separate volume thread. */
    private class PowerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                dispatchMsg(msg.what,msg.arg1, msg.obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
