package com.metasequoia.services.radio.api;

import android.util.Log;

public class RadioApiImpl implements RadioApi{

    static  {
        Log.i("RadioApiImpl", "RadioApiImpl --- load library...");
        System.loadLibrary("radioLib");

    }

    @Override
    public int init() {
        Log.i("RadioApiImpl", "RadioApiImpl init ...");
        return native_init();
    }

    @Override
    public int uninit() {
        return native_uninit();
    }

    public int openAntenna() {
        return native_openAntenna();
    }

    public int closeAntenna() {
        return native_closeAntenna();
    }

    @Override
    public int initRadio(int band) {
        return native_initRadio(band);
    }

    @Override
    public int setScan(int band, int startFeq, int endFreq, int sessionId) {
        return native_setScan(band, startFeq, endFreq, sessionId);
    }

    @Override
    public int getQuality(int band, int freq) {
        return native_getQuality(band, freq);
    }

    @Override
    public int setFreq(int band, int freq) {
        return native_setFreq(band, freq);
    }

    @Override
    public int setMute(boolean mute) {
        return native_setMute(mute ? 1 : 0);
    }

    @Override
    public int setVolume(int vol) {
        return native_setVolume(vol);
    }

    public int getSignalStatus(int band) {
        return native_getSignalStatus(band);
    }

    public int setStereo(boolean enable) {
        return native_setStereo(enable ? 1 : 0);
    }

    public int setRadioDX(int dx) {
        return native_setRadioDX(dx);
    }

    protected native int native_init();

    protected native int native_uninit();

    protected native int native_openAntenna();

    protected native int native_closeAntenna();

    protected native int native_initRadio(int band);

    protected native int native_getSignalStatus(int band);

    protected native int native_setMute(int mode);

    protected native int native_setVolume(int vol);

    protected native int native_setScan(int band, int startFeq, int endFreq, int sessionId);

    protected native int native_getQuality(int band, int freq);

    protected native int native_setFreq(int band, int freq);

    protected native int native_setStereo(int enable);

    protected native int native_setRadioDX(int dx);


}
