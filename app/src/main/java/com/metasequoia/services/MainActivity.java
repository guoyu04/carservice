package com.metasequoia.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.metasequoia.services.core.CarService;
import com.metasequoia.services.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String LOG_TAG = "MainActivity";
    private CarService mCarService;// Main服务
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCarService = ((CarService.CarServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCarService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindSignService();
        findViewById(R.id.test_tv).setOnClickListener(this);
    }

    private void unbindSignService() {
        try {
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Service wasn't bound!");
        }
    }

    private void bindSignService() {
        Intent mServiceIntent = new Intent(this, CarService.class);
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindSignService();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test_tv:
                break;
        }
    }
}
