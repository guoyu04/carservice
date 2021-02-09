package com.metasequoia.manager.system.device;

import android.content.Context;
import android.text.TextUtils;

import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.listener.BaseListener;
import com.metasequoia.manager.system.base.model.Model;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.utils.SystemPropertiesProxy;

/**
 * 设备类：sn
 * Created by guoyu on 2020/8/12.
 */
public class DeviceModel extends BaseListener<Integer> implements IDeviceModel, Model {


	private Context mContext = null;
	private String mDevicesSN = null;
	private String mDevicesFotaDevice = null;
	private String mDevicesFotaOem = null;
	private String mDevicesFotaPlatform = null;
	private String mDevicesFotaType = null;
	private String mDevicesFotaVersion = null;
	public DeviceModel(Context context) {
		mContext = context.getApplicationContext();
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public String getDeviceSN(){
		if(TextUtils.isEmpty(mDevicesSN)) {
			mDevicesSN = SystemPropertiesProxy.get(SettingsConfig.SETTINGS_GSM_SERIAL);
		}
		return mDevicesSN;
	}
	@Override
	public int getDeviceType(){
		return SystemConfig.CAR_SERIES;
	}
	@Override
	public String getFotaDevice() {
		if(TextUtils.isEmpty(mDevicesFotaDevice)){
			mDevicesFotaDevice = SystemPropertiesProxy.get(SettingsConfig.SETTINGS_FOTA_DEVICE);
		}
		return mDevicesFotaDevice;
	}

	@Override
	public String getFotOem() {
		if(TextUtils.isEmpty(mDevicesFotaOem)){
			mDevicesFotaOem = SystemPropertiesProxy.get(SettingsConfig.SETTINGS_FOTA_OEM);
		}
		return mDevicesFotaOem;
	}

	@Override
	public String getFotaPlatform() {
		if(TextUtils.isEmpty(mDevicesFotaPlatform)){
			mDevicesFotaPlatform = SystemPropertiesProxy.get(SettingsConfig.SETTINGS_FOTA_PLATFORM);
		}
		return mDevicesFotaPlatform;
	}

	@Override
	public String getFotaType() {
		if(TextUtils.isEmpty(mDevicesFotaType)){
			mDevicesFotaType = SystemPropertiesProxy.get(SettingsConfig.SETTINGS_FOTA_TYPE);
		}
		return mDevicesFotaType;
	}

	@Override
	public String getFotaVersion() {
		if(TextUtils.isEmpty(mDevicesFotaVersion)){
			mDevicesFotaVersion = SystemPropertiesProxy.get(SettingsConfig.SETTINGS_FOTA_VERSION);
		}
		return mDevicesFotaVersion;
	}
}
