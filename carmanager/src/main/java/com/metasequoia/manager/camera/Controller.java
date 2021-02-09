package com.metasequoia.manager.camera;

import android.content.Context;
import android.content.ServiceConnection;
import android.view.Surface;
import com.metasequoia.manager.camera.config.Constants;

/**
 * The interface Controller.
 */
public interface Controller extends ServiceConnection {

    /**
     * getSdkVersion
     *
     * @return version
     */
    String getSdkVersion();

    /**
     * init sdk
     *
     * @param context context
     * @return result
     */
    int init(Context context);

    /***
     * release sdk
     */
    void release();

    /**
     * open camera
     *
     * @param csiphy id
     * @param count num
     * @return result
     */
    int openCamera(int csiphy, int count);

    /**
     * close camera
     *
     * @param csiphy id
     * @return result
     */
    int closeCamera(int csiphy);

    /**
     * startPreview
     *
     * @param surface the surface
     * @param id      the id num
     * @param width   the width
     * @param height  the height
     */
    void startPreview(Surface surface, int id, int width, int height);

    /***
     * stopPreview
     *
     * @param id id
     */
    void stopPreview(int id);

    /**
     * startVideoRecord
     *
     * @param id id
     */
    void startVideoRecord(int id);

    /***
     * stopVideoRecord
     *
     * @param id id
     */
    void stopVideoRecord(int id);

    /**
     * setFrontBufferCallBack
     *
     * @param callBack callback
     */
    void setFrontBufferCallBack(FrameBufferCallBack callBack);

    /**
     * setBackBufferCallBack
     *
     * @param callBack callback
     */
    void setBackBufferCallBack(FrameBufferCallBack callBack);

    /**
     * check if it is taking picture
     *
     * @return result
     */
    boolean getTakePictureFlag();

    /**
     * takePicture
     *
     * @param id id
     * @param width width
     * @param height height
     * @return patch
     */
    String takePicture(int id, int width, int height);

    /**
     * setLockVideo
     *
     * @param isLock
     * @param lockSegmentType
     * @param lockSegmentThreshold
     */
    void setLockVideo(boolean isLock, int lockSegmentType, int lockSegmentThreshold);

    /**
     * initWaterMark
     *
     * @param type
     * @param fontPath
     * @param fontSize
     * @return
     */
    long initWaterMark(int type, byte[] fontPath, int fontSize);

    /**
     * deinitWaterMark
     *
     * @param handle
     * @return
     */
    int deinitWaterMark(long handle);

    /**
     * setWaterMark
     *
     * @param handle
     * @param data
     * @param index
     * @param posX
     * @param posY
     * @return
     */
    int setWaterMark(long handle, byte[] data, int index, int posX, int posY);

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
    int setWaterMarkWithChinese(long handle, byte[] data, int index, int posX, int posY);

    /**
     * setPreviewMirror
     *
     * @param id
     * @param isMirror
     */
    void setPreviewMirror(int id, boolean isMirror);

    /**
     * setPreviewStreamMirror
     *
     * @param id
     * @param isMirror
     */
    void setPreviewStreamMirror(int id, boolean isMirror);

    /**
     * setVideoStreamMirror
     *
     * @param id
     * @param isMirror
     */
    void setVideoStreamMirror(int id, boolean isMirror);

    /**
     * getCameraParam
     *
     * @return parameter
     */
    CameraParams getCameraParam();

    /**
     * setCameraParam
     *
     * @param cameraParam parameter
     */
    void setCameraParam(CameraParams cameraParam);

    /**
     * isBind
     *
     * @return result
     */
    boolean isBind();

    /**
     * setSurface
     *
     * @param surface
     * @param width
     * @param height
     */
    void setSurface(Surface surface, int width, int height);

    /**
     * clearSurface
     */
    void clearSurface();

    /**
     * getADASResolution
     *
     * @return resolution
     */
    int getADASResolution();

    /**
     * getDevicesID
     *
     * @return id
     */
    Constants.DevicesID getDevicesID();
}