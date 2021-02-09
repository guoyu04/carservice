package com.metasequoia.manager.camera;

import android.database.Observable;

import com.metasequoia.manager.camera.listener.ICameraStateListener;

/**
 * Observable管理类，用于控制监听。
 *
 * @author metasequoia
 */
public class CameraObservableManager implements ICameraStateListener {
    public static final int CAMERA_SREVICE_DIED = 0;
    public static final int CAMERA_SREVICE_CONNECT = 1;
    public static final int CAMERA_STATUS_INSERT = 2;
    public static final int CAMERA_STATUS_UNPLUG = 3;
    public static final int CAMERA_STATUS_PREVIEW_VISIBLE = 4;
    public static final int CAMERA_STATUS_PREVIEW_UNVISIBLE = 5;


    private static CameraObservableManager sInstance;
    private CameraStateObservable mCameraStateObservable = new CameraStateObservable();

    private CameraObservableManager() {
    }

    public static CameraObservableManager getInstance() {
        if (sInstance == null) {
            synchronized (CameraObservableManager.class) {
                if (sInstance == null) {
                    sInstance = new CameraObservableManager();
                }
            }
        }
        return sInstance;
    }

    public void registerCameraStateListener(ICameraStateListener listener) {
        mCameraStateObservable.registerObserver(listener);
    }

    public void unregisterCameraStateListener(ICameraStateListener listener) {
        mCameraStateObservable.unregisterObserver(listener);
    }

    @Override
    public void onCameraState(int code) {
        mCameraStateObservable.onCameraState(code);
    }
    /**
     * SDK状态控制
     */
    private class CameraStateObservable extends Observable<ICameraStateListener> implements ICameraStateListener {
        @Override
        public void registerObserver(ICameraStateListener observer) {
            if (mObservers.contains(observer)) {
                return;
            }
            super.registerObserver(observer);
        }

        @Override
        public void unregisterObserver(ICameraStateListener observer) {
            if (!mObservers.contains(observer)) {
                return;
            }
            super.unregisterObserver(observer);
        }

        @Override
        public void onCameraState(int code) {
            synchronized (mObservers) {
                for (int i = 0; i < mObservers.size(); ++i) {
                    mObservers.get(i).onCameraState(code);
                }
            }
        }
    }
}
