package com.metasequoia.manager.fm;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.metasequoia.manager.constent.Common;
import com.metasequoia.manager.constent.ProductContext;
import com.metasequoia.services.fm.IFMClient;
import com.metasequoia.services.fm.IFMService;

public class FMManager {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "FMManager";
    public static final int DEV_POWER_ON = (1);        //电源开
    public static final int DEV_POWER_OFF = (0);        //电源关闭
    public static final int MSG_FREQ_CHANGE = 1;
    public static final int MSG_POWER_STATUS = 2;
    private static FMManager mInstance;
    //服务stub
    private static IFMService mService;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private final Looper mMainLooper;
    /** @see FMSystemThread */
    private FMSystemThread mFMSystemThread;
    /** @see FMHandler */
    private FMHandler mFMHandler;
    /**
     * freq监听
     */
    private FMFreqListener mFMFreqListener;

    private FMPowerListener mFMPowerListener;
    /**
     * 客户端代理
     */
    private FMClient mClient;
    FMManager(Looper looper) {
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
        createFMSystemThread();
    }

    /**
     * Retrieve the global FMManager instance, creating it if it
     * doesn't already exist.
     */
    public static FMManager getInstance() {
        synchronized (FMManager.class) {
            if (mInstance == null) {
                mInstance = new FMManager(Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    /**
     * Private optimization: retrieve the global FMManager instance,
     * if it exists.
     */
    public static FMManager peekInstance() {
        return mInstance;
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private  IFMService getServiceLocked() {
        if (mService == null) {
            tryConnectToServiceLocked();
        }
        return mService;
    }

    /**
     * 尝试连接到服务端，并把代理设置到服务端
     */
    private void tryConnectToServiceLocked() {
        IBinder iBinder = ServiceManager.getService(ProductContext.SERVICE_NAME_FM);
        if (iBinder == null) {
            return;
        }

        IFMService service = IFMService.Stub.asInterface(iBinder);
        try {
            if(mFMFreqListener != null || mFMPowerListener != null){
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
            Log.e(LOG_TAG, "FMService is dead", re);
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
            case MSG_FREQ_CHANGE:
                onRecFreqChange(arg);
                break;
            case MSG_POWER_STATUS:
                onRecPowerStatus(arg);
                break;
        }
    }

    private void onRecFreqChange(int freq) {
        if(DEBUG) Log.d(LOG_TAG,"onRecFreqChange="+freq);
        if(mFMFreqListener != null){
            mFMFreqListener.onFMFreqChange(freq);
        }
    }

    private void onRecPowerStatus(int state) {
        if(DEBUG) Log.d(LOG_TAG,"onRecPowerStatus="+state);
        if(mFMPowerListener != null){
            mFMPowerListener.onFMPowerStatus(state);
        }
    }

    private void addClient2Remote() throws RemoteException{
        if(mClient == null && mService != null){
            mClient = new FMClient();
            mService.addClient(mClient);
        }
    }

    /**
     * 设置MCU fm freq监听
     * @param listener 监听
     */
    public void setFMFreqListener(FMFreqListener listener){
        mFMFreqListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    /**
     * 设置MCU fm freq监听
     * @param listener 监听
     */
    public void setFMPowerListener(FMPowerListener listener){
        mFMPowerListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    public void openFMPower(){
        try {
            if(getServiceLocked() != null){
                getServiceLocked().openFMPower();
                return;
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        if(mFMPowerListener != null){
            mFMPowerListener.onFMPowerStatus(Common.RTN_FAIL);
        }
    }
    public void closeFMPower(){
        try {
            if(getServiceLocked() != null){
                getServiceLocked().closeFMPower();
                return;
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        if(mFMPowerListener != null){
            mFMPowerListener.onFMPowerStatus(Common.RTN_FAIL);
        }
    }

    public int setFreq(int freq){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().setFreq(freq);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 获取FM freq值
     * @return freq ，0未设置
     */
    public int getFreq(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().getFreq();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return 0;
    }
    //freq监听
    public interface FMFreqListener {
        void onFMFreqChange(int state);
    }

    //freq监听
    public interface FMPowerListener {
        void onFMPowerStatus(int state);
    }
    /**
     * 服务端回调消息代理对象
     */
    private class FMClient extends IFMClient.Stub{
        @Override
        public void onFreqChange(int freq) throws RemoteException {
            mFMHandler.obtainMessage(MSG_FREQ_CHANGE, freq, 0).sendToTarget();
        }

        @Override
        public void onPowerStatus(int status) throws RemoteException {
            mFMHandler.obtainMessage(MSG_POWER_STATUS, status, 0).sendToTarget();
        }
    }

    //----------------------------------------------------------------------------------------------
    //FM handler
    private void createFMSystemThread() {
        mFMSystemThread = new FMSystemThread();
        mFMSystemThread.start();
    }

    /** Thread that handles native FMSystem control. */
    private class FMSystemThread extends Thread {
        FMSystemThread() {
            super("FMService");
        }

        @Override
        public void run() {
            // Set this thread up so the handler will work on it
            Looper.prepare();

            synchronized(FMManager.this) {
                mFMHandler = new FMHandler();
            }

            // Listen for volume change requests that are set by VolumePanel
            Looper.loop();
        }
    }

    /** Handles internal volume messages in separate volume thread. */
    private class FMHandler extends Handler {
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
