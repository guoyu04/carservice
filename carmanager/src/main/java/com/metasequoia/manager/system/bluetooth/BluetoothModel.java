package com.metasequoia.manager.system.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.metasequoia.manager.listener.BaseListener;
import com.metasequoia.manager.system.base.model.Model;

/**
 * Bluetooth状态管理类
 * Created by guoyu on 2020/8/12.
 */
public class BluetoothModel extends BaseListener<BluetoothState> implements IBluetoothStateCallBack, Model {
	private Context mContext;

	/**
     * Bluetooth状态监听
	 */
	private BluetoothBroadcastReciver mBootBroadcastReciver = null;

	private BluetoothAdapter  mBluetoothAdapter;
	public BluetoothModel(Context context) {
		mContext = context.getApplicationContext();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void onCreate() {
		mBootBroadcastReciver = new BluetoothBroadcastReciver(mContext, BluetoothAdapter.ACTION_STATE_CHANGED);
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
	public void onBluetoothStateChange(BluetoothState state) {
		this.dispatchOnChanged(state);
	}

	/**
	 * 获取当前Bluetooth状态
	 * @return Bluetooth状态
	 */
	public BluetoothState getBluetoothState() {
		if(mBluetoothAdapter == null){
			return BluetoothState.STATE_UNKNOWN;
		}
		return BluetoothState.doTranslateValue(mBluetoothAdapter.getState());
	}

	/**
	 * 开启或者关闭Bluetooth.
	 *
	 * @param enabled {@code true} to enable, {@code false} to disable.
	 */
	public void setBluetoothState(boolean enabled){
		if(mBluetoothAdapter == null){
			return;
		}
		if(enabled){
			mBluetoothAdapter.enable();
		}else{
			if (mBluetoothAdapter.isEnabled()){
				mBluetoothAdapter.disable();
			}
		}
	}
}