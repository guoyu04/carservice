package com.metasequoia.manager.system.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.metasequoia.manager.system.base.sender.BroadcastListener;

import java.util.List;


public class BluetoothBroadcastReciver extends BroadcastListener<IBluetoothStateCallBack> {

    public BluetoothBroadcastReciver(Context context, String action) {
        super(context, action);
    }

    @Override
    public void onProcessBroadcast(Intent intent) {
        List<IBluetoothStateCallBack> listeners = getProcessListeners();
        for (IBluetoothStateCallBack callBack : listeners) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothState.STATE_UNKNOWN.getValue());
            callBack.onBluetoothStateChange(BluetoothState.doTranslateValue(state));
        }
    }


}
