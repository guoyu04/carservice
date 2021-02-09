package com.metasequoia.manager.system.base.sender;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.metasequoia.manager.listener.IBroadcastReceiverListener;

public abstract class BroadcastSender extends BroadcastListener<IBroadcastReceiverListener> implements ISender<Bundle> {

    private String mAction = "";

    private Context mContext = null;

    public BroadcastSender(Context context, String action) {
        super(context, action);
        mContext = context;
        mAction = action;
    }

    public void send(Bundle extras) {
        try {
            Intent intent = new Intent();
            intent.setAction(mAction);
            if (extras != null) intent.putExtras(extras);
            if (mContext != null) mContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
