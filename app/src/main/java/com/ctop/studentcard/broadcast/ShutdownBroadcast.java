package com.ctop.studentcard.broadcast;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ctop.studentcard.api.OnReceiveListener;
import com.ctop.studentcard.util.DeviceUtil;
import com.ctop.studentcard.util.LogUtil;

import com.ctop.studentcard.base.BaseSDK;

public class ShutdownBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("ShutdownBroadcast");
        //关机报警
        if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
            String data = "2@"+ DeviceUtil.getBattery();
            //设备关机通知
            BaseSDK.getInstance().sendBootAndShutdown(data, new OnReceiveListener() {
                @Override
                public void onResponse(final String msg) {
                    LogUtil.d("自动关机的return: " + msg);
                }
            });
        }
    }
}