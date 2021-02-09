package com.metasequoia.services.radio.factory;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {

	private static SharedPreferencesUtils sharedPreferencesUtils;

	public static final String RADIO_BAND = "radio_band";

	public static final String FM_FREQUENCY = "fm_frequency";

	public static final String AM_FREQUENCY = "am_frequency";

	/**
     * 收音区域id
     */
    public static final String RADIO_AREA_ID = "extra_radio_area_type";
    /**
     * 收音远近程
     */
    public static final String RADIO_DX_TYPE = "extra_radio_distance_type";
	/**
     * 收音立体声
     */
	public static final String RADIO_STEREO_TYPE ="radio_stereo_status";

	SharedPreferences sp;


	private SharedPreferencesUtils(Context context){
		 sp = context.getSharedPreferences("pvetec_radio", Context.MODE_PRIVATE);
	}

	public static SharedPreferencesUtils getInstance(Context context){
		if(null==sharedPreferencesUtils){
			synchronized (SharedPreferencesUtils.class) {
				if(null==sharedPreferencesUtils){
					sharedPreferencesUtils=new SharedPreferencesUtils(context);
				}
			}
		}

		return sharedPreferencesUtils;
	}


	public void putIntValue(String key,int value){
		if(null!=sp){
			sp.edit().putInt(key, value).commit();
		}
	}

	public int getIntValue(String key,int defalut){
		int value=-1;
		if(null!=sp){
			value=sp.getInt(key, defalut);
		}
		return value;
	}

	public void putBooleanValue(String key,boolean value){
		if(null!=sp){
			sp.edit().putBoolean(key, value).commit();
		}
	}

	public boolean getBooleanValue(String key,boolean defaultValue){
		boolean value=false;
		if(null!=sp){
			value=sp.getBoolean(key, defaultValue);
		}
		return value;
	}


	  public int getRadioDxType(Context context) {
	        int value = 0;//默认远程
	        try {
	            value = android.provider.Settings.System.getInt(context.getContentResolver(), RADIO_DX_TYPE);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return value;
	    }

	  public int getRadioAreaId(Context context) {
	        int value = 1;//默认中国
	        try {
	            value = android.provider.Settings.System.getInt(context.getContentResolver(), RADIO_AREA_ID);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return value;
	    }
}
