package com.metasequoia.manager.camera.config;

/**
 * 默认配置入口
 *
 * @author metasequoia
 */
public class Config {
    // 默认设置开关
    public static final boolean AUDIO_ENABLE = true;
    public static final boolean SUB_REC_ENABLE = false;

    public static final int DEFAULT_RECORD_NUM = 0;
    public static final int MAIN_VIDEO_WIDTH = Constants.MAIN_VIDEO_SIZE[1][0];
    public static final int MAIN_VIDEO_HEIGHT = Constants.MAIN_VIDEO_SIZE[1][1];
    public static final int DEFAULT_MAIN_RATE = Constants.BITRATE_4M;
    public static final int SUB_VIDEO_WIDTH = Constants.SUB_VIDEO_SIZE[1][0];
    public static final int SUB_VIDEO_HEIGHT = Constants.SUB_VIDEO_SIZE[1][1];
    public static final int DEFAULT_SUB_RATE = Constants.BITRATE_4M;

    public static final int DEFAULT_RECORD_FPS = Constants.RECORD_FPS_25;
    public static final int OUTPUT_FORMAT = Constants.MEDIA_OUTPUT_FORMAT_MP4;
    public static final int SEG_REC_TYPE = Constants.SEGMENT_TYPE_TIME;
    public static final int SEG_THRE_TIME = Constants.SEGMENT_TIME_60S;

    public static final String DEFAULT_ENCODE_FORMAT = Constants.ENCODE_FORMAT_AVC;
    public static final String DEFAULT_STORAGE_PATH = "/sdcard/DCIM";

    public static final String sNewPackageName = "com.metasequoia.carcorder";
    public static final String sOldPackageName = "com.pvetec.carcorder";
    public static final String sAisPackageName = "com.metasequoia.services";
    public static boolean sIsNwd;
    public static boolean sIsAis;
}
