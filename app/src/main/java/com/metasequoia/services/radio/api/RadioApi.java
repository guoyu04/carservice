package com.metasequoia.services.radio.api;

public interface RadioApi {
        /**
     * 初始化系统
     *
     * @return
     */
    public int init();

    /**
     * 反初始化系统
     *
     * @return
     */
    public int uninit();

    /**
     * 打开天线
     * @return
     */
    public int openAntenna();

    /**
     * 关闭天线
     * @return
     */
    public int closeAntenna();
    /**
     * 初始化当前radio FM/AM
     *
     * @param band
     * @return
     */
    public int initRadio(int band);


    /**
     *
     * //TODO 添加方法功能描述
     * @sessionId scan 0 停止  1 开始
     * @return
     */
    public int setScan(int band, int startFeq, int endFreq, int sessionId);


    /**
     * 获取当前频点是否有效
     *
     * @param band
     * @param freq
     * @return
     */
    public int getQuality(int band, int freq);

    /**
     * 设置频点
     *
     * @param band
     * @param freq
     * @return
     */

    public int setFreq(int band, int freq);


    public int setMute(boolean mute);


    /**
     * 设置radio声音
     *
     * @param vol
     * @return
     */
    public int setVolume(int vol);

    /**
     * 1 :
     * @param band
     * @return
     */
    public int getSignalStatus(int band);

    /**
     * 是否启用立体声
     * @param enable true打开    false 关闭
     * @return
     */
    public int  setStereo(boolean enable);

    /**
     * 设置近程 远程
     * @param dx 0 远程 1 进程
     * @return
     */
    public int setRadioDX(int dx);
}
