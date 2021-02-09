package com.metasequoia.manager.system.base.sender;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

public abstract class BroadcastListener<Listener>{

    private String mAction = "";

    private Context mContext = null;

    private Broadcast mBroadcast = null;

    private List<Listener> mProcessListener = new ArrayList<Listener>();

    public BroadcastListener(Context context, String action) {
        mContext = context;
        mAction = action;
    }

    private void registerBroadcastReceiver() {
        if (mBroadcast != null) return;
        synchronized (Broadcast.class) {
            if (mBroadcast == null) {
                mBroadcast = new Broadcast(mContext.getApplicationContext());
                mBroadcast.create();
            }
        }
    }

    private void unregisterBroadcastReceiver() {
        synchronized (Broadcast.class) {
            if (mBroadcast != null) {
                mBroadcast.destroy();
                mBroadcast = null;
            }
        }
    }

    abstract public void onProcessBroadcast(Intent intent);

    protected List<Listener> getProcessListeners() {
        return mProcessListener;
    }

    public void registerBroadcastListener(Listener listener) {
        mProcessListener.remove(listener);
        mProcessListener.add(listener);
        registerBroadcastReceiver();
    }

    public void unregisterBroadcastListener(Listener listener) {
        mProcessListener.remove(listener);
        if (mProcessListener.isEmpty()) unregisterBroadcastReceiver();
    }

    private class Broadcast extends IBroadcast {
        public Broadcast(Context context) {
            super(context);
        }

        public IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(mAction);
            return intentFilter;
        }

        @Override
        public int onReceiverListener(Intent eventArgs) {
            if (eventArgs == null) return -1;
            onProcessBroadcast(eventArgs);
            return 0;
        };
    };

}
