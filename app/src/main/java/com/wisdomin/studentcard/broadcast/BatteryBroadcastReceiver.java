package com.wisdomin.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.util.DeviceUtil;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;

public class BatteryBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int ret = batteryInfoIntent.getIntExtra("level", 0);
        long POWER_CONNECTED = PreferencesUtils.getInstance(context).getLong("POWER_CONNECTED", 0);
        long POWER_DISCONNECTED = PreferencesUtils.getInstance(context).getLong("POWER_DISCONNECTED", 0);
        long POWER_FULL = PreferencesUtils.getInstance(context).getLong("POWER_FULL", 0);

        //接通电源
        if(intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
            LogUtil.d("ACTION_POWER_CONNECTED");
            LogUtil.d(""+POWER_CONNECTED+(1000 * 60 *10));
            LogUtil.d(""+System.currentTimeMillis());

            if((POWER_CONNECTED+new Long(1000 * 60 *10))<System.currentTimeMillis()){
                PreferencesUtils.getInstance(context).setLong("POWER_CONNECTED", System.currentTimeMillis());
                BaseSDK.getInstance().sendAlarmPower("5@"+ret+"%",null);
            }

        }else if(intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){//拔掉电源
            LogUtil.d("ACTION_POWER_DISCONNECTED");
            LogUtil.d("BATTERY==="+ret);
            if((POWER_DISCONNECTED+new Long(1000 * 60 *10))<System.currentTimeMillis()){
                PreferencesUtils.getInstance(context).setLong("POWER_DISCONNECTED", System.currentTimeMillis());
                BaseSDK.getInstance().sendAlarmPower("6@"+ret+"%", null);
            }

        }else if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)){//电量低
            LogUtil.d("ACTION_BATTERY_LOW==="+ret);
        }
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            LogUtil.d("ACTION_BATTERY_CHANGED");
            //更新电量数字
            sendBrodcast(context,ret+"%");
            //电量已经充满
            if(100==ret){
                boolean butteryFullNow = PreferencesUtils.getInstance(context).getBoolean("butteryFullNow", false);
                if(!butteryFullNow){
                    if((POWER_FULL+new Long(1000 * 60 *10))<System.currentTimeMillis()){
                        PreferencesUtils.getInstance(context).setBoolean("butteryFullNow", true);
                        PreferencesUtils.getInstance(context).setLong("POWER_FULL", System.currentTimeMillis());
                        BaseSDK.getInstance().sendAlarmPower("7@"+ret+"%", null);
                    }

                }
            }else {
                PreferencesUtils.getInstance(context).setBoolean("butteryFullNow", false);
            }
            if(ret<=5){//自动关机
                boolean belowFiveNow = PreferencesUtils.getInstance(context).getBoolean("belowFiveNow", false);
                if(!belowFiveNow){//电量持续低于5%时候，只有第一次关机
                    PreferencesUtils.getInstance(context).setBoolean("belowFiveNow", true);
                    BaseSDK.getInstance().sendAlarmPower("3@"+ret+"%", null);
                    DeviceUtil.shutDowmMore(context);
                }
            }else if(ret<=10){//低电量报警
                boolean belowTenNow = PreferencesUtils.getInstance(context).getBoolean("belowTenNow", false);
                if(!belowTenNow){
                    PreferencesUtils.getInstance(context).setBoolean("belowTenNow", true);
                    LogUtil.d("发送sendAlarmPower");
                    BaseSDK.getInstance().sendAlarmPower("1@"+ret+"%", null);
                }
            }else {
                PreferencesUtils.getInstance(context).setBoolean("belowFiveNow", false);
                PreferencesUtils.getInstance(context).setBoolean("belowTenNow", false);
            }
        }
    }

    private void sendBrodcast(Context context,String ret) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.BUTTERY_STATE);
        intent.putExtra("state", ret);
        context.sendBroadcast(intent);// 发送
    }

}
