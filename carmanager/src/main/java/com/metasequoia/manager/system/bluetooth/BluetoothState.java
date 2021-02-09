package com.metasequoia.manager.system.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.metasequoia.manager.system.state.IntState;


public enum BluetoothState implements IntState<BluetoothState> {
    STATE_UNKNOWN(0),
    STATE_OFF(BluetoothAdapter.STATE_OFF),
    STATE_TURNING_ON(BluetoothAdapter.STATE_TURNING_ON),
    STATE_ON(BluetoothAdapter.STATE_ON),
    STATE_TURNING_OFF(BluetoothAdapter.STATE_TURNING_OFF);

    private int mValue = -1;

    BluetoothState(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }

    @Override
    public BluetoothState translateValue(int value) {
        return doTranslateValue(value);
    }

    public static BluetoothState doTranslateValue(int value) {
        if (value == STATE_OFF.getValue()) return STATE_OFF;
        if (value == STATE_TURNING_ON.getValue()) return STATE_TURNING_ON;
        if (value == STATE_ON.getValue()) return STATE_ON;
        if (value == STATE_TURNING_OFF.getValue()) return STATE_TURNING_OFF;
        return STATE_UNKNOWN;
    }
}
