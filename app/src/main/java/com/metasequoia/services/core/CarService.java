package com.metasequoia.services.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;
import com.metasequoia.constent.ActionConstent;

public class CarService extends Service  {
	public static final String LOG_TAG = "CarService";
	private IBinder mBinder = new CarServiceBinder();

	private Handler mHandler;
	private HandlerThread mHandlerThread;

	private CarServiceImpl mCarServiceImpl;

	@Override
	public void onCreate() {
		super.onCreate();
		
		mHandlerThread = new HandlerThread("CarServiceInit", 5);
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		initCarService();
		/*mHandler.post(new Runnable() {
			@Override
			public void run() {
				initCarService();
			}
		});*/
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getAction() != null) {
			if (TextUtils.equals(intent.getAction(), ActionConstent.D_ACTION_ACC_ON)
					|| TextUtils.equals(intent.getAction(), ActionConstent.C_ACTION_ACC_ON)
					|| TextUtils.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {

			}
		}
		return START_STICKY;
	}

	private void initCarService() {
		final Context context = getApplicationContext();
		mCarServiceImpl = new CarServiceImpl();
		mCarServiceImpl.initCarService(context);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class CarServiceBinder extends Binder {
		public CarService getService() {
			return CarService.this;
		}
	}
}
