package com.metasequoia.manager.system.base.sender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

abstract public class IBroadcast {

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent == null || intent.getAction() == null)
				return;
			onReceiverListener(intent);
		};
	};
	Context mContext = null;
	
	private Handler mHandler=null;
	
	public IBroadcast(Context context) {
		mContext = context;
	}

	public void create() {
		if (mContext == null)
			return;
		IntentFilter filter = getIntentFilter();
		if(filter==null ) return;
        try {
            mHandler=getScheduler();
            mContext.registerReceiver(mBroadcastReceiver, filter, null, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

	public void destroy() {
		if (mContext == null) return;
		try {
			mContext.unregisterReceiver(mBroadcastReceiver);
			mHandler.getLooper().quit();
			mHandler=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	abstract public IntentFilter getIntentFilter();

	abstract public int onReceiverListener(Intent eventArgs);
	
    public Handler getScheduler() {
        return null;
    }

}
