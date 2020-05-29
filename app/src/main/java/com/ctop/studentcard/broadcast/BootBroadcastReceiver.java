package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ctop.studentcard.api.OnReceiveListener;
import com.ctop.studentcard.util.AppConst;
import com.ctop.studentcard.util.DeviceUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;

import com.ctop.studentcard.base.BaseSDK;

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override  
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("BootBroadcastReceiver");
        //开机报警
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            sendBoot(context);
            BaseSDK.setBaseContext(context.getApplicationContext());
            if(BaseSDK.getInstance().getConnectStatus()){
                String dataButtery = "4@" + DeviceUtil.getBattery();
                BaseSDK.getInstance().sendBootAndShutdown(dataButtery, new OnReceiveListener() {
                    @Override
                    public void onResponse(final String msg) {
                        LogUtil.d("自动开机的return: " + msg);
                    }
                });
            }else {
                AppConst.BOOTBROADCAST = true;
            }

            LogUtil.e("bootonReceive");
//            BaseSDK.getInstance().connect();
        }
    }

    //
    public static void sendBoot(Context context) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.BOOT_STATE);
        context.sendBroadcast(intent);// 发送
    }
} 