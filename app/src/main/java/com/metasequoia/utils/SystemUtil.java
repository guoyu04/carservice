package com.metasequoia.utils;

import android.content.Context;
import android.provider.Settings;

import java.lang.reflect.Method;

/**
 * 系统工具类
 * Created by guoyu on 2020/7/16.
 */
public class SystemUtil {
    private static String SYS_PROJECT = "";

    public static String getProjectCode(Context context){
        if("".equals(SYS_PROJECT)){
            SYS_PROJECT = getStringSetting(context, "ro.fota.version");
        }
        return SYS_PROJECT;
    }

    public static int getIntSetting(Context context, String property){
        return Settings.Global.getInt(context.getContentResolver(), property, 0);
    }

    public static void setIntSetting(Context context, String property, int value){
        Settings.Global.putInt(context.getContentResolver(), property, value);
    }

    public static String getStringSetting(Context context, String property){
        return Settings.Global.getString(context.getContentResolver(), property);
    }

    public static void setStringSetting(Context context, String property, String value){
        Settings.Global.putString(context.getContentResolver(), property, value);
    }
}
