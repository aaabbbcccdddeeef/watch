package com.wisdomin.studentcard.broadcast;

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

import com.wisdomin.studentcard.api.OnReceiveListener;
import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.bean.ClickBean;
import com.wisdomin.studentcard.bean.TemFrequency;
import com.wisdomin.studentcard.feature.MainActivity;
import com.wisdomin.studentcard.feature.step.StepUtils;
import com.wisdomin.studentcard.util.AppConst;
import com.wisdomin.studentcard.util.DeviceUtil;
import com.wisdomin.studentcard.util.JsonUtil;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.ModeUtils;
import com.wisdomin.studentcard.util.PreferencesUtils;
import com.wisdomin.studentcard.util.TimeUtils;


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
                LogUtil.e("timeNowUpdate==="+timeNowUpdate);
                LogUtil.e("randomSecond==="+("02"+randomSecond));
                //如果现在是待机模式，需要开启tcp
                String state = PreferencesUtils.getInstance(context).getString("","");
                if(state.equals(AppConst.MODEL_AWAIT)){
                    BaseSDK.getInstance().connect();
                }
//
                //请求更新apk
                BaseSDK.getInstance().geUpdate("1@", new OnReceiveListener() {
                    @Override
                    public void onResponse(final String msg) {
                    }
                });
            }
        }
        //非课堂模式，响铃
        if(!ModeUtils.inclassmode(context)){
            DeviceUtil.silentSwitchOff(context);
        }

        LogUtil.e("TimeTickReceiver===" + TimeUtils.getNowTimeString(TimeUtils.format5));
        if (intent.ACTION_TIME_TICK.equals(intent.getAction())) {
            //查看实时模式是否到停止时间
            // 1省电模式不上报位置
            // 2平衡模式20min/次
            // 3实时模式3min/次
            String locationMode = PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE);
            if (locationMode.equals(AppConst.MODEL_REAL_TIME)) {//是 实时模式
                long realTimeModeEnd = PreferencesUtils.getInstance(context).getLong("realTimeModeEnd", 0l);
                if (System.currentTimeMillis() >= realTimeModeEnd) {//实时模式 结束
                    //上报设备模式
                    String locationModeOld = PreferencesUtils.getInstance(context).getString("locationModeOld",  AppConst.MODEL_BALANCE);
                    if (locationModeOld.equals(AppConst.MODEL_POWER_SAVING)) {//省电
                        PreferencesUtils.getInstance(context).setString("locationMode", AppConst.MODEL_POWER_SAVING);
                        PreferencesUtils.getInstance(context).setLong("locationModeStart", System.currentTimeMillis());
                        BaseSDK.getInstance().setPeriod(2147483647);
                        LogUtil.e("上报设备模式2");
                        BaseSDK.getInstance().send_device_status(AppConst.MODEL_POWER_SAVING);
                        PreferencesUtils.getInstance(context).setString("locationModeOld", AppConst.MODEL_POWER_SAVING);
                        PreferencesUtils.getInstance(context).setLong("realTimeModeEnd", 0);

                    } else if (locationModeOld.equals(AppConst.MODEL_BALANCE)) {//平衡
                        PreferencesUtils.getInstance(context).setString("locationMode", AppConst.MODEL_BALANCE);
                        PreferencesUtils.getInstance(context).setLong("locationModeStart", System.currentTimeMillis());
                        BaseSDK.getInstance().setPeriod(20 * 60);
                        LogUtil.e("上报设备模式3");
                        BaseSDK.getInstance().send_device_status(AppConst.MODEL_BALANCE);

                        PreferencesUtils.getInstance(context).setString("locationModeOld", AppConst.MODEL_BALANCE);
                        PreferencesUtils.getInstance(context).setLong("realTimeModeEnd", 0);
                    } else if (locationModeOld.equals(AppConst.MODEL_AWAIT)) {//待机
                        PreferencesUtils.getInstance(context).setString("locationMode", AppConst.MODEL_AWAIT);
                        PreferencesUtils.getInstance(context).setLong("locationModeStart", System.currentTimeMillis());

                        BaseSDK.getInstance().canalAlarm(BaseSDK.getInstance().getBaseContext(),BroadcastConstant.GPS,0);

                        LogUtil.e("上报设备模式3-0");
                        BaseSDK.getInstance().send_device_status(AppConst.MODEL_BALANCE);
                        BaseSDK.getInstance().stopTcp();
                        PreferencesUtils.getInstance(context).setString("locationModeOld", AppConst.MODEL_BALANCE);
                        PreferencesUtils.getInstance(context).setLong("realTimeModeEnd", 0);
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
            String locationModeNow = PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE);
            if (!TextUtils.isEmpty(awaitstart)) {
                int awaitstartInt = Integer.parseInt(awaitstart);
                int awaitendInt = Integer.parseInt(awaitend);
                String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                int timeNowInt = Integer.parseInt(timeNow);
                if (awaitstartInt < awaitendInt) {//同一天
                    if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {//处于 待机 时间段内
                        String WAKEUPTime = PreferencesUtils.getInstance(context).getString("WAKEUPTime", "");
                        if(!TextUtils.isEmpty(WAKEUPTime) && TimeUtils.getNowTimeString(TimeUtils.format8).equals(WAKEUPTime)){
                            return;//被唤醒
                        }
                        if(!locationModeNow.equals(AppConst.MODEL_AWAIT)){
                            PreferencesUtils.getInstance(context).setString("locationModeOld", PreferencesUtils.getInstance(context).getString("locationMode",  AppConst.MODEL_BALANCE));
                            PreferencesUtils.getInstance(context).setString("locationMode", AppConst.MODEL_AWAIT);//修改为待机模式
                            //上报一次位置
                            BaseSDK.getInstance().canalAlarm(context, BroadcastConstant.GPS, 0);
                            BaseSDK.getInstance().setAlarmTime(context, System.currentTimeMillis(), BroadcastConstant.GPS, 0, 0);

                            LogUtil.e("上报设备模式4");
                            BaseSDK.getInstance().send_device_status(AppConst.MODEL_AWAIT);
//                            BaseSDK.getInstance().stopTcp();
                            BaseSDK.getInstance().setAwait_stoptcp(true);

                        }
                    } else {
                        if(locationModeNow.equals(AppConst.MODEL_AWAIT)) {
                            PreferencesUtils.getInstance(context).setString("locationMode",
                                    PreferencesUtils.getInstance(context).getString("locationModeOld",  AppConst.MODEL_BALANCE));//从待机模式 还原
                            BaseSDK.getInstance().init(context);
                            PreferencesUtils.getInstance(context).setString("locationModeOld", AppConst.MODEL_AWAIT);
                            LogUtil.e("上报设备模式5");
                            BaseSDK.getInstance().send_device_status(PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE));
                        }
                    }
                } else {//不同天
                    if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                        String WAKEUPTime = PreferencesUtils.getInstance(context).getString("WAKEUPTime", "");
                        if(!TextUtils.isEmpty(WAKEUPTime) && TimeUtils.getNowTimeString(TimeUtils.format8).equals(WAKEUPTime)){
                            return;//被唤醒
                        }
                        if(!locationModeNow.equals(AppConst.MODEL_AWAIT)) {
                            PreferencesUtils.getInstance(context).setString("locationModeOld", PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE));
                            PreferencesUtils.getInstance(context).setString("locationMode", AppConst.MODEL_AWAIT);//修改为待机模式
                            //上报一次位置
                            BaseSDK.getInstance().canalAlarm(context, BroadcastConstant.GPS, 0);
                            BaseSDK.getInstance().setAlarmTime(context, System.currentTimeMillis(), BroadcastConstant.GPS, 0, 0);
                            LogUtil.e("上报设备模式6");
                            BaseSDK.getInstance().send_device_status(AppConst.MODEL_AWAIT);
//                            BaseSDK.getInstance().stopTcp();
                            BaseSDK.getInstance().setAwait_stoptcp(true);
                        }
                    } else {
                        if(locationModeNow.equals(AppConst.MODEL_AWAIT)) {
                            PreferencesUtils.getInstance(context).setString("locationMode", PreferencesUtils.getInstance(context).getString("locationModeOld",  AppConst.MODEL_BALANCE));//从待机模式 还原
                            BaseSDK.getInstance().init(context);
                            PreferencesUtils.getInstance(context).setString("locationModeOld", AppConst.MODEL_AWAIT);
                            LogUtil.e("上报设备模式7");
                            BaseSDK.getInstance().send_device_status(PreferencesUtils.getInstance(context).getString("locationMode",  AppConst.MODEL_BALANCE));
                        }
                    }
                }
            }else {//系统默认待机模式晚11点 到 早 6点
                String awaitstartSys = "2300";
                String awaitendSys = "0600";
                String locationModeNowSys = PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE);
                int awaitstartInt = Integer.parseInt(awaitstartSys);
                int awaitendInt = Integer.parseInt(awaitendSys);
                String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                int timeNowInt = Integer.parseInt(timeNow);
                if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                    String WAKEUPTime = PreferencesUtils.getInstance(context).getString("WAKEUPTime", "");
                    if(!TextUtils.isEmpty(WAKEUPTime) && TimeUtils.getNowTimeString(TimeUtils.format8).equals(WAKEUPTime)){
                        return;//被唤醒
                    }
                    if(!locationModeNow.equals(AppConst.MODEL_AWAIT)) {
                        PreferencesUtils.getInstance(context).setString("locationMode", AppConst.MODEL_AWAIT);//修改为待机模式
                        PreferencesUtils.getInstance(context).setString("locationModeOld", PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE));

                        //上报一次位置
                        BaseSDK.getInstance().canalAlarm(context, BroadcastConstant.GPS, 0);
                        BaseSDK.getInstance().setAlarmTime(context, System.currentTimeMillis(), BroadcastConstant.GPS, 0, 0);

                        LogUtil.e("上报设备模式6");
                        BaseSDK.getInstance().send_device_status(AppConst.MODEL_AWAIT);
//                        BaseSDK.getInstance().stopTcp();
                        BaseSDK.getInstance().setAwait_stoptcp(true);
                    }
                } else {
                    if(locationModeNow.equals(AppConst.MODEL_AWAIT)) {
                        PreferencesUtils.getInstance(context).setString("locationMode",
                                PreferencesUtils.getInstance(context).getString("locationModeOld",  AppConst.MODEL_BALANCE));//从待机模式 还原
                        BaseSDK.getInstance().init(context);
                        PreferencesUtils.getInstance(context).setString("locationModeOld", AppConst.MODEL_AWAIT);
                        LogUtil.e("上报设备模式7");
                        BaseSDK.getInstance().send_device_status(PreferencesUtils.getInstance(context).getString("locationMode",  AppConst.MODEL_BALANCE));
                    }
                }
            }

            //闹钟
            String clickBeanString = PreferencesUtils.getInstance(context).getString("clickBean", "");
            if(!TextUtils.isEmpty(clickBeanString) && !clickBeanString.equals("null")){
                ClickBean clickBean = JsonUtil.parseObject(clickBeanString, ClickBean.class);
                List<ClickBean.ItemsBean> itemsBeanList = clickBean.getItems();
                for (int i = 0; i < itemsBeanList.size(); i++) {
                    if ("1".equals(itemsBeanList.get(i).getIsEffect())) continue;//不生效
                    List<ClickBean.ItemsBean.PeriodBean> periodBeans = itemsBeanList.get(i).getPeriod();
                    for (int j = 0; j < periodBeans.size(); j++) {
                        if (TimeUtils.getWeekInCome().equals(periodBeans.get(i).getWeek())) {
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


            //定时上报 温度
            String temFrequencystr = PreferencesUtils.getInstance(context).getString("temFrequency", "");
            if(!TextUtils.isEmpty(temFrequencystr) && !temFrequencystr.equals("null")){
                TemFrequency mTemFrequency = JsonUtil.parseObject(temFrequencystr, TemFrequency.class);
                List<TemFrequency.Frequency> items = mTemFrequency.getItems();
                for(int i = 0;i<items.size();i++){
                    TemFrequency.Frequency frequency =  items.get(i);
                    String day = frequency.getDay();
                    if(day.equals(TimeUtils.getWeekInCome())){//日期相同
                        List<String> times = frequency.getTimes();
                        for(String time:times){
                            if(time.equals(TimeUtils.getNowTimeString(TimeUtils.format4))){//时间相同
                                //发送测温广播
                                Intent intentTem = new Intent();
                                intentTem.setAction(BroadcastConstant.TEMPERATURE_START);
                                context.sendBroadcast(intentTem);// 发送
                                LogUtil.e("开始：定时测温");
                            }
                        }

                    }

                }
            }
            //每天最后一次，定时上报 步数，然后清空步数
            if("2255".equals(TimeUtils.getNowTimeString(TimeUtils.format4))) {
                //上报
                int step = StepUtils.getStep();
                StepUtils.clearStep();
                //如果是待机模式  不发送报文，
                if(!locationModeNow.equals(AppConst.MODEL_AWAIT)) {
                    BaseSDK.getInstance().sendHealth(TimeUtils.getNowTimeString(TimeUtils.format4) + "-" + TimeUtils.getNowTimeString(TimeUtils.format4) + "@0@0@"+ step);
                }
            }
        }
    }
}
