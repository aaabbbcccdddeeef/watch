package com.ctop.studentcard.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.util.LogUtil;

public class GpsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("GpsReceiver");
        String action = intent.getAction();
        if (action.equals(BroadcastConstant.GPS)) {

            BaseSDK.getInstance().findGPS();

            //因为setWindow只执行一次，所以要重新定义闹钟实现循环。
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intentRe = new Intent(action);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intentRe, PendingIntent.FLAG_CANCEL_CURRENT);
            LogUtil.e("getPeriod()==="+BaseSDK.getInstance().getPeriod());
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (BaseSDK.getInstance().getPeriod() * 1000), sender);

        } else if (action.equals(BroadcastConstant.GPS_GET)) {

            BaseSDK.getInstance().findGPSGet();
        }


    }
}