package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.text.TextUtils;

import java.util.List;

import com.ctop.studentcard.api.OnReceiveListener;
import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.bean.ClickBean;
import com.ctop.studentcard.feature.MainActivity;
import com.ctop.studentcard.util.JsonUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.NetworkUtil;
import com.ctop.studentcard.util.PreferencesUtils;
import com.ctop.studentcard.util.TimeUtils;

public class TimeTickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String timeNowUpdate = TimeUtils.getNowTimeString(TimeUtils.format4);
        //电量
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING ||//充电中
                MainActivity.getSystemBattery(context) >= 50) {//电量大于50%
            int randomSecond=(int)(Math.random()*60);
//            if (timeNowUpdate.equals("1147")) {//凌晨2点到3点之间
            if (timeNowUpdate.equals("02"+randomSecond)) {//凌晨2点到3点之间
                //请求更新apk
                BaseSDK.getInstance().geUpdate("1@", new OnReceiveListener() {
                    @Override
                    public void onResponse(final String msg) {
                    }
                });
            }
        }

        LogUtil.e("TimeTickReceiver===" + TimeUtils.getNowTimeString(TimeUtils.format5));
        if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            //查看实时模式是否到停止时间
            // 1省电模式不上报位置
            // 2平衡模式20min/次
            // 3实时模式3min/次
            String locationMode = PreferencesUtils.getInstance(context).getString("locationMode", "2");
            if (locationMode.equals("3")) {//是 实时模式
                long realTimeModeEnd = PreferencesUtils.getInstance(context).getLong("realTimeModeEnd", 0l);
                if (System.currentTimeMillis() >= realTimeModeEnd) {//实时模式 结束
                    //上报设备模式
                    String locationModeOld = PreferencesUtils.getInstance(context).getString("locationModeOld", "2");
                    if (locationModeOld.equals("1")) {//省电
                        PreferencesUtils.getInstance(context).setString("locationMode", "1");
                        PreferencesUtils.getInstance(context).setLong("locationModeStart", System.currentTimeMillis());
                        BaseSDK.getInstance().setPeriod(9223372036854775807l);
                        LogUtil.e("上报设备模式2");
                        BaseSDK.getInstance().send_device_status("1");
                        PreferencesUtils.getInstance(context).setString("locationModeOld", "1");
                        PreferencesUtils.getInstance(context).setLong("realTimeModeEnd", 0);

                    } else if (locationModeOld.equals("2")) {//平衡
                        PreferencesUtils.getInstance(context).setString("locationMode", "2");
                        PreferencesUtils.getInstance(context).setLong("locationModeStart", System.currentTimeMillis());
                        BaseSDK.getInstance().setPeriod(20 * 60 * 60);
                        LogUtil.e("上报设备模式3");
                        BaseSDK.getInstance().send_device_status("2");

                        PreferencesUtils.getInstance(context).setString("locationModeOld", "2");
                        PreferencesUtils.getInstance(context).setLong("realTimeModeEnd", 0);
                    } else if (locationModeOld.equals("4")) {//待机

                    }
                }
            }
            //待机模式
            /**
             * 开始：停止tcp
             * 结束：打开tcp
             *
             * 0200-1200
             * 2100-0600
             * 2300
             * 0900
             */
            String awaitstart = PreferencesUtils.getInstance(context).getString("awaitModeStart", "");
            String awaitend = PreferencesUtils.getInstance(context).getString("awaitModeEnd", "");
            if (!TextUtils.isEmpty(awaitstart)) {
                int awaitstartInt = Integer.parseInt(awaitstart);
                int awaitendInt = Integer.parseInt(awaitend);
                String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                int timeNowInt = Integer.parseInt(timeNow);
                if (awaitstartInt < awaitendInt) {//同一天
                    if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {
                        PreferencesUtils.getInstance(context).setString("locationModeOld", PreferencesUtils.getInstance(context).getString("locationMode", "2"));
                        LogUtil.e("上报设备模式4");
                        BaseSDK.getInstance().send_device_status("4");
                        BaseSDK.getInstance().stopTcp();
                    } else {
                        if (NetworkUtil.isAvailable(context)) {
                            BaseSDK.getInstance().init(context);
                            PreferencesUtils.getInstance(context).setString("locationModeOld", "4");
                            LogUtil.e("上报设备模式5");
                            BaseSDK.getInstance().send_device_status(PreferencesUtils.getInstance(context).getString("locationMode", "2"));
                        }
                    }
                } else {//不同天
                    if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                        PreferencesUtils.getInstance(context).setString("locationModeOld", PreferencesUtils.getInstance(context).getString("locationMode", "2"));
                        LogUtil.e("上报设备模式6");
                        BaseSDK.getInstance().send_device_status("4");
                        BaseSDK.getInstance().stopTcp();
                    } else {
                        if (NetworkUtil.isAvailable(context)) {
                            BaseSDK.getInstance().init(context);
                            PreferencesUtils.getInstance(context).setString("locationModeOld", "4");
                            LogUtil.e("上报设备模式7");
                            BaseSDK.getInstance().send_device_status(PreferencesUtils.getInstance(context).getString("locationMode", "2"));
                        }
                    }
                }
            }

            //闹钟
            String clickBeanString = PreferencesUtils.getInstance(context).getString("clickBean", "");
            ClickBean clickBean = JsonUtil.parseObject(clickBeanString, ClickBean.class);
            if (null == clickBean)
                return;
            List<ClickBean.ItemsBean> itemsBeanList = clickBean.getItems();
            for (int i = 0; i < itemsBeanList.size(); i++) {
                if ("1".equals(itemsBeanList.get(i).getIsEffect())) continue;//不生效
                List<ClickBean.ItemsBean.PeriodBean> periodBeans = itemsBeanList.get(i).getPeriod();
                for (int j = 0; j < periodBeans.size(); j++) {
                    if (TimeUtils.getWeek().equals(periodBeans.get(i).getWeek())) {
                        String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                        if (timeNow.equals(itemsBeanList.get(i).getTime())) {//
                            Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
                            vibrator.vibrate(30 * 1000);//震动30秒
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                            Ringtone r = RingtoneManager.getRingtone(context, notification);
                            r.play();//播放闹铃
                        }
                    }
                }
            }


        }
    }
}
