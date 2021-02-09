package com.metasequoia.manager.system;

import android.content.Context;
import android.util.Log;

import com.metasequoia.manager.constent.SystemConfig;
import com.metasequoia.manager.system.base.model.Model;
import com.metasequoia.manager.system.bluetooth.BluetoothModel;
import com.metasequoia.manager.system.brightness.BrightnessModel;
import com.metasequoia.manager.system.device.DeviceModel;
import com.metasequoia.manager.system.power.PowerModel;
import com.metasequoia.manager.system.volume.VolumeModel;
import com.metasequoia.manager.system.wifi.WifiModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 车辆系统设置管理类
 * Created by guoyu on 2020/8/12.
 */
public class CarSettingManager {

    /**
     * 亮度设置key
     */
    public static final String BRIGHTNESS_MODEL = "brightnessModel";

    /**
     * 音量设置key
     */
    public static final String VOLUME_MODEL = "volumeModel";

    /**
     * wifi设置key
     */
    public static final String WIFI_MODEL = "wifiModel";

    /**
     * bluetooth设置key
     */
    public static final String BLUETOOTH_MODEL = "bluetoothModel";

    /**
     * power设置key
     */
    public static final String POWER_MODEL = "powerModel";

    /**
     * device设置key
     */
    public static final String DEVICE_MODEL = "deviceModel";

    private Context mContext;

    private static CarSettingManager sCarSettingManager = null;

    /**
     * 模块管理map
     */
    private Map<String, ModelFetcher> mModelMap = new HashMap<String, ModelFetcher>();

    private CarSettingManager(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 根据模块key获取模块实例
     * @param modelName
     * @return
     */
    public Model getModel(String modelName) {
        ModelFetcher fetcher = mModelMap.get(modelName);
        if (fetcher == null) return null;
        return fetcher.getModel(mContext);
    }

    public static CarSettingManager getInstance(Context context) {
        if (sCarSettingManager != null) return sCarSettingManager;
        synchronized (CarSettingManager.class) {
            if (sCarSettingManager == null) {
                sCarSettingManager = new CarSettingManager(context);
                sCarSettingManager.initImpl();
            }
        }
        return sCarSettingManager;
    }

    /**
     * 初始化模块
     */
    public void init() {
        synchronized (CarSettingManager.class) {
            initImpl();
        }
    }

    /**
     * 实际各个模块初始化
     */
    private void initImpl() {
            if (mModelMap.size() > 0) return;
            SystemConfig.initConfig();

            Log.d("CarSettingManager", "CAR_SERIES="+ SystemConfig.CAR_SERIES);
            mModelMap.put(BRIGHTNESS_MODEL, new BrightnessModelFetcher());
            mModelMap.put(VOLUME_MODEL, new VolumeModelFetcher());
            mModelMap.put(WIFI_MODEL, new WifiModelFetcher());
            mModelMap.put(BLUETOOTH_MODEL, new BluetoothModelFetcher());
            mModelMap.put(POWER_MODEL, new PowerModelFetcher());
            mModelMap.put(DEVICE_MODEL, new DeviceModelFetcher());
    }

    /**
     * 销毁模块
     */
    public void destroy() {
        synchronized (CarSettingManager.class) {
            Collection<ModelFetcher> modelFetchers = mModelMap.values();
            if (modelFetchers == null) return;
            Iterator<ModelFetcher> iter = modelFetchers.iterator();
            if (iter == null) return;
            while (iter.hasNext()) {
                ModelFetcher modelFetcher = iter.next();
                if (modelFetcher != null) modelFetcher.onDestroy();
            }
            mModelMap.clear();
        }

    }

    /**
     * 模块基类
     */
    public static abstract class ModelFetcher {
        private Model mModel = null;
        public Model getModel(Context context) {
            if (mModel != null) return mModel;
            return createModel(context);
        }
        public Object peekModel() {
            return mModel;
        }
        public void onDestroy() {
            destroyModel();
        }
        public final Model createModel(Context context) {
            synchronized (ModelFetcher.class) {
                if (mModel == null) {
                    mModel = createModelImpl(context);
                    mModel.onCreate();
                }
            }
            return mModel;
        }

        public abstract Model createModelImpl(Context context);

        public void destroyModel() {
            synchronized (ModelFetcher.class) {
                if (mModel != null) mModel.onDestroy();
                mModel = null;
            }
        }

    }

    /**
     * 亮度模块
     */
   private static class BrightnessModelFetcher extends ModelFetcher {
        @Override
        public Model createModelImpl(Context context) {
            return new BrightnessModel(context);
        }
    }

    /**
     * 音量模块
     */
    private static class VolumeModelFetcher extends ModelFetcher {

        @Override
        public Model createModelImpl(Context context) {
            return new VolumeModel(context);
        }
    }

    /**
     * wifi模块
     */
    private static class WifiModelFetcher extends ModelFetcher {

        @Override
        public Model createModelImpl(Context context) {
            return new WifiModel(context);
        }
    }

    /**
     * bluetooth模块
     */
    private static class BluetoothModelFetcher extends ModelFetcher {

        @Override
        public Model createModelImpl(Context context) {
            return new BluetoothModel(context);
        }
    }

    /**
     * 电源模块
     */
    private static class PowerModelFetcher extends ModelFetcher {

        @Override
        public Model createModelImpl(Context context) {
            return new PowerModel(context);
        }
    }

    /**
     * 设备模块
     */
    private static class DeviceModelFetcher extends ModelFetcher {

        @Override
        public Model createModelImpl(Context context) {
            return new DeviceModel(context);
        }
    }
}
