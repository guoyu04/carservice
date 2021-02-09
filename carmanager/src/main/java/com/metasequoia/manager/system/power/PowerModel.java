package com.metasequoia.manager.system.power;

import android.content.Context;
import android.os.PowerManager;

import com.metasequoia.manager.listener.BaseListener;
import com.metasequoia.manager.system.base.model.Model;
import com.metasequoia.manager.system.state.BooleanState;

/**
 * 电源管理类
 * Created by guoyu on 2020/8/12.
 */
public class PowerModel extends BaseListener<Integer> implements IPowerModel, Model {

	/**
	 * 关闭屏幕
	 */

	private ScreenOffSender mScreenOffSender = null;
	/**
	 * ACC 状态
	 */
	private AccSender mAccSender = null;
	/**
	 * 关闭电源
	 */
	private PowerManager mPowerManager = null;

	private Context mContext = null;

	/**
	 * ACC模式监听
	 */
	private BaseListener<BooleanState> mAccStateListener = null;

	/**
	 * ACC切换监听
	 */
	private OnChangedListener<BooleanState> mAccObserverListener = new OnChangedListener<BooleanState>() {
		@Override
		public void onChanged(BooleanState state) {
			if(state != BooleanState.UNKOWN){
				mAccStateListener.dispatchOnChanged(state);
			}
		}

	};

	public PowerModel(Context context) {
		mContext = context.getApplicationContext();
	}

	@Override
	public void onCreate() {
		mScreenOffSender = new ScreenOffSender(mContext);
		mAccSender = new AccSender(mContext);
		mAccSender.registerOnChangedListener(mAccObserverListener);
		mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
		mAccStateListener = new BaseListener<BooleanState>();
	}

	@Override
	public void onDestroy() {
		mAccSender.unregisterOnChangedListener(mAccObserverListener);
	}

	/**
	 * 关闭屏幕
	 */
	@Override
	public void setScreenOff(){
		mScreenOffSender.setScreenOff();
	}

	/**
	 * 获取ACC状态，仅F系列支持
	 * @return
	 */
	@Override
	public BooleanState getAccState(){
		return mAccSender.getAccState();
	}

	/**
	 * 设备重启
	 */
	@Override
	public void setPowerReboot(){
		try {
			mPowerManager.reboot("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 注册ACC切换监听
	 * @param listener 监听
	 */
	public void registerAccChangedListener(OnChangedListener<BooleanState> listener) {
		mAccStateListener.registerOnChangedListener(listener);
	}

	/**
	 * 注销Acc切换监听
	 * @param listener 监听
	 */
	public void unregisterAccChangedListener(OnChangedListener<BooleanState> listener) {
		mAccStateListener.unregisterOnChangedListener(listener);
	}
}
