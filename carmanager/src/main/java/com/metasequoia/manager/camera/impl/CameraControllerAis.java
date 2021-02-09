package com.metasequoia.manager.camera.impl;

import android.content.ComponentName;
import android.os.IBinder;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import com.metasequoia.manager.camera.CameraObservableManager;
import com.metasequoia.manager.camera.CameraParams;
import com.metasequoia.manager.camera.FrameBufferCallBack;
import com.metasequoia.manager.camera.config.Constants;
import com.metasequoia.manager.camera.Controller;
import com.metasequoia.manager.camera.MemoryFile;
import com.metasequoia.manager.camera.base.BaseCameraControl;
import com.metasequoia.services.camera.ICameraClient;
import com.metasequoia.services.camera.ICameraService;
import com.metasequoia.services.camera.FrameDataCallBack;
import com.metasequoia.manager.constent.ProductContext;

import static com.metasequoia.manager.camera.config.Constants.CHIP_CHANNEL_NUM;
import static com.metasequoia.manager.camera.config.Constants.MAX_CHANNEL_NUM;


public class CameraControllerAis extends BaseCameraControl {
    private static final boolean DEBUG = true;
    private static final String TAG = "CameraControllerAis";
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;

    //服务stub
    private static ICameraService mService;
    private CameraParams mCameraParams;
    private CameraClient mClient;
    private volatile static Controller mInstance;

    private FrameBufferCallBack mCallBack;
    private Constants.DevicesID mDevicesID;
    private int mResolution = -1;
    private IBinder mBinder;
    private IBinder.DeathRecipient mBinderDeathRecip;
    private boolean mConnected = false;
    /**
     * 互斥锁
     */
    private final Object mLock = new Object();

    private final Looper mMainLooper;
//    private Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            try {
//                dispatchMsg(msg.what,msg.arg1, msg.obj);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return false;
//        }
//    });

    private FrameDataCallBack mFrameDataCallBack = new FrameDataCallBack.Stub() {
        @Override
        public void canReadFrameData() throws RemoteException {
            if (mFrontMemoryFile != null) {
                mFrontMemoryFile.readShareBuffer();
            }
        }
    };

    /**
     * Retrieve the global CameraManager instance, creating it if it
     * doesn't already exist.
     */
    public static Controller getInstance() {
        synchronized (CameraControllerAis.class) {
            if (mInstance == null) {
                Log.d(TAG,"getInstance: SERVICE_NAME_CAMERA");
                IBinder b = ServiceManager.getService(ProductContext.SERVICE_NAME_CAMERA);
                ICameraService service = ICameraService.Stub.asInterface(b);
                mInstance = new CameraControllerAis(service, Looper.getMainLooper());
            }
        }
        return mInstance;
    }

    @Override
    protected void setRemoteCtrl(IBinder iBinder) {
        if (iBinder == null) {
            Log.d(TAG,"iBinder is null");
            if (mService != null) {
                try {
                    mService.unregisterFrameByteCallBack(mFrameDataCallBack);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            setFrontBufferCallBack(null);
            if(mBinder != null) {
                boolean result = mBinder.unlinkToDeath(mBinderDeathRecip, 0);
                Log.d(TAG, "mBinder.unlinkToDeath result: " + result);
                try {
                    removeClient4Remote();
                } catch (RemoteException re) {
                    Log.e(TAG, "CameraService is dead", re);
                }
            }
            mResolution = -1;
            mDevicesID = null;
            mService = null;
            mClient = null;
            mBinder = null;
            mBinderDeathRecip = null;
            mConnected = false;
        } else {
            Log.d(TAG,"setRemoteCtrl iBinder = " + iBinder + "mCallBack: " + mCallBack);
            //mService = ICameraService.Stub.asInterface(iBinder);
            if (mService != null) {
                if (mCallBack != null && mFrontMemoryFile != null) {
                    try {
                        mService.setParcelFileDescriptor(mFrontMemoryFile.getParcelFileDescriptor());
                        mService.registerFrameByteCallBack(mFrameDataCallBack);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            //setFrontBufferCallBack(mCallBack);
        }
    }

    @Override
    protected MemoryFile getFrontMemoryFile(FrameBufferCallBack callBack) {
        mCallBack = callBack;
        if (mService != null) {
            try {
                if (callBack != null) {
                    if (getADASResolution() == Constants.Resolution720P) {
                        mService.setParcelFileDescriptor(MemoryFileHuiying.getInstance().getParcelFileDescriptor());
                    } else if (getADASResolution() == Constants.Resolution848_480) {
                        mService.setParcelFileDescriptor(MemoryFileXbw.getInstance().getParcelFileDescriptor());
                    }
                    mService.registerFrameByteCallBack(mFrameDataCallBack);
                } else {
                    mService.unregisterFrameByteCallBack(mFrameDataCallBack);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (getADASResolution() == Constants.Resolution720P) {
            return MemoryFileHuiying.getInstance();
        } else if (getADASResolution() == Constants.Resolution848_480) {
            return MemoryFileXbw.getInstance();
        }
        return null;
    }

    @Override
    protected MemoryFile getBackMemoryFile(FrameBufferCallBack callBack) {
        mCallBack = callBack;
        if (mService != null) {
            try {
                if (callBack != null) {
                    if (getADASResolution() == Constants.Resolution720P) {
                        mService.setParcelFileDescriptor(MemoryFileHuiying.getInstance().getParcelFileDescriptor());
                    } else if (getADASResolution() == Constants.Resolution848_480) {
                        mService.setParcelFileDescriptor(MemoryFileXbw.getInstance().getParcelFileDescriptor());
                    }
                    mService.registerFrameByteCallBack(mFrameDataCallBack);
                } else {
                    mService.unregisterFrameByteCallBack(mFrameDataCallBack);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (getADASResolution() == Constants.Resolution720P) {
            return MemoryFileHuiying.getInstance();
        } else if (getADASResolution() == Constants.Resolution848_480) {
            return MemoryFileXbw.getInstance();
        }
        return null;
    }

    @Override
    protected void updateSurface(Surface surface, int width, int height) {
        if(surface != null) {
            startPreview(surface, 0, width, height);
        } else {
            stopPreview(0);
        }
    }

    @Override
    protected int initService() {
        return initCamera();
    }

    @Override
    public void release() {
        //super.release();
        Log.d(TAG, "on release");
        clearSurface();
        setRemoteCtrl(null);
        mContext = null;
        if (mBackMemoryFile != null) {
            mBackMemoryFile.release();
            mBackMemoryFile = null;
        }
        if (mFrontMemoryFile != null) {
            mFrontMemoryFile.release();
            mFrontMemoryFile = null;
        }
        mInstance = null;
    }

    @Override
    public Constants.DevicesID getDevicesID() {
        if (mService != null) {
            try {
            switch (mService.getDeviceID()) {
                case 1:
                    mDevicesID = Constants.DevicesID.DEVICES_ID_PVET;
                    break;
                case 2:
                    mDevicesID = Constants.DevicesID.DEVICES_ID_XBW;
                    break;
                case 3:
                    mDevicesID = Constants.DevicesID.DEVICES_ID_HY;
                    break;
                case 4:
                    mDevicesID = Constants.DevicesID.DEVICES_ID_AIS;
                    break;
                default:
                    break;
            }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return mDevicesID;
    }

    @Override
    public int getADASResolution() {
        Log.d(this.getClass().getSimpleName(), "getADASResolution");
        if (mService != null) {
            try {
            int value = mService.getADASResolution();
            switch (value) {
                case 0:
                    mResolution = Constants.Resolution1080P;
                    break;
                case 1:
                    mResolution = Constants.Resolution720P;
                    break;
                case 2:
                    mResolution = Constants.Resolution848_480;
                    break;
                default:
                    mResolution = Constants.Resolution720P;
                    break;
            }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return mResolution;
    }

    CameraControllerAis(ICameraService service, Looper looper) {
        mMainLooper = looper;
        synchronized (mLock) {
            tryConnectToServiceLocked();
        }
    }

    /**
     * 获取服务端代理对象，如果为空，则尝试重新连接
     * @return
     */
    private  ICameraService getServiceLocked() {
        if (mService == null) {
            tryConnectToServiceLocked();
        }
        return mService;
    }

    /**
     * 尝试连接到服务端，并把代理设置到服务端
     */
    private void tryConnectToServiceLocked() {
        IBinder iBinder = ServiceManager.getService(ProductContext.SERVICE_NAME_CAMERA);
        if (iBinder == null) {
            return;
        }
        ICameraService service = ICameraService.Stub.asInterface(iBinder);
        try {
            mService = service;
            mBinder = iBinder;
            mConnected = true;
            addClient2Remote();
            iBinder.linkToDeath(mBinderDeathRecip = new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    if(mService != null) {
                        mService = null;
                        mClient = null;
                        mBinder = null;
                        mBinderDeathRecip = null;
                        mConnected = false;
                        CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_SREVICE_DIED);
                        Log.e(TAG, "CameraService is dead");
                    }
                }
            }, 0);
        } catch (RemoteException re) {
            mService = null;
            mClient = null;
            mBinder = null;
            mBinderDeathRecip = null;
            mConnected = false;
            CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_SREVICE_DIED);
            Log.e(TAG, "CameraService is dead", re);
        }
        if(DEBUG) Log.d(TAG,"tryConnectToServiceLocked:" + mService.toString());
    }

    private void addClient2Remote() throws RemoteException{
        if(mClient == null && mService != null){
            mClient = new CameraClient();
            mService.addClient(mClient);
        }
    }

    private void removeClient4Remote() throws RemoteException {
        if(mClient != null && mService != null){
            if(DEBUG) Log.d(TAG,"removeClient:" + mClient.toString());
            mService.removeClient(mClient);
        }
    }

    /**
     * init camera module, client connect to service
     */
    public int initCamera() {
        ICameraService service = getServiceLocked();
        if(service == null) {
            Log.i(TAG, "reconnecting.....");
            return FAILED;
        }
        try {
            int result = service.initCamera(21);
            if(DEBUG) Log.d(TAG,"result="+result);
            return SUCCESS;
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call initCamera() on CameraService:", e);
            return FAILED;
        }
    }

    /**
     * Get SDK Version
     * result String: sdk version
     */
    public String getSdkVersion() {
        // query the sdk version number
        String sdkVersion = null;
        try {
            if (mConnected) {
                sdkVersion = mService.getSdkVersion();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call getSdkVersion() on CameraService:", e);
        }
        return sdkVersion;
    }

    /**
     * Open Camera
     * result 0:success -1:failed 1:opening
     */
    @Override
    public int openCamera(int csiphy, int count) {
        //Todo if(cameraId >= getNumberOfCameras()){
        //         throw new RuntimeException("Unknown camera ID");
        //     }
        int result = -1;
        if (mConnected) {
            try {
                result = mService.openCamera(csiphy, count);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if(DEBUG) Log.d(TAG,"openCamera=" + result);
        }
        return result;
    }

    /**
     * Close Camera
     * result 0:success -1:failed
     */
    @Override
    public int closeCamera(int csiphy) {
        //mService.setPreviewCallback(null);
        int result = -1;
        if (mConnected) {
            try {
                result = mService.closeCamera(csiphy);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if(DEBUG) Log.d(TAG,"closeCamera=" + result);
        }
        return result;
    }

    /**
     * startPreview
     * result
     */
    @Override
    public void startPreview(Surface surface, int id, int width, int height) {
        if(mConnected) {
            try {
                mService.startPreview(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM, surface, width, height, 0);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call startPreview() on CameraService:", e);
            }
        }
    }

    /**
     * stopPreview
     * result
     */
    @Override
    public void stopPreview(int id) {
        if (mConnected) {
            try {
                mService.stopPreview(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call stopPreview() on CameraService:", e);
            }
        }
    }

    /**
     * Set Fps, both Preview and Video
     *
     * @param id
     * @param fps
     * @return null
     */
    public void setFps(int id, int fps) {
        if (mConnected) {
            try {
                mService.setFps(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM, fps);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setPreviewVideoFps() on CameraService:", e);
            }
        }
    }

    public void setPreviewMirror(int id, boolean isMirror) {
        if (mConnected) {
            try {
                mService.setPreviewMirror(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM, isMirror);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setPreviewVideoFps() on CameraService:", e);
            }
        }
    }

    public void setPreviewStreamMirror(int id, boolean isMirror) {
        if (mConnected) {
            try {
                mService.setPreviewStreamMirror(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM, isMirror);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setPreviewVideoFps() on CameraService:", e);
            }
        }
    }

    public void setVideoStreamMirror(int id, boolean isMirror) {
        if (mConnected) {
            try {
                mService.setVideoStreamMirror(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM, isMirror);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setPreviewVideoFps() on CameraService:", e);
            }
        }
    }

    //Video Record.
    /**
     * startVideoRecord
     *
     * @param id
     */
    public void startVideoRecord(int id) {
        if(DEBUG) Log.d(TAG, "startVideoRecord id: " + id);
        if (mConnected) {
            try {
                mService.startVideoRecord(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call startVideoRecord() on CameraService:", e);
            }
        }
    }

    /**
     * stopVideoRecord
     *
     * @param id
     */
    public void stopVideoRecord(int id) {
        if(DEBUG) Log.d(TAG, "stopVideoRecord id:" + id);
        if (mConnected) {
            try {
                mService.stopVideoRecord(id<CHIP_CHANNEL_NUM?0:2, id%CHIP_CHANNEL_NUM);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call stopVideoRecord() on CameraService:", e);
            }
        }
    }

    public void setLockVideo(boolean isLock, int lockSegmentType, int lockSegmentThreshold) {
        if(DEBUG) Log.d(TAG, "setLockVideo isLock: "+isLock+" lockSegmentThreshold: "+lockSegmentThreshold);
        if (mConnected) {
            try {
                mService.setLockVideo(isLock, lockSegmentType, lockSegmentThreshold);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setLockVideo() on CameraService:", e);
            }
        }
    }
    /**
     * getCameraParam
     * @return
     */
    public CameraParams getCameraParam() {
        mCameraParams = null;
        if (mConnected) {
            try {
                mCameraParams = mService.getCameraParams();
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call getCameraParam() on CameraService:", e);
            }
        }
        return mCameraParams;
    }

    /**
     * setCameraParam
     *
     * @param cameraParam
     */
    public void setCameraParam(CameraParams cameraParam){
        //Check available param
        if (mConnected) {
            mCameraParams = cameraParam;
            try {
                mService.setCameraParams(mCameraParams);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setCameraParam() on CameraService:", e);
            }
        }
    }

    /**
     * handleCollision, this function is worked after video record is on,
     * it will take 10s' video before collision and 10s' video after it.
     */
    public void handleCollision() {
        if (mConnected) {
            try {
                mService.handleCollision();
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call handleCollision() on CameraService:", e);
            }
        }
    }

    //Picture and water mark API

    /**
     * getTakePictureFlag, incase take picture is going
     * @return
     */
    @Override
    public boolean getTakePictureFlag() {
        boolean result = false;
        if (mConnected) {
            try {
                result = mService.getTakePicStartFlag();
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call getTakePictureFlag() on CameraService:", e);
            }
        }
        return result;
    }

    /**
     * start take picture
     *
     * @return void
     */
    @Override
    public String takePicture(int id, int width, int height) {
        String picPath = null;
        if (mConnected) {
            try {
                picPath = mService.startTakePic(id<CHIP_CHANNEL_NUM?0:2,id%CHIP_CHANNEL_NUM, width, height);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call takePicture() on CameraService:", e);
            }
        }
        return picPath;
    }

    /**
     * initWaterMark
     *
     * @param type
     * @param fontPath
     * @param fontSize
     * @return result
     */
    public long initWaterMark(int type, byte[] fontPath, int fontSize) {
        long result = 0;
        if (mConnected) {
            try {
                result = mService.initWaterMark(type, fontPath, fontSize);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setWaterMark() on CameraService:", e);
            }
        }
        return result;
    }

    /**
     * Deinit water mark
     *
     * @param handle handler
     * @return result
     */
    public int deinitWaterMark(long handle) {
        int result = 0;
        if (mConnected) {
            try {
                result = mService.deinitWaterMark(handle);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setWaterMark() on CameraService:", e);
            }
        }
        return result;
    }

    /**
     * set water mark
     *
     * @return result
     */
    public int setWaterMark(long handle, byte[] data, int index, int posX, int posY) {
        int result = 0;
        if (mConnected) {
            try {
                result = mService.setWaterMark(handle, data, index, posX, posY);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setWaterMark() on CameraService:", e);
            }
        }
        return result;
    }

    /**
     * set water mark with chinese
     *
     * @return result
     */
    public int setWaterMarkWithChinese(long handle, byte[] data, int index, int posX, int posY) {
        int result = 0;
        if (mConnected) {
            try {
                result = mService.setWaterMarkWithChinese(handle, data, index, posX, posY);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call setWaterMarkWithChinese() on CameraService:", e);
            }
        }
        return result;
    }

    /**
     * clear water mark
     *
     * @return result
     */
    public int clearWaterMark(long handle, int index) {
        int result = 0;
        if (mConnected) {
            try {
                result = mService.clearWaterMark(handle, index);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call clearWaterMark() on CameraService:", e);
            }
        }
        return result;
    }

    /**
     * enable water mark
     *
     * @return result
     */
    public int enableWaterMark(long handle) {
        int result = 0;
        if (mConnected) {
            try {
                result = mService.enableWaterMark(handle);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call enableWaterMark() on CameraService:", e);
            }
        }
        return result;
    }

    /**
     * disable water mark
     *
     * @return result
     */
    public int disableWaterMark(long handle) {
        int result = 0;
        if (mConnected) {
            try {
                result = mService.disableWaterMark(handle);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call disableWaterMark() on CameraService:", e);
            }
        }
        return result;
    }

//    /**
//     * 处理消息
//     * @param what 消息what
//     * @param arg 消息obj
//     */
//    private void dispatchMsg(int what, int arg, Object obj) {
//        switch (what) {
//            case MSG_SET_STATE:
//                setStateLocked(arg);
//                break;
//        }
//    }
//
//    /**
//     * setStateLocked
//     * @param stateFlags
//     */
//    private void setStateLocked(int stateFlags) {
//        if(DEBUG) Log.d(TAG,"setStateLocked="+stateFlags);
//    }

    /**
     * 服务端回调消息代理对象
     */
    private class CameraClient extends ICameraClient.Stub {
        public void setState(int state) {
            // We do not want to change this immediately as the applicatoin may
            // have already checked that accessibility is on and fired an event,
            // that is now propagating up the view tree, Hence, if accessibility
            // is now off an exception will be thrown. We want to have the exception
            // enforcement to guard against apps that fire unnecessary accessibility
            // events when accessibility is off.
//            mHandler.obtainMessage(MSG_SET_STATE, state, 0).sendToTarget();
            if(DEBUG) Log.d(TAG,"setState=" + state);
            ComponentName componentName = new ComponentName("com.metasequoia.services", "com.metasequoia.services.camera.CameraService");
            onServiceConnected(componentName, mBinder);
        }
        public void onCameraStatus(int csiphy, int channel, boolean isInsert) {
            if(DEBUG) Log.d(TAG,"isInsert=" + isInsert);
            if (isInsert) {
                CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_STATUS_INSERT);
            } else {
                CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_STATUS_UNPLUG);
            }
        }
    }
}