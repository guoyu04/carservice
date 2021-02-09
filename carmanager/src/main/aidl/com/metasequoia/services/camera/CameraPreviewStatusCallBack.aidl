// CameraPreviewStatusCallBack.aidl
package com.metasequoia.services.camera;

// Declare any non-default types here with import statements

interface CameraPreviewStatusCallBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
        void cameraDeviceVisible();

        void cameraDeviceUnvisible();
}
