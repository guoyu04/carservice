package com.metasequoia.manager.camera.config;

import android.media.MediaMuxer;

/**
 * 常量
 */
public class Constants {
    /**
     * DEVICES_ID_UNKNOWN, 未知
     * DEVICES_ID_PVET, 掌锐
     * DEVICES_ID_XBW, 小霸王
     * DEVICES_ID_HY, 汇影
     */
    public static enum DevicesID {
        DEVICES_ID_UNKNOWN,//未知
        DEVICES_ID_PVET, //掌锐
        DEVICES_ID_XBW,  //小霸王
        DEVICES_ID_HY, //汇影
        DEVICES_ID_AIS, //汇影
    }
    public static final int Resolution1080P = 0;
    public static final int Resolution720P = 1;
    public static final int Resolution848_480 = 2;

    public static final int PREVIEW_ = 0;
    public static final int VIDEO_ = 1;
    public static final int MAX_CHANNEL_NUM = 6;
    public static final int CHIP_CHANNEL_NUM = 3;
    public static final int RECORD_FPS_25 = 25;

    public static final int [][] PREVIEW_SIZE = {
            {1920,   1080}, // 1080P
            {1280,   720},  // 720P default
            {640,    480},  // 480P
    };
    public static final int [][] MAIN_VIDEO_SIZE = {
            {1920,   1080}, // 1080P
            {1280,   720},  // 720P default
            {640,    480},  // 480P
    };
    public static final int [][] SUB_VIDEO_SIZE = {
            {1280,   720}, // 720P
            {640,    480}, // 480P
            {352,    288}, // 352*288
    };

    public static final int BITRATE_4M = 4 * 1024 * 1024;
    public static final int BITRATE_2M = 2 * 1024 * 1024;
    public static final int BITRATE_1M = 1024 * 1024;
    public static final int BITRATE_512K = 512 * 1024;

    public static final int SEGMENT_SIZE_50M  = 50 * 1024 * 1024;
    public static final int SEGMENT_SIZE_100M = 100 * 1024 * 1024;
    public static final int SEGMENT_SIZE_200M = 200 * 1024 * 1024;
    public static final int SEGMENT_TIME_60S  = 60 * 1000;
    public static final int SEGMENT_TIME_180S = 3 * 60 * 1000;
    public static final int SEGMENT_TIME_360S = 5 * 60 * 1000;

    public static final int SEGMENT_TYPE_SIZE = 0;  //Segment recorded video by size
    public static final int SEGMENT_TYPE_TIME = 1;  //Segment recorded video by time

    public static final int MEDIA_OUTPUT_FORMAT_MP4 = MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4;
    public static final int MEDIA_OUTPUT_FORMAT_WEBM = MediaMuxer.OutputFormat.MUXER_OUTPUT_WEBM;
    public static final int MEDIA_OUTPUT_FORMAT_TS = 2;

    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String ENCODE_FORMAT_AVC = "video/avc";
    public static final String ENCODE_FORMAT_HEVC = "video/hevc";

    public static final String KEY_PREVIEW_ON = "preview-on";
    public static final String KEY_VIDEO_CB_ON = "video-cb-on";
    public static final String KEY_RECORD_NUMBER = "record-number";
    public static final String KEY_PREVIEW_SIZE = "preview-size";
    public static final String KEY_PREVIEW_FORMAT = "preview-format";
    public static final String KEY_PREVIEW_FRAME_RATE = "preview-frame-rate";
    public static final String KEY_PICTURE_SIZE = "picture-size";
    public static final String KEY_AUDIO_ENABLE = "audio-enable";
    public static final String KEY_SEG_THRE = "seg-thre";
}
