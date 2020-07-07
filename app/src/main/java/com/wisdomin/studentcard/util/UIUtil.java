package com.wisdomin.studentcard.util;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class UIUtil {


    private static LocalBroadcastManager mLocalBroadcastManager;

    public static LocalBroadcastManager getLocalBroadcastManager(Context mcontext) {
        if (mLocalBroadcastManager == null)
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(mcontext);
        return mLocalBroadcastManager;
    }


    /**
     * 包名
     *
     * @param appPackageName
     */
    public static void startAPP(Context context, String appPackageName,
                                String startActivity) {
        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(appPackageName, startActivity);
            intent.setComponent(comp);
            intent.setAction("android.intent.action.LAUNCHER");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
