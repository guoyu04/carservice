package com.metasequoia.manager.radio.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RadioArea implements Parcelable {

    private int mFrequencyMin;

    private int mFrequencyMax;

    private int mFrequencyStep;

    private int mFactor;

    public static final Creator<RadioArea> CREATOR = new Creator<RadioArea>() {

        @Override
        public RadioArea createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new RadioArea(source);
        }

        @Override
        public RadioArea[] newArray(int size) {
            // TODO Auto-generated method stub
            return new RadioArea[size];
        }
    };

    public RadioArea(int min, int max, int step, int factor) {
        this.mFrequencyMin = min;
        this.mFrequencyMax = max;
        this.mFrequencyStep = step;
        this.mFactor = factor;
    }

    private RadioArea(Parcel paramParcel) {
        this.mFrequencyMin = paramParcel.readInt();
        this.mFrequencyMax = paramParcel.readInt();
        this.mFrequencyStep = paramParcel.readInt();
        this.mFactor = paramParcel.readInt();
    }

    public int getFrequencyMax() {
        return this.mFrequencyMax;
    }

    public int getFrequencyMin() {
        return this.mFrequencyMin;
    }

    public int getFrequencyStep() {
        return this.mFrequencyStep;
    }

    public int getFactor() {
        return this.mFactor;
    }

    public static Creator<RadioArea> getCreator() {
        return CREATOR;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("min = ").append(this.mFrequencyMin).append(",max = ").append(this.mFrequencyMax).append(",step = ").append(this.mFrequencyStep).append(",factor = ").append(this.mFactor);
        return stringBuilder.toString();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeInt(this.mFrequencyMin);
        dest.writeInt(this.mFrequencyMax);
        dest.writeInt(this.mFrequencyStep);
        dest.writeInt(this.mFactor);
    }
}
