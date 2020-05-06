package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.netty.NettyClient;
import com.ctop.studentcard.util.Const;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PackDataUtil;

public class TemReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("TemReceiver");
        String action = intent.getAction();
        if (action.equals(BroadcastConstant.TEMPERATURE_RESULT)) {
            String valueTem = intent.getExtras().getString("value");
            LogUtil.e("valueTem===" + valueTem);
            if (!TextUtils.isEmpty(Const.ISSUED_TEM_WATERNUMBER)) {//下发：获取实时温度
                String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), Const.ISSUED_TEM_WATERNUMBER, Const.SET_HEALTH,
                        Const.RESPONSE_OF_ISSUED, "0@21@"+valueTem);
                NettyClient.getInstance(context).sendMsgToServer(str, null);
                Const.ISSUED_TEM_WATERNUMBER = "";
            } else {//上报：实时温度
                //上报
                BaseSDK.getInstance().sendHealth("0@" + valueTem + "@0");
                //测温页面显示温度
                Intent intent1 = new Intent();
                intent.putExtra("value", valueTem);
                intent1.setAction(BroadcastConstant.TEMPERATURE_RESULT_POST);
                context.sendBroadcast(intent1);// 发送
            }

        }
    }
}