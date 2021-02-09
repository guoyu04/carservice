package com.metasequoia.manager.system.brightness;

import android.content.Context;
import android.util.Log;

import com.metasequoia.manager.listener.BaseListener;
import com.metasequoia.manager.system.base.model.Model;
import com.metasequoia.manager.system.setter.SettingsConfig;
import com.metasequoia.manager.system.state.BooleanState;
import com.metasequoia.manager.system.state.BrightnessMode;
import com.metasequoia.manager.utils.Utils;

/**
 * 亮度模式管理类
 * Created by guoyu on 2020/8/12.
 */
public class BrightnessModel extends BaseListener<Integer> implements IBrightnessModel, Model {
	public static final String TAG = "BrightnessModel";
	/**
	 * 白天模式下亮度设置
	 */
	private BrightnessDaySetterSender mDaySetter = null;
	/**
	 * 黑夜模式下亮度设置
	 */
	private BrightnessNightSetterSender mNightSetter = null;

	/**
	 * 白天/黑夜模式切换设置
	 */
	private BrightnessModeSetterSender mBrightnessModeSetter = null;

	/**
	 * 自动亮度模式设置
	 */
	private BrightnessAutoSetterSender mBrightnessAutoSetter = null;
	/**
	 * 当前白天/黑夜模式
	 */
	private BrightnessMode mBrightnessMode = BrightnessMode.DAYTIME;

	private Context mContext = null;

	/**
	 * 白天/黑夜状态模式监听
	 */
	private BaseListener<BrightnessMode> mModeListener = null;

	/**
	 * 白天/黑夜状态模式监听
	 */
	private BaseListener<BooleanState> mAutoBtListener = null;

	public BrightnessModel(Context context) {
		mContext = context.getApplicationContext();
	}

	@Override
	public void onCreate() {
		intMember();
		initListener();
	}

	@Override
	public void onDestroy() {
		destoryListener();
	}

	/**
	 * 初始化白天、黑夜及模式设置
	 */
	private void intMember() {
		//mBrightnessSetterSender = new BrightnessSetterSender(mContext);
		mBrightnessModeSetter = new BrightnessModeSetterSender(mContext);
		mBrightnessAutoSetter = new BrightnessAutoSetterSender(mContext);
		mDaySetter = new BrightnessDaySetterSender(mContext);
		mNightSetter = new BrightnessNightSetterSender(mContext);
		mModeListener = new BaseListener<BrightnessMode>();
		mAutoBtListener = new BaseListener<BooleanState>();
		updateBrightnessMode(mBrightnessModeSetter.getBrightnessMode());
		updateBrightnessAuto(mBrightnessAutoSetter.getBrightnessAuto());
	}

	/**
	 * 初始化状态监听
	 */
	private void initListener() {
		mBrightnessModeSetter.registerOnChangedListener(mModeObserverListener);
		mBrightnessAutoSetter.registerOnChangedListener(mAutoBtObserverListener);
		//mBrightnessSetterSender.registerOnChangedListener(mBrightnessObserverListener);
		mDaySetter.registerOnChangedListener(mBrightnessObserverListener);
		mNightSetter.registerOnChangedListener(mBrightnessObserverListener);
	}

	/**
	 * 销毁状态监听
	 */
	private void destoryListener() {
		mBrightnessModeSetter.unregisterOnChangedListener(mModeObserverListener);
		mBrightnessAutoSetter.unregisterOnChangedListener(mAutoBtObserverListener);
		//mBrightnessSetterSender.unregisterOnChangedListener(mBrightnessObserverListener);
		mDaySetter.unregisterOnChangedListener(mBrightnessObserverListener);
		mNightSetter.unregisterOnChangedListener(mBrightnessObserverListener);
	}

	/**
	 * 设置亮度值，如果当前是黑夜模式则设置到黑夜字段，如果当前是白天模式则设置到白天字段
	 * @param value 亮度值[0,100]
	 */
	@Override
	public void setBrightness(int value) {
		value = Utils.normalizeValue(value, 0, 100);
		value= value*SettingsConfig.SETTINGS_BRIGHTNESS_VALUE_MAX/100;
		if (mBrightnessMode == BrightnessMode.NIHGT) {
			mNightSetter.setBrightness(value);
		} else {
			mDaySetter.setBrightness(value);
		}
	}

	/**
	 * 格式化处理
	 * @param value
	 * @return
	 */
	private static int normalizeValue(int value){
		return value*100/SettingsConfig.SETTINGS_BRIGHTNESS_VALUE_MAX;
	}

	/**
	 * 获取亮度值，如果当前是黑夜模式则获取黑夜字段，如果当前是白天模式则获取白天字段
	 * @return 亮度值[0,100]
	 */
	@Override
	public int getBrightness() {
		int value = 0;
		if (mBrightnessMode == BrightnessMode.NIHGT) {
			value = mNightSetter.getBrightness(SettingsConfig.SETTINGS_BRIGHTNESS_NIGHT_DEFAULT);
		} else {
			value = mDaySetter.getBrightness(SettingsConfig.SETTINGS_BRIGHTNESS_DAY_DEFAULT);
		}
		return normalizeValue(value);
	}

	/**
	 * 白天/黑夜亮度状态监听
	 */
	private OnChangedListener<Integer> mBrightnessObserverListener = new OnChangedListener<Integer>() {
		@Override
		public void onChanged(Integer state) {
			dispatchOnChanged(normalizeValue(state));
		}
	};

	private void updateBrightnessMode(BrightnessMode state){
		Log.i(TAG, "updateBrightnessMode " + state);
		mBrightnessMode = state;
		mDaySetter.setBrightnessMode(mBrightnessMode);
		mNightSetter.setBrightnessMode(mBrightnessMode);
	}

	private void updateBrightnessAuto(BooleanState state){
		mDaySetter.setBrightnessAuto(state);
		mNightSetter.setBrightnessAuto(state);
	}
	/**
	 * 白天黑夜模式切换监听
	 */
	private OnChangedListener<BrightnessMode> mModeObserverListener = new OnChangedListener<BrightnessMode>() {
		@Override
		public void onChanged(BrightnessMode state) {
			if(state != mBrightnessMode && state != BrightnessMode.UNKOWN){
				updateBrightnessMode(state);
				//通知亮度改变
				dispatchOnChanged(getBrightness());
				//通知亮度模式改变
				mModeListener.dispatchOnChanged(state);
			}
		}

	};

	/**
	 * 自动亮度/手动亮度切换监听
	 */
	private OnChangedListener<BooleanState> mAutoBtObserverListener = new OnChangedListener<BooleanState>() {
		@Override
		public void onChanged(BooleanState state) {
			if(state != BooleanState.UNKOWN){
				updateBrightnessAuto(mBrightnessAutoSetter.getBrightnessAuto());
				mAutoBtListener.dispatchOnChanged(state);
			}
		}

	};


	/**
	 * 设置白天/黑夜模式切换
	 * @param mode 模式
	 */
	public void setBrightnessMode(BrightnessMode mode) {
		mBrightnessModeSetter.setBrightnessMode(mode);
	}

	/**
	 * 设置自动亮度
	 * @param isAuto
	 */
	public void setBrightnessAuto(BooleanState isAuto){
		mBrightnessAutoSetter.setBrightnessAudo(isAuto);
	}

	/**
	 * 获取是否是自动亮度模式
	 * @return
	 */
	public BooleanState getBrightnessAuto(){
		return mBrightnessAutoSetter.getBrightnessAuto();
	}
	/**
	 * 获取白天/黑夜模式
	 * @return mode
	 */
	public BrightnessMode getBrightnessMode() {
		if(mBrightnessMode == BrightnessMode.UNKOWN){
			updateBrightnessMode(mBrightnessModeSetter.getBrightnessMode());
		}
		Log.i(TAG, "getBrightnessMode " + mBrightnessMode);
		return mBrightnessMode;
	}

	/**
	 * 注册白天/黑夜模式切换监听
	 * @param listener 监听
	 */
	public void registerModeChangedListener(OnChangedListener<BrightnessMode> listener) {
		mModeListener.registerOnChangedListener(listener);
	}

	/**
	 * 注销白天/黑夜模式切换监听
	 * @param listener 监听
	 */
	public void unregisterModeChangedListener(OnChangedListener<BrightnessMode> listener) {
		mModeListener.unregisterOnChangedListener(listener);
	}


	/**
	 * 注册自动/手动模式切换监听
	 * @param listener 监听
	 */
	public void registerAutoBrightnessChangedListener(OnChangedListener<BooleanState> listener) {
        mAutoBtListener.registerOnChangedListener(listener);
	}

	/**
	 * 注销自动/手动模式切换监听
	 * @param listener 监听
	 */
	public void unregisterAutoBrightnessChangedListener(OnChangedListener<BooleanState> listener) {
		mAutoBtListener.unregisterOnChangedListener(listener);
	}

}
