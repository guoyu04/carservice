package com.metasequoia.manager.camera;

public interface FrameBufferCallBack {
    void onFrame(byte[] frame, int len);
}
