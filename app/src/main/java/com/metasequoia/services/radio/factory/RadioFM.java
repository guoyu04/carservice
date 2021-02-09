package com.metasequoia.services.radio.factory;

import android.content.Context;
import android.os.AsyncTask.Status;
import android.util.Log;

import com.metasequoia.manager.radio.bean.PublicDef;
import com.metasequoia.manager.radio.bean.RadioArea;
import com.metasequoia.manager.radio.listener.OnDataChangeListener;

public class RadioFM extends Radio.Sub {
    private static final String TAG = "RadioFM";

    RadioScanTask mScanTask = null;
    @Override
    public int init() {
        if(isScanning()) return -1;
        return mRadioCtrlApi.initRadio(PublicDef.RADIO_FM);
    }

    @Override
    public int uninit() {
        return stopScan();
    }

    @Override
    public int getId() {
        return PublicDef.RADIO_FM;
    }

    @Override
    public int setFreq(int freq) {
        if(isScanning()) {
            stopScan();
        }
        if(mRadioCtrlApi == null) return -1;
        return mRadioCtrlApi.setFreq(PublicDef.RADIO_FM, freq);
    }

    @Override
    public int scan(Context context, boolean direction, OnDataChangeListener listener) {
        if(isScanning()) return -1;
        Log.i(TAG, "fm scan..");
        if(mRadioCtrlApi == null) return -1;
        Log.i(TAG, "fm scan 1..");
        if(this.getArea() == null) return -1;
        Log.i(TAG, "fm scan 2..");
        RadioArea area = this.getArea();
        mScanTask = new RadioScanTask(context, this, mRadioCtrlApi, area, direction, listener);
        mScanTask.execute();
        return 0;
    }

    @Override
    public int stopScan() {
        if (isScanning()) {
            if(mScanTask.cancel(true))
                return 1;
            else
                return 0;
        }
        return -1;
    }

    @Override
    public boolean isScanning() {
        return (mScanTask != null && mScanTask.getStatus() != Status.FINISHED);
    }

    @Override
    public RadioArea getArea() {
        return PublicDef.sAreaMapFM.get(mCurArea);
    }

    @Override
    public int getAreaId() {
        return mCurArea;
    }

    @Override
    public RadioArea getAreaById(int areaId) {
        return PublicDef.sAreaMapFM.get(areaId);
    }

    @Override
    public int getSignalStatus(int freq) {
        if (isScanning()) return -1;
        if(mRadioCtrlApi==null) return -1;
        if(freq==0) return -1;
        Log.e(TAG,"getSignalStatus---RadioFM--->"+freq);
        return mRadioCtrlApi.getSignalStatus(freq);
    }
}
