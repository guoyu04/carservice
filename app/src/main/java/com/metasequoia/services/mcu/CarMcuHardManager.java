package com.metasequoia.services.mcu;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import com.metasequoia.manager.constent.Common;
import com.metasequoia.manager.mcu.MCUVersionInfo;

/**
 * MCU节点监听代理服务
 * 
 * @author guoyu
 * 
 */
public class CarMcuHardManager extends Binder implements ICarMcuHardInterface {
	public static final String TAG = "CarMcuBaseManager";
	public static final int MSG_REGISTER_PROXY = 1;
	public static final int MSG_NOTIFY_MCU_VER = 2;
	public static final int MSG_NOTIFY_MCU_UPDATE_STATE = 3;

	private static CarMcuHardManager sManager = null;
	private static IBinder sService = null;
	private static final String NATIVE_SERVICE_NAME = "metasequoia.mcu.CarMcuService";
	private static final String REMOTE_SERVICE_NAME = "metasequoia.mcu.ICarMcuManager";

	private Context mContext;

	protected static Object object = new Object();

	//acc 状态回调
	private CarMcuHardListener mCarMcuHardListener;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			try {
				dispatchMsg(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	});

	// 获得实例
	static public CarMcuHardManager getInstance() {
		if (sManager == null) {
			synchronized (CarMcuHardManager.class) {
				if (sManager == null) {
					sManager = new CarMcuHardManager();
				}
			}
		}
		return sManager;
	}

	private CarMcuHardManager() {
	}

	public void init(Context context) {
		mContext = context;
		registProxy();
	}

	public void destory(Context context) {
		unRegistProxy();
		Intent intent = new Intent();
		intent.setClass(context, CarMcuHardManager.class);
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
			case BIND_NOTIFY_MCU_VER: {
				Log.d(TAG, "rec notifition mcu version");
				data.readInt();
				data.readString();
				int arg1 = data.readInt();
				String soft = data.readString();
				String hard = data.readString();
				reply.writeInt(Common.RTN_SUCCESS);
				//发送
				Message msg = mHandler.obtainMessage(MSG_NOTIFY_MCU_VER);
				msg.obj = new MCUVersionInfo(soft, hard);
				mHandler.sendMessage(msg);
				return true;
			}
			case BIND_NOTIFY_MCU_UPDATE_STATE: {
				Log.d(TAG, "rec notifition mcu update status");
				data.readInt();
				data.readString();
				int status = data.readInt();
				data.readString();
				data.readString();
				reply.writeInt(Common.RTN_SUCCESS);
				//发送
				Message msg = mHandler.obtainMessage(MSG_NOTIFY_MCU_UPDATE_STATE);
				msg.arg1 = status;
				mHandler.sendMessage(msg);
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
	 * @param msg 消息
	 */
	private void dispatchMsg(Message msg){
		switch (msg.what) {
			case MSG_REGISTER_PROXY:
				registProxy();
				break;
			case MSG_NOTIFY_MCU_VER:
				notificationMcuVersion((MCUVersionInfo)msg.obj);
				break;
			case MSG_NOTIFY_MCU_UPDATE_STATE:
				notificationMcuUpdateState(msg.arg1);
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
			getService().transact(BIND_REGISTER_MCU, _data, _reply, FLAG_ONEWAY);
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
			getService().transact(BIND_UNREGISTER_MCU, _data, _reply, FLAG_ONEWAY);
		} catch (RemoteException e) {
			Log.e(TAG, " unregistProxy regist failed!!!");
			e.printStackTrace();
		}
		_reply.recycle();
		_data.recycle();
	}

	/**
	 * 处理接收到版本信息
	 * @param versionInfo 版本信息
	 */
	private void notificationMcuVersion(MCUVersionInfo versionInfo){
		Log.d(TAG, "notificationMcuVersion soft:"+versionInfo.softVersion+"; hard:"+versionInfo.hardVersion);
		if(mCarMcuHardListener != null){
			mCarMcuHardListener.onMcuVersionInfo(versionInfo);
		}
	}

	/**
	 * 处理接收到更新状态
	 * @param state 成功(Common.RTN_SUCCESS) /  失败(Common.RTN_FAIL)
	 */
	private void notificationMcuUpdateState(int state){
		Log.d(TAG, "notificationMcuUpdateState state:"+state);
		if(mCarMcuHardListener != null){
			mCarMcuHardListener.onMcuUpdateState(state);
		}
	}


	/**
	 * 请求MCU版本信息，异步回调形式返回
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int requestMcuVersion() {
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		try {
			getService().transact(BIND_REQ_MCU_VER, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, "request mcu version to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " requestMcuVersion return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}

	/**
	 * 请求mcu更新
	 * @param binPath mcu文件路径
	 * @return Common.RTN_SUCCESS 成功 Common.RTN_FAIL失败
	 */
	public int requestMcuUpdate(String binPath) {
		int ret = Common.RTN_SUCCESS;
		Parcel _data = Parcel.obtain();
		Parcel _reply = Parcel.obtain();
		if (getService() == null) {
			Log.e(TAG, " the regist proxy is null!!!");
			return Common.RTN_FAIL;
		}
		_data.writeInterfaceToken(NATIVE_SERVICE_NAME);
		_data.writeString(binPath);
		try {
			getService().transact(BIND_REQ_MCU_UPDATE, _data, _reply, FLAG_ONEWAY);
			ret = _reply.readInt();
		} catch (Exception e) {
			Log.e(TAG, "request mcu update to service failed!!!");
			e.printStackTrace();
			ret = Common.RTN_FAIL;
		}
		Log.d(TAG, " requestMcuUpdate return["+ret+"]");
		_reply.recycle();
		_data.recycle();
		return ret;
	}


	/**
	 * 延迟向服务端注册
	 */
	private void sendRegisterProxyMessage(){
		mHandler.sendEmptyMessageDelayed(MSG_REGISTER_PROXY, 2000);
	}


	public void setCarMcuHardListener(CarMcuHardListener listener){
		mCarMcuHardListener = listener;
	}

	public interface CarMcuHardListener {
		/**
		 * MCU acc 状态信号
		 * @param versionInfo 版本信息
		 */
		void onMcuVersionInfo(MCUVersionInfo versionInfo);

		/**
		 * MCU 更新状态
		 * @param state 成功(Common.RTN_SUCCESS) /  失败(Common.RTN_FAIL)
		 */
		void onMcuUpdateState(int state);
	}
}
