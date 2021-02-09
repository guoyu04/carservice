package com.metasequoia.manager.camera.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.Surface;

import com.metasequoia.manager.camera.CameraObservableManager;
import com.metasequoia.manager.camera.FrameBufferCallBack;
import com.metasequoia.manager.camera.Controller;
import com.metasequoia.manager.camera.MemoryFile;
import com.metasequoia.manager.camera.CameraParams;

public abstract class BaseCameraControl implements Controller, ServiceConnection {
    protected boolean isBind;
    protected Surface mSurface;
    protected int mWidth, mHeight;
    protected Context mContext;
    protected MemoryFile mFrontMemoryFile;
    protected MemoryFile mBackMemoryFile;

    @Override
    public void setSurface(Surface surface, int width, int height) {
        if (isBind) {
            updateSurface(surface, width, height);
        }
        mSurface = surface;
        mWidth = width;
        mHeight = height;
    }

    protected abstract void updateSurface(Surface surface, int width, int height);

    @Override
    public void clearSurface() {
        setSurface(null, 0, 0);
    }

    @Override
    public int init(Context context) {
        if (isBind) return -1;
        mContext = context;
        return initService();
    }

    protected abstract int initService();

    @Override
    public void release() {
        clearSurface();
        setRemoteCtrl(null);
        mContext.unbindService(this);
        mContext = null;
        if (mBackMemoryFile != null) {
            mBackMemoryFile.release();
            mBackMemoryFile = null;
        }
        if (mFrontMemoryFile != null) {
            mFrontMemoryFile.release();
            mFrontMemoryFile = null;
        }

    }

    @Override
    public boolean isBind() {
        return isBind;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        isBind = true;
        setRemoteCtrl(service);
        if (mSurface != null && mSurface.isValid()) {
            setSurface(mSurface, mWidth, mHeight);
        }
        CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_SREVICE_CONNECT);
    }

    protected abstract void setRemoteCtrl(IBinder service);

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBind = false;
        setRemoteCtrl(null);
        CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_SREVICE_DIED);
    }

    @Override
    public void setFrontBufferCallBack(FrameBufferCallBack callBack) {
        mFrontMemoryFile = getFrontMemoryFile(callBack);
        if (mFrontMemoryFile != null) {
            mFrontMemoryFile.setBufferYv12CallBack(callBack);
        }
    }

    protected abstract MemoryFile getFrontMemoryFile(FrameBufferCallBack callBack);

    @Override
    public void setBackBufferCallBack(FrameBufferCallBack callBack) {
        mBackMemoryFile = getBackMemoryFile(callBack);
        if (mBackMemoryFile != null) {
            mBackMemoryFile.setBufferYv12CallBack(callBack);
        }
    }

    protected abstract MemoryFile getBackMemoryFile(FrameBufferCallBack callBack);

    @Override
    public String getSdkVersion() {
        return null;
    }

    @Override
    public int openCamera(int csiphy, int count) {
        return 0;
    }

    @Override
    public int closeCamera(int csiphy) {
        return 0;
    }

    @Override
    public void startPreview(Surface surface, int id, int width, int height) {
    }

    @Override
    public void stopPreview(int id) {
    }

    @Override
    public void startVideoRecord(int id) {
    }

    @Override
    public void stopVideoRecord(int id) {
    }

    @Override
    public void setLockVideo(boolean isLock, int segType, int segThreshold) {
    }

    @Override
    public boolean getTakePictureFlag() {
        return false;
    }

    @Override
    public String takePicture(int id, int width, int height) {
        return null;
    }

    @Override
    public long initWaterMark(int type, byte[] fontPath, int fontSize) {
        return 0;
    }

    @Override
    public int deinitWaterMark(long handle) {
        return 0;
    }

    @Override
    public int setWaterMark(long handle, byte[] data, int index, int posX, int posY) {
        return 0;
    }

    @Override
    public int setWaterMarkWithChinese(long handle, byte[] data, int index, int posX, int posY) {
        return 0;
    }

    @Override
    public void setPreviewMirror(int id, boolean isMirror) {
    }

    @Override
    public void setPreviewStreamMirror(int id, boolean isMirror) {
    }

    @Override
    public void setVideoStreamMirror(int id, boolean isMirror) {
    }

    @Override
    public CameraParams getCameraParam() {
        return null;
    }

    @Override
    public void setCameraParam(CameraParams cameraParam) {}
}
