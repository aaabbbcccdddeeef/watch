package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ctop.studentcard.util.AppConst;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;

import com.ctop.studentcard.base.BaseSDK;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override  
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("BootBroadcastReceiver");
        //开机报警
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            BaseSDK.setBaseContext(context.getApplicationContext());
            AppConst.BOOTBROADCAST = true;
            LogUtil.e("bootonReceive");
//            BaseSDK.getInstance().connect();
        }
    }  
} 