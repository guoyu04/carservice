// CarcorderRemoteCtrl.aidl
package com.metasequoia.services.camera;
import com.metasequoia.services.camera.FrameDataCallBack;
// Declare any non-default types here with import statements
import com.metasequoia.services.camera.CameraPreviewStatusCallBack;

interface CarcorderRemoteCtrl {

    void setSurface(int sessionId,int cameraDevice, int width,int height,in Surface surface);
    void setParcelFileDescriptor(in ParcelFileDescriptor pfd);
    void registerFrameByteCallBack(FrameDataCallBack frameDataCallBack);
    void unregisterFrameByteCallBack(FrameDataCallBack frameDataCallBack);
    void linkToDeath(IBinder binder);
    void unlinkToDeath(IBinder binder);
    int getDeviceID();
    int getADASResolution();
    void registerCameraPreviewStatusCallBack(CameraPreviewStatusCallBack cameraPreviewStatusCallBack);
    void unregisterCameraPreviewStatusCallBack(CameraPreviewStatusCallBack cameraPreviewStatusCallBack);
}