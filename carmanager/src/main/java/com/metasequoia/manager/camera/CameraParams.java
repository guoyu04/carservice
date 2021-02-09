/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.metasequoia.manager.camera;

import android.media.MediaMuxer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.metasequoia.manager.camera.config.Constants;
import com.metasequoia.manager.camera.config.Config;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Information about a camera
 *
 * @hide
 */
public class CameraParams implements Parcelable {
    // Can't parcel nested classes, so make this a top level class that composes
    // CameraParams.

    private static final String TAG = "CameraParams";

    private int mRecordNum = Config.DEFAULT_RECORD_NUM;
    private int mVideoWidth = Config.MAIN_VIDEO_WIDTH;
    private int mVideoHeight = Config.MAIN_VIDEO_HEIGHT;
    private int mMainRate = Config.DEFAULT_MAIN_RATE;
    private int mSubWidth = Config.SUB_VIDEO_WIDTH;
    private int mSubHeight = Config.SUB_VIDEO_HEIGHT;
    private int mSubRate = Config.DEFAULT_SUB_RATE;

    private boolean mAudioRecEnable = Config.AUDIO_ENABLE;
    private boolean mSubRecEnable = Config.SUB_REC_ENABLE;

    private int mRecFps = Config.DEFAULT_RECORD_FPS;
    private int mOutputFormat = Config.OUTPUT_FORMAT;
    private int mSegRecType = Config.SEG_REC_TYPE;
    private int mSegThreSize = Config.SEG_THRE_TIME;

    private String mEncodeFormat = Config.DEFAULT_ENCODE_FORMAT;
    private String mStoragePath = Config.DEFAULT_STORAGE_PATH;

    //Todo channle enable map, can move above other parameters to it.
    public HashMap<String, String> mPreviewMap;

    public CameraParams() {
        mPreviewMap = new HashMap<String, String>(/*initialCapacity*/Constants.MAX_CHANNEL_NUM);
        for (int i=0 ; i<Constants.MAX_CHANNEL_NUM/2; i++) {
            setPrevChannelOn(0, i,false);
            setPrevChannelOn(2, i,false);
        }
    }

    public void setRecordNumber(int num) {
        mRecordNum = num;
    }
    public int getRecordNumber() {
        return mRecordNum;
    }
    public void setVideoWidth(int width) {
        mVideoWidth = width;
    }
    public int getVideoWidth() {
        return mVideoWidth;
    }
    public void setVideoHeight(int height) {
        mVideoHeight = height;
    }
    public int getVideoHeight() {
        return mVideoHeight;
    }
    public void setMainRate(int rate) {
        mMainRate = rate;
    }
    public int getMainRate() {
        return mMainRate;
    }

    public void setSubWidth(int width) {
        mSubWidth = width;
    }
    public int getSubWidth() {
        return mSubWidth;
    }
    public void setSubHeight(int height) {
        mSubHeight = height;
    }
    public int getSubHeight() {
        return mSubHeight;
    }
    public void setSubRate(int rate) {
        mSubRate = rate;
    }
    public int getSubRate() {
        return mSubRate;
    }

    public void setAudioRecEnable(boolean audioEnable) {
        mAudioRecEnable = audioEnable;
        Log.i(TAG, "setAudioRecEnable: " + mAudioRecEnable);
    }
    public boolean getAudioRecEnable() {
        Log.i(TAG, "getAudioRecEnable: " + mAudioRecEnable);
        return mAudioRecEnable;
    }

    public void setSubRecEnable(boolean recEnable) {
        mSubRecEnable = recEnable;
    }
    public boolean getSubRecEnable() {
        return mSubRecEnable;
    }

    public void setRecFps(int recFps) {
        mRecFps = recFps;
    }
    public int getRecFps() {
        return mRecFps;
    }
    public void setEncodeFormat(String encoderFormat) {
        mEncodeFormat = encoderFormat;
    }
    public String getEncodeFormat() {
        return mEncodeFormat;
    }

    public void setOutputFormat(int output) {
        mOutputFormat = output;
    }
    public int getOutputFormat() {
        return mOutputFormat;
    }

    public void setSegRecType(int segRecType) {
        mSegRecType = segRecType;
    }
    public int getSegRecType() {
        return mSegRecType;
    }

    public void setSegThre(int segThre) {
        mSegThreSize = segThre;
        Log.i(TAG, "setSegThre: " + mSegThreSize);
    }
    public int getSegThre() {
        Log.i(TAG, "getSegThre: " + mSegThreSize);
        return mSegThreSize;
    }

    public void setStoragePath(String path) {
        mStoragePath = path;
    }
    public String getStoragePath() {
        return mStoragePath;
    }

    /**
     * Set Video cb status of a csi and channel id
     *
     * @param csiphy
     * @param channel
     * @return
     */
    public void setCbChannelOn(int csiphy, int channel, boolean on) {
        String suffix = Integer.toString(csiphy) + "x" + Integer.toString(channel);
        set(Constants.KEY_VIDEO_CB_ON + suffix, on?Constants.TRUE:Constants.FALSE);
    }

    /**
     * Get Video cb status of a csi and channel id
     *
     * @param csiphy
     * @param channel
     * @return
     */
    public boolean getCbChannelOn(int csiphy, int channel) {
        String suffix = Integer.toString(csiphy) + "x" + Integer.toString(channel);
        return Constants.TRUE.equals(get(Constants.KEY_VIDEO_CB_ON + suffix));
    }

    /**
     * Set preview status of a csi and channel id
     *
     * @param csiphy
     * @param channel
     * @return
     */
    public void setPrevChannelOn(int csiphy, int channel, boolean on) {
        String suffix = Integer.toString(csiphy) + "x" + Integer.toString(channel);
        set(Constants.KEY_PREVIEW_ON + suffix, on?Constants.TRUE:Constants.FALSE);
    }

    /**
     * Get preview status of a csi and channel id
     *
     * @param csiphy
     * @param channel
     * @return
     */
    public boolean getPrevChannelOn(int csiphy, int channel) {
        String suffix = Integer.toString(csiphy) + "x" + Integer.toString(channel);
        return Constants.TRUE.equals(get(Constants.KEY_PREVIEW_ON + suffix));
    }

    /**
     * Set preview status of a csi id
     *
     * @param csiphy
     * @return
     */
    public void setPrevChannelOn(int csiphy, boolean on) {
        for(int i=0; i<Constants.MAX_CHANNEL_NUM/2; i++) {
            setPrevChannelOn(csiphy, i, on);
        }
    }

    /**
     * Get preview status of a csi id
     *
     * @param csiphy
     * @return
     */
    public boolean getPrevChannelOn(int csiphy) {
        boolean result = false;
        for(int i=0; i<Constants.MAX_CHANNEL_NUM/2; i++) {
            if(getPrevChannelOn(csiphy, i)) {
                result = true;
                break;
            }
        }
        Log.e(TAG, "getPrevChannelOn result: " + result);
        return result;
    }

    /**
     * Remove a String parameter
     *
     * @param key
     */
    public void remove(String key) {
        mPreviewMap.remove(key);
    }

    /**
     * Set a String parameter.
     *
     * @param key   the key name for the parameter
     * @param value the String value of the parameter
     */
    public void set(String key, String value) {
        if (key.indexOf('=') != -1 || key.indexOf(';') != -1 || key.indexOf(0) != -1) {
            Log.e(TAG, "Key \"" + key + "\" contains invalid character (= or ; or \\0)");
            return;
        }
        if (value.indexOf('=') != -1 || value.indexOf(';') != -1 || value.indexOf(0) != -1) {
            Log.e(TAG, "Value \"" + value + "\" contains invalid character (= or ; or \\0)");
            return;
        }

        put(key, value);
    }

    /**
     * Set an integer parameter.
     *
     * @param key   the key name for the parameter
     * @param value the int value of the parameter
     */
    public void set(String key, int value) {
        put(key, Integer.toString(value));
    }

    /**
     * Put an String parameter.
     *
     * @param key
     * @param value
     */
    private void put(String key, String value) {
        /*
         * Remove the key if it already exists.
         *
         * This way setting a new value for an already existing key will always move
         * that key to be ordered the latest in the map.
         */
        mPreviewMap.remove(key);
        mPreviewMap.put(key, value);
    }

    /**
     * Return the value of a String parameter.
     *
     * @param key the key name for the parameter
     * @return the String value of the parameter
     */
    public String get(String key) {
        return mPreviewMap.get(key);
    }

    /**
     * Returns the value of an integer parameter.
     *
     * @param key the key name for the parameter
     * @return the int value of the parameter
     */
    public int getInt(String key) {
        return Integer.parseInt(mPreviewMap.get(key));
    }

    /**
     * Describe Contents
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write data to Parcel
     *
     * @param out
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mRecordNum);
        out.writeInt(this.mVideoWidth);
        out.writeInt(this.mVideoHeight);
        out.writeInt(this.mMainRate);
        out.writeInt(this.mSubWidth);
        out.writeInt(this.mSubHeight);
        out.writeInt(this.mSubRate);

        out.writeByte((byte) (this.mAudioRecEnable==true?1:0));
        out.writeByte((byte) (this.mSubRecEnable==true?1:0));

        out.writeInt(this.mRecFps);
        out.writeInt(this.mOutputFormat);
        out.writeInt(this.mSegRecType);
        out.writeInt(this.mSegThreSize);
        out.writeString(this.mEncodeFormat);
        out.writeString(this.mStoragePath);

        out.writeMap(this.mPreviewMap);
    }

    /**
     * Read data from parcel
     *
     * @param in
     */
    public void readFromParcel(Parcel in) {
        this.mRecordNum = in.readInt();
        this.mVideoWidth = in.readInt();
        this.mVideoHeight = in.readInt();
        this.mMainRate = in.readInt();
        this.mSubWidth = in.readInt();
        this.mSubHeight = in.readInt();
        this.mSubRate = in.readInt();

        this.mAudioRecEnable = in.readByte()!=0;
        this.mSubRecEnable = in.readByte()!=0;

        this.mRecFps = in.readInt();
        this.mOutputFormat = in.readInt();
        this.mSegRecType = in.readInt();
        this.mSegThreSize = in.readInt();

        this.mEncodeFormat = in.readString();
        this.mStoragePath = in.readString();
        this.mPreviewMap = in.readHashMap(HashMap.class.getClassLoader());
    }

    /**
     * Parcelable CREATOR
     */
    public static final Parcelable.Creator<CameraParams> CREATOR =
            new Parcelable.Creator<CameraParams>() {
        @Override
        public CameraParams createFromParcel(Parcel in) {
            CameraParams info = new CameraParams();
            info.readFromParcel(in);

            return info;
        }

        @Override
        public CameraParams[] newArray(int size) {
            return new CameraParams[size];
        }
    };
}
