package com.wisdomin.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wisdomin.studentcard.feature.message.DialogActivity;
import com.wisdomin.studentcard.util.LogUtil;

import static com.wisdomin.studentcard.broadcast.BroadcastConstant.SHOW_SCREEN_SMS;

/**
 * 平台下发留言
 */
public class SmsMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("SmsMessageReceiver ok");
        String action = intent.getAction();
        if (SHOW_SCREEN_SMS.equals(action)) {
            Bundle bundle = intent.getExtras();
            String uuid = bundle.getString("uuid");
            String smsType = bundle.getString("smsType");
            Intent intent1=new Intent(context, DialogActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("uuid",uuid);
            intent1.putExtra("smsType",smsType);
            context.startActivity(intent1);
        }
    }
}