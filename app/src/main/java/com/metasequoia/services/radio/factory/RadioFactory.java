package com.metasequoia.services.radio.factory;

import com.metasequoia.manager.radio.bean.PublicDef;

import java.util.HashMap;
import java.util.Map;

public class RadioFactory {
    private static RadioFactory sRadioFactory;

    private Map<Integer, Radio> mRaidoCtrlMap = new HashMap<Integer, Radio>();

    public static RadioFactory getInstance() {
        if (sRadioFactory != null) return sRadioFactory;
        synchronized (RadioFactory.class) {
            if (sRadioFactory == null) sRadioFactory = new RadioFactory();
        }
        return sRadioFactory;
    }

    public Radio getRadio(int radio) {
        if (!mRaidoCtrlMap.containsKey(radio)) {
            if (PublicDef.RADIO_FM==radio) {
                mRaidoCtrlMap.put(PublicDef.RADIO_FM, new RadioFM());
            } else if (PublicDef.RADIO_AM==radio) {
                mRaidoCtrlMap.put(PublicDef.RADIO_AM, new RadioAM());
            }
        }
        return mRaidoCtrlMap.get(radio);
    }
}
