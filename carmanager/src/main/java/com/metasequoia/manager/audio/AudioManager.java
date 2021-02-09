package com.metasequoia.manager.audio;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.metasequoia.manager.constent.Common;
import com.metasequoia.manager.constent.ProductContext;
import com.metasequoia.services.audio.IAudioClient;
import com.metasequoia.services.audio.IAudioService;


public class AudioManager {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "FMAudioManager";
    public static final int MSG_VOLUME_CHANGE = 1;

    private static AudioManager mInstance;
    //服务stub
    private static IAudioService mService;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private final Looper mMainLooper;
    /** @see AudioManagerThread */
    private AudioManagerThread mAudioSystemThread;
    /** @see AudioHandler */
    private AudioHandler mAudioHandler;
    private AudioVolumeListener mAudioVolumeListener;

    private AudioClient mClient;
    AudioManager(Looper looper) {
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
        createAudioSystemThread();
    }

    /**
     * Retrieve the global AudioManager instance, creating it if it
     * doesn't already exist.
     */
    public static AudioManager getInstance() {
        synchronized (AudioManager.class) {
            if (mInstance == null) {
                mInstance = new AudioManager(Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    /**
     * Private optimization: retrieve the global AudioManager instance,
     * if it exists.
     */
    public static AudioManager peekInstance() {
        return mInstance;
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private  IAudioService getServiceLocked() {
        if (mService == null) {
            tryConnectToServiceLocked();
        }
        return mService;
    }

    /**
     * 尝试连接到服务端，并把代理设置到服务端
     */
    private void tryConnectToServiceLocked() {
        IBinder iBinder = ServiceManager.getService(ProductContext.SERVICE_NAME_AUDIO);
        if (iBinder == null) {
            return;
        }

        IAudioService service = IAudioService.Stub.asInterface(iBinder);
        try {
            mService = service;
            if(mAudioVolumeListener != null){
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
            Log.e(LOG_TAG, "AudioService is dead", re);
            mService = null;
            mClient = null;
        }
        if(DEBUG) Log.d(LOG_TAG,"tryConnectToServiceLocked:"+mService.toString());
    }

    private void addClient2Remote() throws RemoteException{
        if(mClient == null && mService != null){
            mClient = new AudioClient();
            mService.addClient(mClient);
        }
    }

    /**
     * 处理消息
     * @param what 消息what
     * @param arg 消息obj
     */
    private void dispatchMsg(int what, int arg, Object obj){
        switch (what) {
            case MSG_VOLUME_CHANGE:
                onRecVolumeChange(arg);
                break;
        }
    }

    private void onRecVolumeChange(int volume) {
        if(DEBUG) Log.d(LOG_TAG,"onRecVolumeChange="+volume);
        if(mAudioVolumeListener != null){
            mAudioVolumeListener.onAudioVolumeChange(volume);
        }
    }
    /**
     * 设置音量值，按照[0,100]设置
     * @param volume 音量值
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setVolume(int volume, int flag){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG,"setVolume="+volume);
                return getServiceLocked().setVolume(volume, flag);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 设置静音
     * @param  isMute ture静音 false关闭静音
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setMute(boolean isMute){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG,"setMute="+isMute);
               return getServiceLocked().setMute(isMute);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 设置音效，低音 中音  高音
     * @param low 低音
     * @param mid 中音
     * @param high 高音
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setEffect(int low, int mid, int high){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG,"setEffect:low="+low+"; mid="+mid+"; high="+ high);
                return getServiceLocked().setEffect(low, mid,high);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 设置增益，前左，前右，后左，后右
     * @param fl 前左
     * @param fr 前右
     * @param bl 后左
     * @param br 后右
     * @return RTN_SUCCESS 成功 正常
     *         RTN_FAIL 失败 异常
     */
    public int setGain(int fl, int fr, int bl, int br){

        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG, "setGain:fl="+fl+"; fr="+fr+"; bl="+ bl+"; br"+br);
                return getServiceLocked().setGain(fl,fr, bl,br );
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }
    /**
     * 设置音源
     * @param channel AUDIO_CHANNEL_MEDIA，AUDIO_CHANNEL_FM
     * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
     */
    public int setChannel(int channel){
        try {
            if(getServiceLocked() != null){
                if(DEBUG) Log.d(LOG_TAG, "setChannel:"+channel);
                return getServiceLocked().setChannel(channel);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return Common.RTN_FAIL;
    }

    public void setAudioVolumeListener(AudioVolumeListener listener){
        mAudioVolumeListener = listener;
        try {
            addClient2Remote();
        }catch (RemoteException e){
            e.printStackTrace();
            mService = null;
            mClient = null;
        }
    }
    public interface AudioVolumeListener{
        void onAudioVolumeChange(int volume);
    }
    /**
     * 服务端回调消息代理对象
     */
    private class AudioClient extends IAudioClient.Stub{
        public void onVolumeChange(int value) {
            // We do not want to change this immediately as the applicatoin may
            // have already checked that accessibility is on and fired an event,
            // that is now propagating up the view tree, Hence, if accessibility
            // is now off an exception will be thrown. We want to have the exception
            // enforcement to guard against apps that fire unnecessary accessibility
            // events when accessibility is off.
            mAudioHandler.obtainMessage(MSG_VOLUME_CHANGE, value, 0).sendToTarget();
        }

    }

    //----------------------------------------------------------------------------------------------
    //audio handler
    private void createAudioSystemThread() {
        mAudioSystemThread = new AudioManagerThread();
        mAudioSystemThread.start();
        //waitForAudioHandlerCreation();
    }

    /** Waits for the volume handler to be created by the other thread. */
   /* private void waitForAudioHandlerCreation() {
        synchronized(this) {
            while (mAudioHandler == null) {
                try {
                    // Wait for mAudioHandler to be set by the other thread
                    wait();
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "Interrupted while waiting on volume handler.");
                }
            }
        }
    }*/

    /** Thread that handles native AudioSystem control. */
    private class AudioManagerThread extends Thread {
        AudioManagerThread() {
            super("AudioService");
        }

        @Override
        public void run() {
            // Set this thread up so the handler will work on it
            Looper.prepare();

            synchronized(AudioManager.this) {
                mAudioHandler = new AudioHandler();
            }

            // Listen for volume change requests that are set by VolumePanel
            Looper.loop();
        }
    }

    /** Handles internal volume messages in separate volume thread. */
    private class AudioHandler extends Handler {
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

