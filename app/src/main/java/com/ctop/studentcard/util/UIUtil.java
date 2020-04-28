package com.ctop.studentcard.util;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

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

}
