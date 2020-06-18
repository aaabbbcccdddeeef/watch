package com.ctop.studentcard.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.util.AppConst;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;

public class HeartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("HeartReceiver");
        String action = intent.getAction();
        if (action.equals(BroadcastConstant.HEART_BEAT)) {

            if(BaseSDK.getInstance().getConnectStatus()){//定时上报
                BaseSDK.getInstance().send_report_heart();
            }else {
                String locationModeNow = PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE);
                if(!locationModeNow.equals(AppConst.MODEL_AWAIT)){//待机模式时候，不链接平台
                    BaseSDK.getInstance().connect();
                }
            }
            //因为setWindow只执行一次，所以要重新定义闹钟实现循环。
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intentRe = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseSDK.getInstance().getBaseContext(), 0, intentRe, PendingIntent.FLAG_CANCEL_CURRENT);
            LogUtil.e("getPeriod()==="+BaseSDK.getInstance().getPeriod());
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (BaseSDK.getInstance().getPeriodHeart() * 1000), pendingIntent);
        }
    }
}
