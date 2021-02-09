package com.metasequoia.manager.radio.listener;

import com.metasequoia.manager.radio.bean.Frequency;

import java.util.List;

public interface OnDataChangeListener {
    /**
     * 扫描状态改变
     *
     *
     * @param effect
     */
    public void onScanChanged(Frequency frequency, int effect);

    /**
     * 当前频率，band等变化
     * @param frequency
     */
    public void onCurrentFrequencyChanged(Frequency frequency);

    /**
     * 扫描结束
     *
     * @param backParam
     */
    public void onEnd(String backParam);

    /**
     * 立体声
     * @param isStereoOn
     */
    public void onStereoChanged(boolean isStereoOn);

    /**
     * 远近程
     * @param isNearOn
     */
    public void onRadioDXChanged(boolean isNearOn);

    /**
     * radio init state
     * @param result
     */
    public void onRadioStateChanged(int result);

    /**
     * 读/写收音机信号强度回调
     * @param isRead
     * true：读
     * false：写
     * @param fmCustCfg
     *
     * @param rssi
     *
     */
    public void onRssiCallback(boolean isRead, List<String> fmCustCfg, int rssi);
}
