package com.metasequoia.services.radio.factory;

import android.content.Context;
import android.util.Log;

import com.metasequoia.services.radio.api.RadioApi;
import com.metasequoia.manager.radio.bean.PublicDef;
import com.metasequoia.manager.radio.bean.RadioArea;
import com.metasequoia.manager.radio.listener.OnDataChangeListener;

public interface Radio {

    /**
     * init radio devices
     * @return
     * false: -1
     * success:1
     */
    public int init();

    public int uninit();

    /**
     * get radio mode : fm or am
     * @return
     * fm: 1
     * am: 2
     */
    public int getId();   //fm or am

    /**
     * setFrequency
     * @param freq
     * @return
     *
     * faild:-1 ?
     * successe:1
     */
    public int setFreq(int freq);

    /**
     * scan
     * @param context
     * @param direction
     * @param listener
     * @return
     * false: -1 (not start scan)
     * success: 0 (start scan)
     *
     */
    public int scan(Context context, boolean direction, OnDataChangeListener listener);

    /**
     * stop scan
     * @return
     * stop scan success:0
     * stop scan false:1
     * not scaning now:-1
     */
    public int stopScan();

    public boolean isScanning();

    public RadioArea getArea();

    /**
     * set AreaId see Radio.Sub
     * @param areaId
     * @return
     * success: not -1
     * false: -1
     */
    public int setAreaId(int areaId);

    public RadioArea getAreaById(int areaId);

    public int getAreaId();

    public void setRadioApi(RadioApi api);

    public int getSignalStatus(int freq);

    public static abstract class Sub implements Radio {
        protected int mCurArea = PublicDef.RADIO_AREA_CHINA;

        protected RadioApi mRadioCtrlApi;

        public int setAreaId(int areaId) {
            Log.i("Radio", "Radio setAreaId:" + areaId);
            mCurArea = areaId;
            return mCurArea;
        }

        public void setRadioApi(RadioApi api) {
            mRadioCtrlApi = api;
        }

    }
}
