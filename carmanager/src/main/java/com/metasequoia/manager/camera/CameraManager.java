package com.metasequoia.manager.camera;

import android.content.Context;
import android.view.Surface;

import com.metasequoia.manager.camera.impl.CameraControllerAis;
import com.metasequoia.manager.camera.impl.CameraControllerNwd;
import com.metasequoia.manager.camera.impl.CameraControllerPvet;
import com.metasequoia.manager.camera.utils.Utils;
import com.metasequoia.manager.camera.config.Config;
import com.metasequoia.manager.camera.config.Constants;

import static com.metasequoia.manager.camera.config.Config.sIsNwd;
import static com.metasequoia.manager.camera.config.Config.sIsAis;

public class CameraManager {
    private volatile static CameraManager mInstance;
    private Controller mController;

    private CameraManager() {
    }

    public static CameraManager getInstance() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null) {
                    mInstance = new CameraManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * init sdk
     *
     * @param context context
     * @return result
     */
    public int init(Context context) {
        sIsAis = Utils.checkAppInstalled(context, Config.sAisPackageName);
        sIsNwd = Utils.checkAppInstalled(context, Config.sNewPackageName);
        if(sIsAis) {
            mController = CameraControllerAis.getInstance();
        } else if (sIsNwd) {
            mController = CameraControllerNwd.getInstance();
        } else {
            mController = CameraControllerPvet.getInstance();
        }
        return mController.init(context);
    }

    /***
     * release sdk
     */
    public void release() {
        if (mController != null) {
            mController.release();
        }
        mController = null;
        mInstance = null;
    }

    /**
     * getSdkVersion
     *
     * @return version
     */
    public String getSdkVersion() {
        String result = null;
        if (mController != null) {
            result = mController.getSdkVersion();
        }
        return result;
    }

    /**
     * open camera
     *
     * @param csiphy id
     * @param count num
     * @return result
     */
    public int openCamera(int csiphy, int count) {
        int result = 0;
        if (mController != null) {
            result = mController.openCamera(csiphy, count);
        }
        return result;
    }

    /**
     * close camera
     *
     * @param csiphy id
     * @return result
     */
    public int closeCamera(int csiphy) {
        int result = 0;
        if (mController != null) {
            result = mController.closeCamera(csiphy);
        }
        return result;
    }

    /**
     * startPreview
     *
     * @param surface the surface
     * @param id      the id num
     * @param width   the width
     * @param height  the height
     */
    public void startPreview(Surface surface, int id, int width, int height) {
        if (mController != null) {
            mController.startPreview(surface, id, width, height);
        }
    }

    /***
     * stopPreview
     *
     * @param id id
     */
    public void stopPreview(int id) {
        if (mController != null) {
            mController.stopPreview(id);
        }
    }

    /**
     * startVideoRecord
     *
     * @param id id
     */
    public void startVideoRecord(int id) {
        if (mController != null) {
            mController.startVideoRecord(id);
        }
    }

    /***
     * stopVideoRecord
     *
     * @param id id
     */
    public void stopVideoRecord(int id) {
        if (mController != null) {
            mController.stopVideoRecord(id);
        }
    }

    /**
     * setFrontBufferCallBack
     *
     * @param callBack callback
     */
    public void setFrontBufferCallBack(FrameBufferCallBack callBack) {
        if (mController != null) {
            mController.setFrontBufferCallBack(callBack);
        }
    }

    /**
     * setBackBufferCallBack
     *
     * @param callBack callback
     */
    public void setBackBufferCallBack(FrameBufferCallBack callBack) {
        if (mController != null) {
            mController.setBackBufferCallBack(callBack);
        }
    }

    /**
     * check if it is taking picture
     *
     * @return result
     */
    public boolean getTakePictureFlag() {
        boolean result = false;
        if (mController != null) {
           result = mController.getTakePictureFlag();
        }
        return result;
    }

    /**
     * takePicture
     *
     * @param id id
     * @param width width
     * @param height height
     * @return patch
     */
    public String takePicture(int id, int width, int height) {
        String result = null;
        if (mController != null) {
            result = mController.takePicture(id, width, height);
        }
        return result;
    }

    /**
     * setLockVideo
     *
     * @param isLock
     * @param lockSegmentType
     * @param lockSegmentThreshold
     */
    public void setLockVideo(boolean isLock, int lockSegmentType, int lockSegmentThreshold) {
        if (mController != null) {
            mController.setLockVideo(isLock, lockSegmentType, lockSegmentThreshold);
        }
    }

    /**
     * initWaterMark
     *
     * @param type
     * @param fontPath
     * @param fontSize
     * @return handle
     */
    public long initWaterMark(int type, byte[] fontPath, int fontSize) {
        long handle = 0;
        if (mController != null) {
            handle = mController.initWaterMark(type, fontPath, fontSize);
        }
        return handle;
    }

    /**
     * deinitWaterMark
     *
     * @param handle
     * @return result
     */
    public int deinitWaterMark(long handle) {
        int result = 0;
        if (mController != null) {
            result = mController.deinitWaterMark(handle);
        }
        return result;
    }

    /**
     * setWaterMark
     *
     * @param handle
     * @param data
     * @param index
     * @param posX
     * @param posY
     * @return result
     */
    public int setWaterMark(long handle, byte[] data, int index, int posX, int posY) {
        int result = 0;
        if (mController != null) {
            result = mController.setWaterMark(handle, data, index, posX, posY);
        }
        return result;
    }

    /**
     * setWaterMarkWithChinese
     *
     * @param handle
     * @param data
     * @param index
     * @param posX
     * @param posY
     * @return
     */
    public int setWaterMarkWithChinese(long handle, byte[] data, int index, int posX, int posY) {
        int result = 0;
        if (mController != null) {
            result = mController.setWaterMarkWithChinese(handle, data, index, posX, posY);
        }
        return result;
    }

    /**
     * setPreviewMirror
     *
     * @param id
     * @param isMirror
     */
    public void setPreviewMirror(int id, boolean isMirror) {
        if (mController != null) {
            mController.setPreviewMirror(id, isMirror);
        }
    }

    /**
     * setPreviewStreamMirror
     *
     * @param id
     * @param isMirror
     */
    public void setPreviewStreamMirror(int id, boolean isMirror) {
        if (mController != null) {
            mController.setPreviewStreamMirror(id, isMirror);
        }
    }

    /**
     * setVideoStreamMirror
     *
     * @param id
     * @param isMirror
     */
    public void setVideoStreamMirror(int id, boolean isMirror) {
        if (mController != null) {
            mController.setVideoStreamMirror(id, isMirror);
        }
    }

    /**
     * getCameraParam
     *
     * @return parameter
     */
    public CameraParams getCameraParam() {
        if (mController != null) {
            return mController.getCameraParam();
        }
        return null;
    }

    /**
     * setCameraParam
     *
     * @param cameraParam parameter
     */
    public void setCameraParam(CameraParams cameraParam){
        if (mController != null) {
            mController.setCameraParam(cameraParam);
        }
    }

    public boolean isBindFinish() {
        return mController == null ? false : mController.isBind();
    }

    public void clearSurface() {
        if (mController != null) {
            mController.clearSurface();
        }
    }

    public void setSurface(Surface surface, int width, int height) {
        if (mController != null) {
            mController.setSurface(surface, width, height);
        }
    }

    public Constants.DevicesID getDevicesID() {
        if (mController != null) {
            return mController.getDevicesID();
        }
        return Constants.DevicesID.DEVICES_ID_UNKNOWN;
    }

    public int getADASResolution() {
        if (mController != null) {
            return mController.getADASResolution();
        }
        return -1;
    }

    public int getHeigth() {
        if (getADASResolution() == Constants.Resolution848_480) {
            return 480;
        }
        if (getADASResolution() == Constants.Resolution720P) {
            return 720;
        }
        return -1;
    }

    public int getWidth() {
        if (getADASResolution() == Constants.Resolution848_480) {
            return 848;
        }
        if (getADASResolution() == Constants.Resolution720P) {
            return 1280;
        }
        return -1;
    }
}
