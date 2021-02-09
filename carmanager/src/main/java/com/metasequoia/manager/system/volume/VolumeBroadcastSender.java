package com.metasequoia.manager.system.volume;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.metasequoia.manager.listener.IBroadcastReceiverListener;
import com.metasequoia.manager.system.base.sender.BroadcastSender;

import java.util.List;

public class VolumeBroadcastSender extends BroadcastSender {

    public static final String ACTION = "pvetec.intent.action.show.volume.ui";

    public static final String EXTRA = "extra_volumeVisible";

    public VolumeBroadcastSender(Context context) {
        super(context, ACTION);
    }

    @Override
    public void onProcessBroadcast(Intent intent) {
        if (intent == null) return;
        List<IBroadcastReceiverListener> modelList = getProcessListeners();
        for (IBroadcastReceiverListener model : modelList) {
            model.onReceiverBroadcast(intent);
        }
    }

    private void doSend(int value) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA, value);
        send(bundle);
    }

    public void sendValue(int value){
        doSend(value);
    }
}
