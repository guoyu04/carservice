package com.metasequoia.manager.power;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.metasequoia.manager.constent.ProductContext;
import com.metasequoia.services.power.IPowerClient;
import com.metasequoia.services.power.IPowerService;

public class PowerManager {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "PowerManager";
    public static final int DEV_POWER_ON = (1);        //电源开
    public static final int DEV_POWER_OFF = (0);        //电源关闭
    public static final int MSG_ACC_CHANGE = 1;
    public static final int MSG_BACK_SIGN_CHANGE = 2;
    public static final int MSG_ILL_SIGN_CHANGE = 3;
    private static PowerManager mInstance;
    //服务stub
    private static IPowerService mService;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private final Looper mMainLooper;
    /** @see PowerSystemThread */
    private PowerSystemThread mPowerSystemThread;
    /** @see PowerHandler */
    private PowerHandler mPowerHandler;
    /**
     * acc信号监听
     */
    private PowerAccListener mPowerAccListener;

    /**
     * 倒车信号监听
     */
    private PowerBackSignListener mPowerBackSignListener;

    /**
     * 大灯信号监听
     */
    private PowerIllSignListener mPowerIllSignListener;

    /**
     * 客户端代理
     */
    private PowerClient mClient;
    PowerManager(Looper looper) {
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
        createPowerSystemThread();
    }

    /**
     * Retrieve the global PowerManager instance, creating it if it
     * doesn't already exist.
     */
    public static PowerManager getInstance() {
        synchronized (PowerManager.class) {
            if (mInstance == null) {
                mInstance = new PowerManager(Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    /**
     * Private optimization: retrieve the global PowerManager instance,
     * if it exists.
     */
    public static PowerManager peekInstance() {
        return mInstance;
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private  IPowerService getServiceLocked() {
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

        IPowerService service = IPowerService.Stub.asInterface(iBinder);
        try {
            if(mPowerAccListener != null || mPowerBackSignListener != null || mPowerIllSignListener != null){
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
            Log.e(LOG_TAG, "PowerService is dead", re);
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
            case MSG_ACC_CHANGE:
                onRecAccChange(arg);
                break;
            case MSG_BACK_SIGN_CHANGE:
                onRecBackSignChange(arg);
                break;
            case MSG_ILL_SIGN_CHANGE:
                onRecIllSignChange(arg);
                break;
        }
    }

    private void onRecAccChange(int state) {
        if(DEBUG) Log.d(LOG_TAG,"onRecAccChange="+state);
        if(mPowerAccListener != null){
            mPowerAccListener.onPowerAccChange(state);
        }
    }

    private void onRecBackSignChange(int state) {
        if(DEBUG) Log.d(LOG_TAG,"onRecBackSignChange="+state);
        if(mPowerBackSignListener != null){
            mPowerBackSignListener.onPowerBackSignChange(state);
        }
    }

    private void onRecIllSignChange(int state) {
        if(DEBUG) Log.d(LOG_TAG,"onRecIllSignChange="+state);
        if(mPowerIllSignListener != null){
            mPowerIllSignListener.onPowerIllSignChange(state);
        }
    }

    private void addClient2Remote() throws RemoteException{
        if(mClient == null && mService != null){
            mClient = new PowerClient();
            mService.addClient(mClient);
        }
    }

    /**
     * 设置MCU acc 状态信号监听
     * @param listener 监听
     */
    public void setPowerAccListener(PowerAccListener listener){
        mPowerAccListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    /**
     * 设置MCU 倒车信号监听
     * @param listener 监听
     */
    public void setPowerBackSignListener(PowerBackSignListener listener){
        mPowerBackSignListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    /**
     * 设置大灯信号监听
     * @param listener 监听
     */
    public void setPowerIllSignListener(PowerIllSignListener listener){
        mPowerIllSignListener = listener;
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
        return DEV_POWER_ON;
    }
    //MCU acc 状态信号
    public interface PowerAccListener{
        /**
         * MCU acc 状态信号
         * @param state  acc on(Common.DEV_POWER_ON) /  acc off(Common.DEV_POWER_OFF)
         */
        void onPowerAccChange(int state);
    }


    //MCU 倒车信号监听
    public interface PowerBackSignListener{
        /**
         * MCU 倒车信号
         * @param state 倒车开始(Common.DEV_POWER_ON) /  倒车结束(Common.DEV_POWER_OFF)
         */
        void onPowerBackSignChange(int state);
    }

    //大灯信号监听
    public interface PowerIllSignListener{
        /**
         * 大灯信号
         * @param state 大灯开启(Common.DEV_POWER_ON) /  大灯关闭(Common.DEV_POWER_OFF)
         */
        void onPowerIllSignChange(int state);
    }
    /**
     * 服务端回调消息代理对象
     */
    private class PowerClient extends IPowerClient.Stub{
        public void onAccStateChange(int state){
            mPowerHandler.obtainMessage(MSG_ACC_CHANGE, state, 0).sendToTarget();
        }
        public void onBackSignChange(int state){
            mPowerHandler.obtainMessage(MSG_BACK_SIGN_CHANGE, state, 0).sendToTarget();
        }

        public void onIllSignChange(int state){
            mPowerHandler.obtainMessage(MSG_ILL_SIGN_CHANGE, state, 0).sendToTarget();
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

            synchronized(PowerManager.this) {
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
