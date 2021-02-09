package com.metasequoia.manager.radio.bean;

import com.metasequoia.manager.radio.listener.OnDataChangeListener;

import java.util.HashMap;
import java.util.Map;

public class PublicDef {
    public static int RADIO_FM = 1;

    public static int RADIO_AM = 2;
    // 中国
    public static final int RADIO_AREA_CHINA = 1;
    // 日本
    public static final int RADIO_AREA_JAPAN = 2;

    // 美国
    public static final int RADIO_AREA_AMERICA = 3;

    // 拉美
    public static final int RADIO_AREA_LATIN_AMERICA = 4;

    // 欧洲
    public static final int RADIO_AREA_EUROPE = 5;

    // 俄罗斯
    public static final int RADIO_AREA_RUSSIA = 6;

    public static final int [][] PREB_FREQUENCY_FM = {
            {8750,   8780,  8830,  8860,  8980,  9120},  //default
            {8750,   8780,  8830,  8860,  8980,  9120}, // 中国
            {8750,   8780,  8830,  8860,  8980,  9130}, // 日本
            {8750,   8780,  8830,  8870,  8990,  9130}, // 美国
            {8750,   8780,  8830,  8870,  8990,  9130}, // 拉美
            {8750,   8780,  8830,  8860,  8980,  9120}, // 欧洲
            {6500,   6580,  6830,  6860,  6980}, // 俄罗斯

    };
    public static final int [][] PREB_FREQUENCY_AM = {
            {522,   603,   999,  1404,  1620}, // default
            {522,   603,   999,  1404,  1620}, // 中国
            {522,   603,   999,  1404,  1620}, // 日本
            {530,   600,  1000,  1400,  1710}, // 美国
            {530,   600,  1000,  1400,  1710}, // 拉美
            {522,   603,   999,  1404,  1620}, // 欧洲
            {522,   603,   999,  1404,  1620}, // 俄罗斯
    };

    public static int RADIO_MUTE_DISABLE = 0;

    public static int RADIO_MUTE_ACTIVE = 1;

    public static int RAIDO_DX_REMOTE=1;

    public static int RADIO_DX_NEAR=2;

    public static int AreaType = 0;

    public static int preAreaType = 0;

    public static final int EFFECTCOUNT = 15;

    public static class ScanParam {

        private boolean direction;

        private OnDataChangeListener listener;

        public OnDataChangeListener getListener() {
            return listener;
        }

    }


    public static Map<Integer, RadioArea> sAreaMapAM = new HashMap<Integer, RadioArea>();
    static {
        sAreaMapAM.put(PublicDef.RADIO_AREA_CHINA, new RadioArea(522, 1710, 9, 1));
        sAreaMapAM.put(PublicDef.RADIO_AREA_JAPAN, new RadioArea(522, 1620, 9, 1));
        sAreaMapAM.put(PublicDef.RADIO_AREA_AMERICA, new RadioArea(530, 1710, 10, 1));
        sAreaMapAM.put(PublicDef.RADIO_AREA_LATIN_AMERICA, new RadioArea(530, 1710, 10, 1));
        sAreaMapAM.put(PublicDef.RADIO_AREA_EUROPE, new RadioArea(522, 1710, 9, 1));
        sAreaMapAM.put(PublicDef.RADIO_AREA_RUSSIA, new RadioArea(522, 1620, 9, 1));
    }

    public static Map<Integer, RadioArea> sAreaMapFM = new HashMap<Integer, RadioArea>();

    static {
        sAreaMapFM.put(PublicDef.RADIO_AREA_CHINA, new RadioArea(8750, 10800, 10, 100));
        sAreaMapFM.put(PublicDef.RADIO_AREA_JAPAN, new RadioArea(7600, 9000, 10, 100));
        sAreaMapFM.put(PublicDef.RADIO_AREA_AMERICA, new RadioArea(8750, 10800, 10, 100));
        sAreaMapFM.put(PublicDef.RADIO_AREA_LATIN_AMERICA, new RadioArea(8750, 10800, 10, 100));
        sAreaMapFM.put(PublicDef.RADIO_AREA_EUROPE, new RadioArea(8750, 10800, 10, 100));
        sAreaMapFM.put(PublicDef.RADIO_AREA_RUSSIA, new RadioArea(6500, 7400, 10, 100));
    }
}
