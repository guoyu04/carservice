package com.metasequoia.manager.system.setter;

import android.content.ContentResolver;
import android.net.Uri;

import com.metasequoia.manager.constent.SystemConfig;


/**
 * 系统设置provider设置及监听类：
 * android.provider.Settings系统标准
 * com.pvetec.systemsdk.settings.Settings掌锐系统设置
 *
 * Created by guoyu on 2020/8/12.
 */
public class SettingsProvider {
    public static final String AUTHORITY;//SystemProvider  settings

    static {
        if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX) {
            AUTHORITY = "settings";
        }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
            AUTHORITY = "SystemProvider";
        }else {
            AUTHORITY = "settings";
        }
    }
    /**
     * System表
     */
    public static final class System {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/system");

        public static Uri getUriFor(String name) {
            return Uri.withAppendedPath(CONTENT_URI, name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
                //TODO
                return -1;
            }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
                //TODO
                return -1;
            } else{
                //default
                return android.provider.Settings.System.getInt(cr, name, def);
            }
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
                //TODO
                return false;
            }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
                //TODO
                return false;
            } else {
                //default
                return android.provider.Settings.System.putInt(cr,name,value);
            }
        }
    }

    /**
     * Secure表
     */
    public static final class Secure {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/secure");

        public static Uri getUriFor(String name) {
            return Uri.withAppendedPath(CONTENT_URI, name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
                //TODO
                return -1;
            }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
                //TODO
                return -1;
            } else {
                //default
                return android.provider.Settings.Secure.getInt(cr, name, def);
            }

        }


        public static boolean putInt(ContentResolver cr, String name, int value) {
            if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
                //TODO
                return false;
            }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
                //TODO
                return false;
            } else{
                return android.provider.Settings.Secure.putInt(cr,name,value);
            }
        }
    }

    /**
     * Global表
     */
    public static final class Global {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/global");

        public static Uri getUriFor(String name) {
            return Uri.withAppendedPath(CONTENT_URI, name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
                //TODO
                return -1;
            }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
                //TODO
                return -1;
            } else{
                //default
                return android.provider.Settings.Global.getInt(cr, name, def);
            }
        }


        public static boolean putInt(ContentResolver cr, String name, int value) {
            if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_AXX){
                //TODO
                return false;
            }else if(SystemConfig.CAR_SERIES == SystemConfig.CAR_SERIES_BXX){
                //TODO
                return false;
            } else{
                //default
                return android.provider.Settings.Global.putInt(cr,name,value);
            }
        }
    }
}
