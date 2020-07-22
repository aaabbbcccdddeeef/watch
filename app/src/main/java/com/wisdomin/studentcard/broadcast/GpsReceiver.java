package com.wisdomin.studentcard.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.util.LogUtil;

public class GpsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("GpsReceiver");
        String action = intent.getAction();
        if (action.equals(BroadcastConstant.GPS)) {

            if(BaseSDK.getInstance().getConnectStatus()){//上报地理位置
                BaseSDK.getInstance().findWifi();
            }

            //因为setWindow只执行一次，所以要重新定义闹钟实现循环。
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intentRe = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseSDK.getInstance().getBaseContext(), 0, intentRe, PendingIntent.FLAG_CANCEL_CURRENT);
            LogUtil.e("getPeriod()==="+BaseSDK.getInstance().getPeriod());
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (BaseSDK.getInstance().getPeriod() * 1000), pendingIntent);

        } else if (action.equals(BroadcastConstant.GPS_GET)) {//主动获取地理位置

            BaseSDK.getInstance().findGPSGet();
        }

    }
}