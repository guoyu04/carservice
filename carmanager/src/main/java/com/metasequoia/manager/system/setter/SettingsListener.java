package com.metasequoia.manager.system.setter;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.metasequoia.manager.listener.OnListener;

/**
 * 系统设置状态值监听类
 *
 * Created by guoyu on 2020/8/12.
 */
public abstract class SettingsListener<valueType> extends IValueListener<String, valueType> {

    private Context mContext;

    private boolean mRegister = false;

    public static enum TableType{SYSTEM,SECURE,GLOBAL};

    /**
     * 对应provider表：SYSTEM,SECURE,GLOBAL
     */
    private final TableType mTable;

    public SettingsListener(Context context, String keyName) {
        this(context,keyName, TableType.SYSTEM);
    }

    public SettingsListener(Context context, String keyName, TableType table) {
        super(keyName);
        mContext = context;
        mTable=table;
    }

    /**
     * 获取对应的表：SYSTEM,SECURE,GLOBAL
     * @return
     */
    public TableType getTable() {
        return mTable;
    }
    private void OnContentValueChanged() {
        this.dispatchOnChanged(getValue());
    }

    /**
     * 获取表key对应的url
     * @param context
     * @param param
     * @param table
     * @return
     */
    public static Uri getTableKeyUri(Context context, String param, TableType table) {
        if (table == TableType.SECURE) {
            return SettingsProvider.Secure.getUriFor(param);
        } else if (table == TableType.GLOBAL) {
            return SettingsProvider.Global.getUriFor(param);
        }
        return SettingsProvider.System.getUriFor(param);
    }

    ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {

        public void onChange(boolean selfChange) {
            OnContentValueChanged();
        }
    };

    /**
     * 注册key值改变监听
     * @param obverser
     */
    public void registerOnChangedListener(OnListener.OnChangedListener<valueType> obverser) {
        super.registerOnChangedListener(obverser);
        if (!mRegister && !TextUtils.isEmpty(this.getKeyName())) {
            registerTableKeyObserver(mContext, true, this.getKeyName(), mContentObserver,this.getTable());
            mRegister = true;
        }
    }
    /**
     * 注销key值改变监听
     * @param obverser
     */
    public void unregisterOnChangedListener(OnListener.OnChangedListener<valueType> obverser) {
        super.unregisterOnChangedListener(obverser);
        if (mRegister && (getChangedListeners() == null || getChangedListeners().isEmpty())) {
            unregisterTableKeyObserver(mContext, mContentObserver);
            mRegister = false;
        }
    }

    protected static void registerTableKeyObserver(Context context, boolean notifyForDescendents, String param, ContentObserver observer, TableType table) {
        try {
            Uri paramUri = getTableKeyUri(context, param,table);
            context.getContentResolver().registerContentObserver(paramUri, notifyForDescendents, observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void unregisterTableKeyObserver(Context context, ContentObserver observer) {
        try {
            context.getContentResolver().unregisterContentObserver(observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
