package com.metasequoia.manager.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class Utils {

    public static final String TAG="SystemSdkUtils";
    public static Intent createExplicitService(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(intent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    public static int normalizeValue(int value, int min, int max) {
        value = (value < min ? min : value);
        value = (value > max ? max : value);
        return value;
    }
    
    
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
    
    public static void startActivity(Context context, Intent intent) {
        if (intent == null || context == null) return;
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public static ComponentName getRunningActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfoList=activityManager.getRunningTasks(1);
        if(runningTaskInfoList==null||runningTaskInfoList.isEmpty()) return null;
        ComponentName runningActivity = runningTaskInfoList.get(0).topActivity;
        return runningActivity;
    }
    
    public static ComponentName getLauncherActivity(Context context, String packageName) {
        return getLauncherActivity(context, Intent.CATEGORY_LAUNCHER, packageName);
    }
    
    public static ComponentName getPvetecLauncherActivity(Context context, String packageName) {
        return getLauncherActivity(context, Intent.CATEGORY_LAUNCHER+".PVETEC", packageName);
    }
    public static ComponentName  getLauncherActivity(Context context,String launcherCategory,String packageName) {
        if(context==null||packageName==null) return null;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(launcherCategory);
        mainIntent.setPackage(packageName);
        PackageManager mPm=context.getPackageManager();
        List<ResolveInfo> infos = mPm.queryIntentActivities(mainIntent, 0);
        if(infos==null||infos.size()==0) return null;
        ResolveInfo resolveInfo=infos.get(0);
        String launchPackageName = resolveInfo.activityInfo.packageName;
        String launchClassName = resolveInfo.activityInfo.name;
        return new ComponentName(launchPackageName, launchClassName);
    }
    
}
