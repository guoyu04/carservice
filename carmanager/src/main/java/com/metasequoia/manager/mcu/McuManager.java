package com.metasequoia.manager.mcu;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.metasequoia.manager.constent.Common;
import com.metasequoia.manager.constent.ProductContext;
import com.metasequoia.services.hard.IMcuHardClient;
import com.metasequoia.services.hard.IMcuHardService;

public class McuManager {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "McuManager";
    public static final int MSG_MCU_VERSION = 1;
    public static final int MSG_MCU_UPDATE_STATE = 2;
    private static McuManager mInstance;
    //服务stub
    private static IMcuHardService mService;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private final Looper mMainLooper;
    /** @see McuSystemThread */
    private McuSystemThread mMcuThread;
    /** @see McuHandler */
    private McuHandler mMcuHandler;
    /**
     * acc信号监听
     */
    private McuVersionListener mMcuVersionListener;

    /**
     * 倒车信号监听
     */
    private McuUpdateStateListener mMcuUpdateStateListener;


    /**
     * 客户端代理
     */
    private McuHardClient mClient;
    McuManager(Looper looper) {
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
        createMcuThread();
    }

    /**
     * Retrieve the global PowerManager instance, creating it if it
     * doesn't already exist.
     */
    public static McuManager getInstance() {
        synchronized (McuManager.class) {
            if (mInstance == null) {
                mInstance = new McuManager(Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    /**
     * Private optimization: retrieve the global PowerManager instance,
     * if it exists.
     */
    public static McuManager peekInstance() {
        return mInstance;
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private  IMcuHardService getServiceLocked() {
        if (mService == null) {
            tryConnectToServiceLocked();
        }
        return mService;
    }

    /**
     * 尝试连接到服务端，并把代理设置到服务端
     */
    private void tryConnectToServiceLocked() {
        IBinder iBinder = ServiceManager.getService(ProductContext.SERVICE_NAME_MCU);
        if (iBinder == null) {
            return;
        }

        IMcuHardService service = IMcuHardService.Stub.asInterface(iBinder);
        try {
            if(mMcuVersionListener != null || mMcuUpdateStateListener != null ){
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
            Log.e(LOG_TAG, "McuManager is dead", re);
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
            case MSG_MCU_VERSION:
                onRecMcuVersion((MCUVersionInfo)obj);
                break;
            case MSG_MCU_UPDATE_STATE:
                onRecMcuUpdateState(arg);
                break;
        }
    }

    private void onRecMcuVersion(MCUVersionInfo versionInfo) {
        if(DEBUG) Log.d(LOG_TAG,"onRecMcuVersion softVersion="+versionInfo.softVersion + "; hardVersion"+versionInfo.hardVersion);
        if(mMcuVersionListener != null){
            mMcuVersionListener.onMcuVersionInfo(versionInfo);
        }
    }

    private void onRecMcuUpdateState(int state) {
        if(DEBUG) Log.d(LOG_TAG,"onRecMcuUpdateState="+state);
        if(mMcuUpdateStateListener != null){
            mMcuUpdateStateListener.onMcuUpdateState(state);
        }
    }

    private void addClient2Remote() throws RemoteException{
        if(mClient == null && mService != null){
            mClient = new McuHardClient();
            mService.addClient(mClient);
        }
    }

    /**
     * 设置MCU 版本监听
     * @param listener 监听
     */
    public void setMcuVersionListener(McuVersionListener listener){
        mMcuVersionListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    /**
     * 设置MCU 版本更新状态监听
     * @param listener 监听
     */
    public void setMcuUpdateStateListener(McuUpdateStateListener listener){
        mMcuUpdateStateListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }

    /**
     * 请求MCU版本信息，异步回调形式返回
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int requestMcuVersion(){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().requestMcuVersion();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    /**
     * 请求mcu更新
     * @param binPath mcu文件路径
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int requestMcuUpdate(String binPath){
        try {
            if(getServiceLocked() != null){
                return getServiceLocked().requestMcuUpdate(binPath);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    //MCU version 监听
    public interface McuVersionListener{
        void onMcuVersionInfo(MCUVersionInfo info);
    }


    //MCU 更新状态状态监听
    public interface McuUpdateStateListener {
        void onMcuUpdateState(int state);
    }

    /**
     * 服务端回调消息代理对象
     */
    private class McuHardClient extends IMcuHardClient.Stub{
        @Override
        public void onMcuVersionInfo(String softVersion, String hardVersion){
            mMcuHandler.obtainMessage(MSG_MCU_VERSION, 0, 0,new MCUVersionInfo(softVersion,hardVersion)).sendToTarget();
        }

        @Override
        public void onMcuUpdateState(int state){
            mMcuHandler.obtainMessage(MSG_MCU_UPDATE_STATE, state, 0).sendToTarget();
        }
    }

    //----------------------------------------------------------------------------------------------
    //Power handler
    private void createMcuThread() {
        mMcuThread = new McuSystemThread();
        mMcuThread.start();
    }

    /** Thread that handles native PowerSystem control. */
    private class McuSystemThread extends Thread {
        McuSystemThread() {
            super("McuHardService");
        }

        @Override
        public void run() {
            // Set this thread up so the handler will work on it
            Looper.prepare();

            synchronized(McuManager.this) {
                mMcuHandler = new McuHandler();
            }

            // Listen for volume change requests that are set by VolumePanel
            Looper.loop();
        }
    }

    /** Handles internal volume messages in separate volume thread. */
    private class McuHandler extends Handler {
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
