package com.metasequoia.manager.camera.listener;

/**
 * Camera SDK状态监听。
 *
 * @author metasequoia
 */
public interface ICameraStateListener {

    /**
     * DMS运行状态回调。
     *
     * @param code 状态码
     */
    void onCameraState(int code);
}
