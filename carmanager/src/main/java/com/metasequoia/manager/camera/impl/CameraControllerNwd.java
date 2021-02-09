package com.metasequoia.manager.camera.impl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import com.metasequoia.manager.camera.CameraObservableManager;
import com.metasequoia.manager.camera.CameraParams;
import com.metasequoia.services.camera.CameraPreviewStatusCallBack;
import com.metasequoia.services.camera.CarcorderRemoteCtrl;
import com.metasequoia.services.camera.FrameDataCallBack;
import com.metasequoia.manager.camera.FrameBufferCallBack;
import com.metasequoia.manager.camera.config.Constants;
import com.metasequoia.manager.camera.Controller;
import com.metasequoia.manager.camera.MemoryFile;
import com.metasequoia.manager.camera.base.BaseCameraControl;

public class CameraControllerNwd extends BaseCameraControl {
    private CarcorderRemoteCtrl mCarcorderRemoteCtrl;
    private volatile static Controller mInstance;
    private FrameBufferCallBack mCallBack;
    private Constants.DevicesID mDevicesID;
    private int mResolution = -1;
    private boolean isbind;
    private FrameDataCallBack mFrameDataCallBack = new FrameDataCallBack.Stub() {
        @Override
        public void canReadFrameData() throws RemoteException {
            if (mFrontMemoryFile != null) {
                mFrontMemoryFile.readShareBuffer();
            }
        }
    };

    private CameraPreviewStatusCallBack mRemoteCameraDevicesStatusCallBack = new CameraPreviewStatusCallBack.Stub() {

        @Override
        public void cameraDeviceVisible() throws RemoteException {
            CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_STATUS_PREVIEW_VISIBLE);
        }

        @Override
        public void cameraDeviceUnvisible() throws RemoteException {
            CameraObservableManager.getInstance().onCameraState(CameraObservableManager.CAMERA_STATUS_PREVIEW_UNVISIBLE);
        }
    };


    private CameraControllerNwd() {

    }

    public static Controller getInstance() {
        if (mInstance == null) {
            synchronized (CameraControllerNwd.class) {
                if (mInstance == null) {
                    mInstance = new CameraControllerNwd();
                }
            }
        }
        return mInstance;
    }

    @Override
    protected void setRemoteCtrl(IBinder service) {
        if (service == null) {
            mResolution = -1;
            mDevicesID = null;
            if (mCarcorderRemoteCtrl != null) {
                try {
                    mCarcorderRemoteCtrl.unlinkToDeath(mFrameDataCallBack.asBinder());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCarcorderRemoteCtrl = null;
            setFrontBufferCallBack(null);
        } else {
            mCarcorderRemoteCtrl = CarcorderRemoteCtrl.Stub.asInterface(service);
            if (mCarcorderRemoteCtrl != null) {
                try {
                    mCarcorderRemoteCtrl.linkToDeath(mFrameDataCallBack.asBinder());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (mFrontMemoryFile == null) {
                    setFrontBufferCallBack(mCallBack);
                }
                if (mCallBack != null) {
                    try {
                        mCarcorderRemoteCtrl.setParcelFileDescriptor(mFrontMemoryFile.getParcelFileDescriptor());
                        mCarcorderRemoteCtrl.registerFrameByteCallBack(mFrameDataCallBack);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                if (mRemoteCameraDevicesStatusCallBack != null) {
                    try {
                        mCarcorderRemoteCtrl.registerCameraPreviewStatusCallBack(mRemoteCameraDevicesStatusCallBack);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected MemoryFile getFrontMemoryFile(FrameBufferCallBack callBack) {
        mCallBack = callBack;
        if (mCarcorderRemoteCtrl != null) {
            try {
                if (callBack != null) {
                    if (getADASResolution() == Constants.Resolution720P) {
                        mCarcorderRemoteCtrl.setParcelFileDescriptor(MemoryFileHuiying.getInstance().getParcelFileDescriptor());
                    } else if (getADASResolution() == Constants.Resolution848_480) {
                        mCarcorderRemoteCtrl.setParcelFileDescriptor(MemoryFileXbw.getInstance().getParcelFileDescriptor());
                    }
                    mCarcorderRemoteCtrl.registerFrameByteCallBack(mFrameDataCallBack);
                } else {
                    mCarcorderRemoteCtrl.unregisterFrameByteCallBack(mFrameDataCallBack);
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
        if (mCarcorderRemoteCtrl != null) {
            try {
                if (callBack != null) {
                    if (getADASResolution() == Constants.Resolution720P) {
                        mCarcorderRemoteCtrl.setParcelFileDescriptor(MemoryFileHuiying.getInstance().getParcelFileDescriptor());
                    } else if (getADASResolution() == Constants.Resolution848_480) {
                        mCarcorderRemoteCtrl.setParcelFileDescriptor(MemoryFileXbw.getInstance().getParcelFileDescriptor());
                    }
                    mCarcorderRemoteCtrl.registerFrameByteCallBack(mFrameDataCallBack);
                } else {
                    mCarcorderRemoteCtrl.unregisterFrameByteCallBack(mFrameDataCallBack);
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
        if (mCarcorderRemoteCtrl == null) return;
        try {
            mCarcorderRemoteCtrl.setSurface(surface == null ? 0 : surface.hashCode(), 1, width, height, surface);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int initService() {
        Intent intent = new Intent("com.metasequoia.carcorder.service.CarcorderService");
        intent.setClassName("com.metasequoia.carcorder", "com.metasequoia.carcorder.service.CarcorderService");
        isbind = mContext.bindService(intent, this, Service.BIND_AUTO_CREATE);
        Log.d("metasequoia", "bind = " + isBind);
        return 0;
    }

    @Override
    public void release() {
        if (mCarcorderRemoteCtrl != null) {
            try {
                mCarcorderRemoteCtrl.unregisterFrameByteCallBack(mFrameDataCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (mRemoteCameraDevicesStatusCallBack != null) {
            try {
                mCarcorderRemoteCtrl.unregisterCameraPreviewStatusCallBack(mRemoteCameraDevicesStatusCallBack);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        super.release();
        mInstance = null;
    }

    @Override
    public Constants.DevicesID getDevicesID() {
        if (mCarcorderRemoteCtrl != null) {
            try {
                switch (mCarcorderRemoteCtrl.getDeviceID()) {
                    case 2:
                        mDevicesID = Constants.DevicesID.DEVICES_ID_XBW;
                        break;
                    case 3:
                        mDevicesID = Constants.DevicesID.DEVICES_ID_HY;
                        break;
                    default:
                        mDevicesID = Constants.DevicesID.DEVICES_ID_XBW;
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
        if (mCarcorderRemoteCtrl != null) {
            try {
                int value = mCarcorderRemoteCtrl.getADASResolution();
                switch (value) {
                    case 2:
                        mResolution = Constants.Resolution848_480;
                        break;
                    case 1:
                        mResolution = Constants.Resolution720P;
                        break;
                    default:
                        mResolution = Constants.Resolution848_480;
                        break;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return mResolution;
    }
}
