package com.metasequoia.manager.radio.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Frequency implements Parcelable {

    private int id;
    private int mFrequency;
    private int mBandType;
    private String mPSName;
    private int index;
    //private int mArea;
    private String uninumame;

    public static final Creator<Frequency> CREATOR = new Creator<Frequency>() {

        @Override
        public Frequency createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Frequency(source);
        }

        @Override
        public Frequency[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Frequency[size];
        }
    };

    public Frequency(int id, int frequency, int bandType, String psName, int index, /*int area,*/ String uninuname) {
        this.id = id;
        this.mFrequency = frequency;
        this.mBandType = bandType;
        this.mPSName = psName;
        this.index = index;
        //this.mArea = area;
        this.uninumame = uninuname;
    }


    private Frequency(Parcel paramParcel) {
        this.id = paramParcel.readInt();
        this.mFrequency = paramParcel.readInt();
        this.mBandType = paramParcel.readInt();
        this.mPSName = paramParcel.readString();
        this.index = paramParcel.readInt();
        //this.mArea = paramParcel.readInt();
        this.uninumame = paramParcel.readString();
    }

    public void copy(Frequency paramFrequency) {
        this.id = paramFrequency.getId();
        this.mFrequency = paramFrequency.getFrequency();
        this.mBandType = paramFrequency.getBandType();
        this.mPSName = paramFrequency.getPSName();
        this.index = paramFrequency.getIndex();
        //this.mArea = paramFrequency.getArea();
        this.uninumame = paramFrequency.getUninumame();
    }

    public int getId() {
        return this.id;
    }

    public int getFrequency() {
        return this.mFrequency;
    }

    public int getBandType() {
        return this.mBandType;
    }

    public String getPSName() {
        return this.mPSName;
    }

    public int getIndex() {
        return this.index;
    }

//    public int getArea() {
//        return this.mArea;
//    }

    public String getUninumame() {
        return this.uninumame;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFrequency(int paramInt) {
        this.mFrequency = paramInt;
    }

    public void setBandType(int paramByte) {
        this.mBandType = paramByte;
    }

    public void setPSName(String paramString) {
        this.mPSName = paramString;
    }

    public void setIndex(int index) {
        this.index = index;
    }

//    public void setArea(int area) {
//        this.mArea = area;
//    }

    public void setUninumame(String uninumame) {
        this.uninumame = uninumame;
    }

    public String toString() {
        return "frequency = " + this.mFrequency + ",band = " + this.mBandType + ",psname=" + this.mPSName + ",index=" + this.index + /*",area=" + mArea + */",uninumame=" + uninumame;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeInt(this.id);
        dest.writeInt(this.mFrequency);
        dest.writeInt(this.mBandType);
        dest.writeString(this.mPSName);
        dest.writeInt(this.index);
        //dest.writeInt(this.mArea);
        dest.writeString(this.uninumame);
    }

    private static final String TAG = "Frequency";
    /**
     * 用来List<Frequency>中contains对比
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if(this == obj) {
            Log.i(TAG, "Frequency---0");
            return true;
        }
        if(!(obj instanceof Frequency)) {
            Log.i(TAG, "Frequency---1");
            return false;
        }

        Frequency freq = (Frequency) obj;
        if(this.toString().equals(freq.toString())) {
            return true;
        } else {
            return false;
        }
    }
}
