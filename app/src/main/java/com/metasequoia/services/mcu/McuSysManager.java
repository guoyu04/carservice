package com.metasequoia.services.mcu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.metasequoia.manager.constent.Common;

/**
 * 系统状态监听代理服务
 * 
 * @author guoyu
 * 
 */
public class McuSysManager extends Binder implements ICustomCarSysInterface {
	public static final String TAG = "CustomCarSysManager";
	public static final int MSG_REGISTER_PROXY = 1;
	public static final int MSG_NOTIFY_ACC_STATE = 2;
	public static final int MSG_NOTIFY_BAT_POWER = 3;
	public static final int MSG_NOTIFY_BAT_STATE = 4;
	public static final int MSG_NOTIFY_CAR_GEAR = 6;

	public static final int MSG_NOTIFY_CAR_SPEED = 7;//车速
	public static final int MSG_NOTIFY_ROTATE_SPEED= 8;//转速
	public static final int MSG_NOTIFY_TOTAL_MIL= 9;//总里程
	public static final int MSG_NOTIFY_REM_MIL= 10;//剩余里程
	public static final int MSG_NOTIFY_WIFI_ACCOUNT= 11;//wifi账号
	public static final int MSG_NOTIFY_WIFI_PWD= 12;//wifi密码
	public static final int MSG_NOTIFY_KEY_EVENT= 13;//按键事件

	public static final int MSG_NOTIFY_DEV_ID= 14;//DEV id

	public static final int MSG_NOTIFY_BAT_VOLTAGE = 15;//小电瓶电压
	private static McuSysManager sManager = null;
	private static IBinder sService = null;
	private static final String NATIVE_SERVICE_NAME = "metasequoia.mcu.CarSystemService";
	private static final String REMOTE_SERVICE_NAME = "metasequoia.mcu.ICarSystemManager";

	private Context mContext;
	/**
	 * 对象锁
	 */
	protected static Object object = new Object();

	private SystemStateReceiver mSystemStateReceiver;

	//acc 状态回调
	private CustomCarSysPowerListener mCustomCarSysPowerListener;

	private CustomCarSysRunningListener mCustomCarSysRunningListener;

	private Handler mHandler;
	private HandlerThread mHandlerThread;

	// 获得实例
	static public McuSysManager getInstance() {
		if (sManager == null) {
			synchronized (McuSysManager.class) {
				if (sManager == null) {
					sManager = new McuSysManager();
				}
			}
		}
		return sManager;
	}

	private McuSysManager() {
	}

	public void init(Context context) {
		mContext = context;
		mHandlerThread = new HandlerThread("McuSysManager", 1);
		mHandlerThread.start();
		mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
			@Override
			public boolean handleMessage(Message message) {
				dispatchMsg(message.what,message.arg1,message.arg2, message.obj);
				return false;
			}
		});
		registProxy();
	}

	public void destory(Context context) {
		unRegistProxy();
		Intent intent = new Intent();
		intent.setClass(context, McuSysManager.class);
		context.stopService(intent);
	}

	private IBinder getService() {
		if (sService != null) {
			return sService;
		}
		sService = ServiceManager.getService(NATIVE_SERVICE_NAME);
		if (sService != null) {
			try {
				sService.linkToDeath(new DeathRecipient() {
					@Override
					public void binderDied() {
						sService = null;
						Log.e(TAG,"the native service is died!");
						sendRegisterProxyMessage();
					}
				}, 0);
				attachInterface(this, REMOTE_SERVICE_NAME);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sService;
	}

	@Override
	public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
		Log.d(TAG, "onTransact code = " + code);
		try {
			switch (code) {
			case BIND_NOTIFY_ACC_STATUS: {
				Log.d(TAG, "rec notifition device[acc sign message] ");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_ACC_STATE, status);
				return true;
			}
			case BIND_NOTIFY_BAT_POWER: {
				Log.d(TAG, "rec notifition device [battery power change message]");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_BAT_POWER, status);
				return true;
			}
			case BIND_NOTIFY_BAT_STATE: {
				Log.d(TAG, "rec notifition device [battery state message] ");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_BAT_STATE, status);
				return true;
			}
			case BIND_NOTIFY_CAR_GEAR: {
				Log.d(TAG, "rec notifition device [car gear message] ");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_CAR_GEAR, status);
				return true;
			}
			case BIND_NOTIFY_CAR_SPEED: {
				Log.d(TAG, "rec notifition device [car speed message] ");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_CAR_SPEED, status);
				return true;
			}
			case BIND_NOTIFY_ROTATE_SPEED: {
				Log.d(TAG, "rec notifition device [rotate speed message] ");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_ROTATE_SPEED, status);
				return true;
			}
			case BIND_NOTIFY_TOTAL_MIL: {
				Log.d(TAG, "rec notifition device [total milleage message] ");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_TOTAL_MIL, status);
				return true;
			}
			case BIND_NOTIFY_REM_MIL: {
				Log.d(TAG, "rec notifition device [rem milleage sign message] ");
				data.readInt();
				data.readString();
				int status = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_REM_MIL, status);
				return true;
			}
			case BIND_NOTIFY_WIFI_ACCOUNT: {
				Log.d(TAG, "rec notifition device [wifi account message] ");
				data.readInt();
				data.readString();
				data.readInt();
				String account = data.readString();
				reply.writeInt(Common.RTN_SUCCESS);
				sendDataMessage(MSG_NOTIFY_WIFI_ACCOUNT, account);
				return true;
			}
			case BIND_NOTIFY_WIFI_PWD: {
				Log.d(TAG, "rec notifition device [wifi password message] ");
				data.readInt();
				data.readString();
				data.readInt();
				String pwd = data.readString();
				reply.writeInt(Common.RTN_SUCCESS);
				sendDataMessage(MSG_NOTIFY_WIFI_PWD, pwd);
				return true;
			}
			case BIND_NOTIFY_DEV_ID: {
					Log.d(TAG, "rec notifition device [wifi password message] ");
					data.readInt();
					data.readString();
					data.readInt();
					String devID = data.readString();
					reply.writeInt(Common.RTN_SUCCESS);
					sendDataMessage(MSG_NOTIFY_DEV_ID, devID);
					return true;
				}
			case BIND_NOTIFY_BAT_VOLTAGE: {
					Log.d(TAG, "rec notifition device [car battery voltage] ");
					data.readInt();
					data.readString();
					int voltage = data.readInt();
					reply.writeInt(Common.RTN_SUCCESS);
					sendStateMessage(MSG_NOTIFY_BAT_VOLTAGE, voltage);
					return true;
				}
			case BIND_NOTIFY_KEY_EVENT: {
				Log.d(TAG, "rec notifition device [key event message] ");
				data.readInt();
				data.readString();
				int key = data.readInt();
				int value = data.readInt();
				reply.writeInt(Common.RTN_SUCCESS);
				sendStateMessage(MSG_NOTIFY_KEY_EVENT, key, value);
				return true;
			}
			default:
				Log.d(TAG, "unknow code!!!");
				break;
			}
		} catch (Exception e) {
			Log.e(TAG,"onTransact  Exception "+ e.toString());

		}
		return false;
	}

	@Override
	public IBinder asBinder() {
		return this;
	}

	/**
	 * 处理消息
	 * @param what 消息what
	 * @param arg1 消息obj
	 */
	private void dispatchMsg(int what, int arg1, int arg2, Object obj){
		switch (what) {
			case MSG_REGISTER_PROXY:
				registProxy();
				break;
			case MSG_NOTIFY_ACC_STATE:
				notificationAccState(arg1);
				break;
			case MSG_NOTIFY_BAT_POWER:
				notificationBatPower(arg1);
				break;
			case MSG_NOTIFY_BAT_STATE:
				notificationBatState(arg1);
				break;
			case MSG_NOTIFY_CAR_GEAR:
				notificationCarGear(arg1);
				break;
			case MSG_NOTIFY_CAR_SPEED:
				notificationCarSpeed(arg1);
				break;
			case MSG_NOTIFY_ROTATE_SPEED:
				notificationRotateSpeed(arg1);
				break;
			case MSG_NOTIFY_TOTAL_MIL:
				notificationTotalMileage(arg1);
				break;
			case MSG_NOTIFY_REM_MIL:
				notificationRemMileage(arg1);
				break;
			case MSG_NOTIFY_WIFI_ACCOUNT:
				notificationWifiAccount((String)obj);
				break;
			case MSG_NOTIFY_WIFI_PWD:
				notificationWifiPwd((String)obj);
				break;
			case MSG_NOTIFY_DEV_ID:
				notificationDevID((String)obj);
				break;
			case MSG_NOTIFY_BAT_VOLTAGE:
				notificationBatVoltage(arg1);
				break;
			case MSG_NOTIFY_KEY_EVENT:
				notificationKeyEvent(arg1,arg2);
				break;
		}
	}

	/**
	 * 注册服务到远程
	 * 
	 */
	private int registProxy() {
		int ret = Common.RTN_FAIL;

		attachInterface(this, REMOTE_SERVICE_NAME);
		Log.d(TAG, "registProxy!!!");
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			sendRegisterProxyMessage();
			return ret;
		}
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		_data.writeStrongBinder(this);
		_data.writeString(mContext.getPackageName());
		try {
			getService().transact(BIND_REGISTER_SYS, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (RemoteException e) {
			Log.e(TAG, "registProxy regist failed!!!");
			e.printStackTrace();
		}
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 注册服务到远程
	 * 
	 */
	private void unRegistProxy() {
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.d(TAG, " the unregist proxy is null!!!");
			return;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		_data.writeStrongBinder(this);
		try {
			getService().transact(BIND_UNREGISTER_SYS, _data, _reply, FLAG_ONEWAY);
		} catch (RemoteException e) {
			Log.e(TAG, " unregistProxy regist failed!!!");
			e.printStackTrace();
		}
		_reply.recycle();
		_data.recycle();
	}

	/**
	 * 处理接收到acc状态
	 * @param accState acc状态 Common.DEV_POWER_ON acc off /Common.DEV_POWER_OFF acc on
	 */
	private void notificationAccState(int accState){
		Log.d(TAG, "notificationAccState state:"+accState);
		if(mCustomCarSysPowerListener != null){
			mCustomCarSysPowerListener.onMcuAccChange(accState);
		}
	}

	/**
	 * mcu电池电量
	 * @param battery  电量
	 */
	private void notificationBatPower(int battery){
		Log.d(TAG, "notificationBatPower battery:"+battery*0.4f);
		if(mCustomCarSysPowerListener != null){
			mCustomCarSysPowerListener.onMcuBatteryPower(battery*0.4f);
		}
	}

	/**
	 * mcu电池状态
	 * @param state Common.DEV_POWER_ON acc off /Common.DEV_POWER_OFF acc on
	 */
	private void notificationBatState(int state){
		Log.d(TAG, "notificationBatState state:"+state);
		if(mCustomCarSysPowerListener != null){
			mCustomCarSysPowerListener.onMcuBatteryState(state);
		}
	}

	/**
	 * mcu总里程
	 * @param mileage 总里程
	 */
	private void notificationTotalMileage(int mileage){
		Log.d(TAG, "notificationTotalMileage mileage:"+mileage);
		if(mCustomCarSysPowerListener != null){
			mCustomCarSysPowerListener.onMcuTotalMileage(mileage);
		}
	}

	/**
	 * mcu剩余里程
	 * @param mileage 剩余里程
	 */
	private void notificationRemMileage(int mileage){
		Log.d(TAG, "notificationRemMileage mileage:"+mileage);
		if(mCustomCarSysPowerListener != null){
			mCustomCarSysPowerListener.onMcuRemMileage(mileage);
		}
	}

	/**
	 * mcu电瓶电压
	 * @param voltage 电瓶电压
	 */
	private void notificationBatVoltage(int voltage){
		Log.d(TAG, "notificationBatVoltage voltage:"+voltage);
		if(mCustomCarSysPowerListener != null){
			mCustomCarSysPowerListener.onMcuBatVoltage(voltage*0.01f);
		}
	}
	/**
	 * mcu档位状态
	 * @param gear mcu档位
	 */
	private void notificationCarGear(int gear){
		Log.d(TAG, "notificationCarGear gear:"+gear);
		if(mCustomCarSysRunningListener != null){
			mCustomCarSysRunningListener.onMcuCarGear(gear);
		}
	}

	/**
	 * mcu车速
	 * @param speed 车速
	 */
	private void notificationCarSpeed(int speed){
		Log.d(TAG, "notificationCarSpeed speed:"+speed);
		if(mCustomCarSysRunningListener != null){
			mCustomCarSysRunningListener.onMcuCarSpeed(speed/10.0f);
		}
	}
	/**
	 * mcu转速
	 * @param speed 转速
	 */
	private void notificationRotateSpeed(int speed){
		Log.d(TAG, "notificationRotateSpeed speed:"+speed);
		if(mCustomCarSysRunningListener != null){
			mCustomCarSysRunningListener.onMcuRotateSpeed(speed);
		}
	}

	/**
	 * wifi账号
	 * @param account wifi账号
	 */
	private void notificationWifiAccount(String account){
		Log.d(TAG, "notificationWifiAccount account:"+account);
		if(mCustomCarSysRunningListener != null) {
			mCustomCarSysRunningListener.onWifiAccountChanged(account);
		}
	}

	/**
	 * wifi密码
	 * @param pwd wifi密码
	 */
	private void notificationWifiPwd(String pwd){
		Log.d(TAG, "notificationWifiPwd pwd:"+pwd);
		if(mCustomCarSysRunningListener != null) {
			mCustomCarSysRunningListener.onWifiPwdChanged(pwd);
		}
	}

	/**
	 * device id
	 * @param devID id
	 */
	private void notificationDevID(String devID){
		Log.d(TAG, "notificationDevID pwd:"+devID);
		if(mCustomCarSysRunningListener != null) {
			mCustomCarSysRunningListener.onWifiPwdChanged(devID);
		}
	}
	/**
	 * key事件
	 */
	private void notificationKeyEvent(int key, int state){
		Log.d(TAG, "notificationKeyEvent key:"+key + ";state"+state);
		if(mCustomCarSysRunningListener != null) {
			mCustomCarSysRunningListener.onMcuKeyEvent(key, state);
		}
	}

	/**
	 * 发送语音
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int sendSpeak(String data) {
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			byte [] dataByte = data.getBytes("GBK");
			_data.writeInt(dataByte.length);
			for(int i=0; i< dataByte.length; i ++){
				_data.writeInt(dataByte[i]);
			}
			_data.writeString(data);
			getService().transact(BIND_SEND_SPEAKER, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set system sleep to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " sendSpeak return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 设置当前前台应用
	 * @param pckName 前台应用包名
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setTopPackage(String pckName) {
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeString(pckName);
			getService().transact(BIND_SET_TOP_PACKAGE, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set system boot complete to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setSystemReady return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 设置导航打开状态
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setNavOpenState(int state) {
		Log.d(TAG, "set navigation state");
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeInt(state);
			getService().transact(BIND_SET_NAV_STATE, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set navigation state to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setNavOpenState return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 设置蓝牙打开状态
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setBleOpenState(int state) {
		Log.d(TAG, "set bluetooth state");
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeInt(state);
			getService().transact(BIND_SET_BLE_STATE, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set bluetooth state to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setBleOpenState return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}
	/**
	 * 设置FM打开状态
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setFMOpenState(int state) {
		Log.d(TAG, "setFMOpenState");
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeInt(state);
			getService().transact(BIND_SET_FM_STATE, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set system brightness to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setFMOpenState return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 设置人脸认证状态
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setFaceState(int state) {
		Log.d(TAG, "setFaceState");
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeInt(state);
			getService().transact(BIND_SET_FACE_STATE, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set face state to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setFaceState return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 设置评分等级
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setGrading(int state) {
		Log.d(TAG, "setGrading");
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeInt(state);
			getService().transact(BIND_SET_GRADING, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set grading to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setGrading return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 目的地周边优惠信息
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setDestPerInfo(int state) {
		Log.d(TAG, "setDestPerInfo");
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeInt(state);
			getService().transact(BIND_SET_DEST_PER_INFO, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set dest per info to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setDestPerInfo return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 订单信息
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int setOrderInfo(int state) {
		Log.d(TAG, "setOrderInfo");
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			_data.writeInt(state);
			getService().transact(BIND_SET_ORDER_INFO, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set order info to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " setOrderInfo return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}
	/**
	 * 获取车辆状态
	 * @return 车辆状态
	 */
	public int getAccState(){
		int status = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_ACC_STATUS, _data, _reply, 0);
			status = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set system brightness to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getAccState status["+status+"]");
		_reply.recycle();
		_data.recycle();
		return status;
	}
	/**
	 * 获取剩余电量
	 * @return 剩余电量
	 */
	public float getBatteryPower(){
		Log.d(TAG, "getBatteryPower");
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		float battery = 0;
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_BAT_POWER, _data, _reply, 0);
			battery = _reply.readInt()*0.4f;
		} catch (Exception e) {
			Log.e(TAG, " set battery power to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getBatteryPower battery["+battery+"]");
		_reply.recycle();
		_data.recycle();
		return battery;
	}
	/**
	 * 获取电池状态
	 * @return 电池状态
	 */
	public int getBatteryState(){
		Log.d(TAG, "getBatteryState");
		int state = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_BAT_STATE, _data, _reply, 0);
			state = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set battery state to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getBatteryState state["+state+"]");
		_reply.recycle();
		_data.recycle();
		return state;
	}
	/**
	 * 获取档位状态
	 * @return 位状态
	 */
	public int getCarGear() {
		Log.d(TAG, "get car gear");
		int gear = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_CAR_GEAR, _data, _reply, 0);
			gear = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " get car gear to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getCarGear return["+gear+"]");
		_reply.recycle();
		_data.recycle();
		return gear;
	}
	/**
	 * 获取车速
	 * @return 车速
	 */
	public float getCarSpeed(){
		float speed = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_CAR_SPEED, _data, _reply, 0);
			speed = _reply.readInt()/10.0f;
		} catch (Exception e) {
			Log.e(TAG, " getCarSpeed to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getCarSpeed return["+speed+"]");
		_reply.recycle();
		_data.recycle();
		return speed;
	}
	/**
	 * 获取转速
	 * @return 转速
	 */
	public int getRotateSpeed(){
		int speed = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_ROTATE_SPEED, _data, _reply, 0);
			speed = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " getRotateSpeed to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getRotateSpeed speed["+speed+"]");
		_reply.recycle();
		_data.recycle();
		return speed;
	}
	/**
	 * 获取总里程
	 * @return 总里程
	 */
	public int getTotalMileage(){
		int mileage = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_TOTAL_MIL, _data, _reply, 0);
			mileage = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set system brightness to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getTotalMileage mileage["+mileage+"]");
		_reply.recycle();
		_data.recycle();
		return mileage;
	}
	/**
	 * 获取剩余里程
	 * @return 剩余里程
	 */
	public int getRemMileage() {
		int mileage = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_REM_MIL, _data, _reply, 0);
			mileage = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, " set system brightness to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getRemMileage return["+mileage+"]");
		_reply.recycle();
		_data.recycle();
		return mileage;
	}
	/**
	 * 获取wifi账号
	 * @return wifi账号
	 */
	public String getWifiAccount(){
		String account = "";
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return "";
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_WIFI_ACCOUNT, _data, _reply, 0);
			account = _reply.readString();
		} catch (Exception e) {
			Log.e(TAG, " getWifiAccount to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getWifiAccount return["+account+"]");
		_reply.recycle();
		_data.recycle();
		return account;
	}
	/**
	 * 获取wifi密码
	 * @return wifi密码
	 */
	public String getWifiPwd(){
		String pwd = "";
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return "";
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_WIFI_PWD, _data, _reply, 0);
			pwd = _reply.readString();
		} catch (Exception e) {
			Log.e(TAG, " getWifiPwd to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getWifiPwd return["+pwd+"]");
		_reply.recycle();
		_data.recycle();
		return pwd;
	}

	/**
	 * 获取device id
	 * @return id
	 */
	public String getDeviceID(){
		String devID = "";
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return "";
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_DEV_ID, _data, _reply, 0);
			devID = _reply.readString();
		} catch (Exception e) {
			Log.e(TAG, " getDeviceID to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getDeviceID return["+devID+"]");
		_reply.recycle();
		_data.recycle();
		return devID;
	}

	/**
	 * 获取电瓶电压
	 * @return 电瓶电压
	 */
	public float getBatVoltage(){
		float voltage = 0;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_GET_BAT_VOLTAGE, _data, _reply, 0);
			voltage = _reply.readInt()*0.01f;
		} catch (Exception e) {
			Log.e(TAG, " getBatVoltage to service failed!!!");
			e.printStackTrace();
		}
		Log.d(TAG, " getBatVoltage return["+voltage+"]");
		_reply.recycle();
		_data.recycle();
		return voltage;
	}
	/**
	 * 延迟向服务端注册
	 */
	private void sendRegisterProxyMessage(){
		mHandler.sendEmptyMessageDelayed(MSG_REGISTER_PROXY, 2000);
	}

	/**
	 * 发送状态变更消息
	 */
	private void sendStateMessage(int msgCode, int status){
		Message msg = mHandler.obtainMessage(msgCode);
		msg.arg1 = status;
		mHandler.sendMessage(msg);
	}

	/**
	 * 发送状态变更消息
	 */
	private void sendStateMessage(int msgCode, int arg1, int status){
		Message msg = mHandler.obtainMessage(msgCode);
		msg.arg1 = arg1;
		msg.arg2 = status;
		mHandler.sendMessage(msg);
	}
	/**
	 * 发送状态变更消息
	 */
	private void sendDataMessage(int msgCode, String data){
		Message msg = mHandler.obtainMessage(msgCode);
		msg.obj = data;
		mHandler.sendMessage(msg);
	}
	public void setSysPowerListener(CustomCarSysPowerListener listener){
		mCustomCarSysPowerListener = listener;
	}

	public void setSysRunningListener(CustomCarSysRunningListener listener){
		mCustomCarSysRunningListener = listener;
	}
	public interface CustomCarSysRunningListener {
		/**
		 * mcu档位状态
		 * @param gear mcu档位
		 */
		void onMcuCarGear(int gear);

		/**
		 * mcu车速
		 * @param speed 车速
		 */
		void onMcuCarSpeed(float speed);
		/**
		 * mcu转速
		 * @param speed 转速
		 */
		void onMcuRotateSpeed(int speed);

		/**
		 * wifi 帐号
		 * @param account 帐号
		 */
		void onWifiAccountChanged(String account);

		/**
		 * wifi 密码
		 * @param pwd 密码
		 */
		void onWifiPwdChanged(String pwd);

		/**
		 * key事件
		 * @param key 按键
		 */
		void onMcuKeyEvent(int key, int state);
	}
	public interface CustomCarSysPowerListener {
		/**
		 * MCU acc 状态信号
		 * @param state  acc on(Common.DEV_POWER_ON) /  acc off(Common.DEV_POWER_OFF)
		 */
		void onMcuAccChange(int state);

		/**
		 * mcu电池电量
		 * @param battery
		 */
		void onMcuBatteryPower(float battery);

		/**
		 * mcu电池状态
		 * @param state
		 */
		void onMcuBatteryState(int state);


		/**
		 * mcu总里程
		 * @param mileage 总里程
		 */
		void onMcuTotalMileage(int mileage);

		/**
		 * mcu剩余里程
		 * @param mileage 剩余里程
		 */
		void onMcuRemMileage(int mileage);

		/**
		 * device id
		 * @param devID id
		 */
		void onDeviceIDChanged(String devID);

		/**
		 * mcu电瓶电压
		 * @param voltage 电瓶电压
		 */
		void onMcuBatVoltage(float voltage);
	}

	//----------------------------------------------------------------------------------------------

	class SystemStateReceiver extends BroadcastReceiver {
        @Override
         public void onReceive(Context context, Intent intent) {
			if (null == intent || null == intent.getAction()) {
				return;
			}
			Log.i(TAG, "intent action="+intent.getAction());

		}
    }
}
