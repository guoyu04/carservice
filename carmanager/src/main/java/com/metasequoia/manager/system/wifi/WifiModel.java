package com.metasequoia.manager.system.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.metasequoia.manager.listener.BaseListener;
import com.metasequoia.manager.system.base.model.Model;

/**
 * Wi-Fi状态管理类
 * Created by guoyu on 2020/8/12.
 */
public class WifiModel extends BaseListener<WifiState> implements IWifiStateCallBack, Model {
	private Context mContext;

	/**
	 * Wi-Fi状态监听
	 */
	private WifiStateBroadcastReciver mBootBroadcastReciver = null;
	/**
	 * wifi manager
	 */
	WifiManager mWifiManager;
	public WifiModel(Context context) {
		mContext = context.getApplicationContext();
		mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	public void onCreate() {
		mBootBroadcastReciver = new WifiStateBroadcastReciver(mContext, WifiManager.WIFI_STATE_CHANGED_ACTION);
		mBootBroadcastReciver.registerBroadcastListener(this);
	}

	@Override
	public void onDestroy() {
		mBootBroadcastReciver.unregisterBroadcastListener(this);
	}

	/**
	 * wifi状态监听并分发消息
	 * @param state wifi状态
	 */
	@Override
	public void onWifiStateChange(WifiState state) {
		this.dispatchOnChanged(state);
	}

	/**
	 * 获取当前Wi-Fi状态
	 * @return Wi-Fi状态
	 */
	public WifiState getWifiState() {
		return WifiState.doTranslateValue(mWifiManager.getWifiState());
	}

	/**
	 * 开启或者关闭Wi-Fi.
	 * <p>
	 * Applications must have the {@link android.Manifest.permission#CHANGE_WIFI_STATE}
	 * permission to toggle wifi.
	 *
	 * @param enabled {@code true} to enable, {@code false} to disable.
	 * @return {@code false} if the request cannot be satisfied; {@code true} indicates that wifi is
	 *         either already in the requested state, or in progress toward the requested state.
	 * @throws  {@link SecurityException} if the caller is missing required permissions.
	 */
	public void setWifiState(boolean enabled){
		mWifiManager.setWifiEnabled(enabled);
	}
}