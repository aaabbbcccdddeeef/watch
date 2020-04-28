package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ctop.studentcard.util.LogUtil;

/**
 * 呼出电话，对方接听时候，系统发广播，这里接收。
 */
public class CallActiveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.e("com.ctop.studentcard.call.active");
        sendBrocastHook(context, "hook");



    }

    //发送广播 接通
    private void sendBrocastHook(Context context, String state) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.PHONE_STATE);
        intent.putExtra("state", state);
        context.sendBroadcast(intent);// 发送
    }


}
