package com.wisdomin.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.wisdomin.studentcard.api.OnReceiveListener;
import com.wisdomin.studentcard.util.DeviceUtil;
import com.wisdomin.studentcard.util.LogUtil;

import com.wisdomin.studentcard.base.BaseSDK;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

    private boolean flag = false;//从高电量 到 低电量

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            // 当前电量
            int level =intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 1)*100;
            // 最大电量
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
            int ret =  level/scale;
            sendBrodcast(context,ret+"%");
            if(ret<=5){//自动关机
                BaseSDK.getInstance().sendAlarmPower("3@"+ret+"%", new OnReceiveListener() {
                    @Override
                    public void onResponse(final String msg) {
                        LogUtil.d("自动关机的return: " + msg);
                    }
                });
                DeviceUtil.shutDowm();
            }else if(ret<=10){//低电量报警
                LogUtil.d("flag："+flag+"");
                if(flag==false){
                    flag = true;
                    LogUtil.d("发送sendAlarmPower");
                    BaseSDK.getInstance().sendAlarmPower("1@"+ret+"%", new OnReceiveListener() {
                        @Override
                        public void onResponse(final String msg) {
                            LogUtil.d("低电量报警的return: " + msg);
                        }
                    });
                }
            }else {
                flag = false;//电量高于10时，恢复false
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
