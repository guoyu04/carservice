package com.metasequoia.manager.system.setter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 系统设置状态值监听类:int
 *
 * Created by guoyu on 2020/8/12.
 */
public class SettingsIntListener extends SettingsListener<Integer> {

    protected Context mContext = null;

    public SettingsIntListener(Context context, String keyName) {
        this(context,keyName,TableType.SYSTEM);
    }

    public SettingsIntListener(Context context, String keyName, TableType table) {
        super(context, keyName,table);
        mContext = context.getApplicationContext();
    }


    /**
     * 设置值到provider
     * @param value 值
     */
    @Override
    public boolean setValue(Integer value) {
        return setValue(value,true);
    }

    /**
     * 设置值到provider
     * @param value 值
     * @param notify 知否设置通知监听
     */
    @Override
    public boolean setValue(Integer value,boolean notify) {
        setValueForKey(mContext, value, notify, this.getKeyName(),this.getTable());
        return true;
    }

    /**
     * 获取值，如果未设置，则返回-1
     * @return 结果
     */
    @Override
    public Integer getValue() {
        return getValue(-1);
    }

    /**
     * 获取值，如果未设置，则返回默认值
     * @param defaultValue 默认值
     * @return 结果
     */
    @Override
    public Integer getValue(Integer defaultValue) {
        return getValueForKey(mContext, this.getKeyName(), defaultValue,this.getTable());
    }

    /**
     * 根据keyName从provider设置获取值
     * @param context Context
     * @param keyName Table对应的key
     * @param defaultValue 默认值
     * @param table System、Secure、Global
     * @return 结果值
     */
    public static int getValueForKey(Context context, String keyName, int defaultValue, TableType table) {
        try {
            if(TextUtils.isEmpty(keyName)){
                return 0;
            }
            if (table == TableType.SECURE) {
                return SettingsProvider.Secure.getInt(context.getContentResolver(), keyName, defaultValue);
            } else if (table == TableType.GLOBAL) {
                return SettingsProvider.Global.getInt(context.getContentResolver(), keyName, defaultValue);
            } else {
                return SettingsProvider.System.getInt(context.getContentResolver(), keyName, defaultValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 根据keyName设置值到provider设置
     * @param context Context
     * @param value 值
     * @param notify 是否设置通知监听
     * @param keyName Table对应的key
     * @param table System、Secure、Global
     */
    public static void setValueForKey(Context context, int value, boolean notify, String keyName, TableType table) {
        try {
            if(TextUtils.isEmpty(keyName)){
                return;
            }
            Uri paramUri = getTableKeyUri(context, keyName, table);
            if (table == TableType.SECURE) {
                SettingsProvider.Secure.putInt(context.getContentResolver(), keyName, value);
            } else if (table == TableType.GLOBAL) {
                SettingsProvider.Global.putInt(context.getContentResolver(), keyName, value);
            } else {
                SettingsProvider.System.putInt(context.getContentResolver(), keyName, value);
            }
            if (notify) context.getContentResolver().notifyChange(paramUri, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}