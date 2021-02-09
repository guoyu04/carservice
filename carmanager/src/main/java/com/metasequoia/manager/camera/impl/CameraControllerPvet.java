package com.metasequoia.manager.camera.impl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Surface;

import com.metasequoia.manager.camera.CameraParams;
import com.metasequoia.services.camera.CarcorderRemoteCtrl;
import com.metasequoia.manager.camera.FrameBufferCallBack;
import com.metasequoia.manager.camera.config.Constants;
import com.metasequoia.manager.camera.Controller;
import com.metasequoia.manager.camera.MemoryFile;
import com.metasequoia.manager.camera.base.BaseCameraControl;
import com.metasequoia.manager.camera.utils.Utils;

public class CameraControllerPvet extends BaseCameraControl {

    private CarcorderRemoteCtrl mCarcorderRemoteCtrl;
    private volatile static Controller mInstance;

    private BroadcastReceiver mReverseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mContext != null && mSurface != null && mSurface.isValid() && !Utils.isBackground(mContext)) {
                setSurface(mSurface, mWidth, mHeight);
            }
        }
    };

    private CameraControllerPvet() {

    }

    public static Controller getInstance() {
        if (mInstance == null) {
            synchronized (CameraControllerNwd.class) {
                if (mInstance == null) {
                    mInstance = new CameraControllerPvet();
                }
            }
        }
        return mInstance;
    }

    @Override
    protected void updateSurface(Surface surface, int width, int height) {
        if (mCarcorderRemoteCtrl != null) {
            try {
                mCarcorderRemoteCtrl.setSurface(surface != null ? surface.hashCode() : (mSurface == null ? 0 : mSurface.hashCode()), 1, width, height, surface);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int init(Context context) {
        super.init(context);
        IntentFilter intentFilter = new IntentFilter("com.metasequoia.carcorder.reverse.stop");
        mContext.registerReceiver(mReverseBroadcastReceiver, intentFilter);
        return 0;
    }

    @Override
    public void release() {
        if (isBind) {
            mContext.unregisterReceiver(mReverseBroadcastReceiver);
        }
        super.release();
        mInstance = null;
    }

    @Override
    public Constants.DevicesID getDevicesID() {
        return Constants.DevicesID.DEVICES_ID_PVET;
    }

    @Override
    public int getADASResolution() {
        return Constants.Resolution720P;
    }

    @Override
    protected void setRemoteCtrl(IBinder service) {
        if (service == null) {
            mCarcorderRemoteCtrl = null;
        } else {
            mCarcorderRemoteCtrl = CarcorderRemoteCtrl.Stub.asInterface(service);
        }
    }

    @Override
    protected MemoryFile getFrontMemoryFile(FrameBufferCallBack callBack) {
        return MemoryFilePvetFront.getInstance();
    }

    @Override
    protected MemoryFile getBackMemoryFile(FrameBufferCallBack callBack) {
        return MemoryFilePvetBack.getInstance();
    }

    @Override
    protected int initService() {
        Intent intent = new Intent("pvetec.intent.action.carcorder.carcorderservice.start");
        intent.setClassName("com.pvetec.carcorder", "com.pvetec.carcorder.CarcorderService");
        mContext.bindService(intent, this, Service.BIND_AUTO_CREATE);
        return 0;
    }
}
