package com.metasequoia.services;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.metasequoia.services.core.CarService;

public class MyApplication extends Application {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d(LOG_TAG, "onCreate");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(), CarService.class.getName()));
        startService(intent);
    }
}
