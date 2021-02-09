// IFMRadioService.aidl
package com.metasequoia.services.radio;

// Declare any non-default types here with import statements
import com.metasequoia.services.radio.IRadioClient;
import com.metasequoia.manager.radio.bean.Frequency;
import com.metasequoia.manager.radio.bean.RadioArea;

interface IRadioService {
    int addClient(IRadioClient client);
    void initRadio(int type);
    void play();
    void init();
    int uninitRadio();
    int openAntenna();
    int closeAntenna();
    void AM();
    void FM();
    int setAreaId(int areaId);
    RadioArea[] getArea();
    RadioArea[] getAreaById(int areaId);
    void search();
    void seek();
    void searchByDirection(boolean direction);
    int stopSearch();
    boolean isSupportStereo();
    void setStereo(boolean enable);
    void setRadioDX(int dx);
    boolean getStereo();
    boolean getRadioDX();
    boolean isScanning();
    void setFrequency(int freq);
    int getBand();
    int setVolume(int vol);
    int setMute(boolean mute);
    Frequency[] getPrefabFrequency();
    Frequency getCurrentFrequency();
    void nextChannel();
    void preChannel();
    int getSignalStatus(int freq);
    void getRadioInfo();
    void sendRadioCommand(byte param0, byte param1);
    boolean isSupportRadioDx();
    boolean isSupportBand(int bandType);
    boolean isSupportScaningCallback();
    boolean isSupportSetRssi();
    void getRssi();
    void setRssi(in List<String> fmCustCfgs, int values);
    boolean isSupportAntenna();
    boolean isSupportRssi();
    String getAntennaState();
    void saveRadioInfo(in Frequency frequency, in String str);
}
