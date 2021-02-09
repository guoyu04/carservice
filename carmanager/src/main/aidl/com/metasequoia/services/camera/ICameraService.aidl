// ICameraService.aidl
package com.metasequoia.services.camera;

// Declare any non-default types here with import statements
import com.metasequoia.services.camera.FrameDataCallBack;
import com.metasequoia.services.camera.ICameraClient;
import com.metasequoia.manager.camera.CameraParams;
import android.view.Surface;
import android.content.Context;

interface ICameraService {
    //Preview
    String getSdkVersion();
    int addClient(ICameraClient client);
    int removeClient(ICameraClient client);
    int initCamera(int type);
    int openCamera(int csiphy, int inputNum);
    int closeCamera(int csiphy);
    void startPreview(int csiphy, int channel, in Surface surface, int width, int height, int fmt);
    void stopPreview(int csiphy, int channel);
    void setPreviewSize(int csiphy, int channel, int width, int height);
    void setFps(int csiphy, int channel, int fps);
    void setPreviewMirror(int csiphy, int channel, boolean isMirror);
    void setPreviewStreamMirror(int csiphy, int channel, boolean isMirror);
    void setVideoStreamMirror(int csiphy, int channel, boolean isMirror);

    //Video Record
    void startVideoRecord(int csiphy, int channel);
    void stopVideoRecord(int csiphy, int channel);
    void setLockVideo(boolean isLock, int lockSegmentType, int lockSegmentThreshold);
    CameraParams getCameraParams();
    void setCameraParams(inout CameraParams cameraInfo);
    void handleCollision();

    //Jpeg and Water Mark
    boolean getTakePicStartFlag();
    String startTakePic(int csiphy, int channel, int width, int height);
    long initWaterMark(int type, in byte[] fontPath, int fontSize);
    int deinitWaterMark(long handle);
    int setWaterMark(long handle, in byte[] data, int index, int posX, int posY);
    int setWaterMarkWithChinese(long handle, in byte[] data, int index, int posX, int posY);
    int clearWaterMark(long handle, int index);
    int enableWaterMark(long handle);
    int disableWaterMark(long handle);

    //SD Storage
    void setRootStoragePath(String path);
    String getStoragePath(boolean type);

    //Shared Memory
    void setParcelFileDescriptor(in ParcelFileDescriptor pfd);
    void registerFrameByteCallBack(FrameDataCallBack frameDataCallBack);
    void unregisterFrameByteCallBack(FrameDataCallBack frameDataCallBack);
    int getDeviceID();
    int getADASResolution();
}
