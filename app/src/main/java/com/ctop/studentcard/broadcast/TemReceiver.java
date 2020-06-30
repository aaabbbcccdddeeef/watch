package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.feature.step.StepUtils;
import com.ctop.studentcard.greendao.DaoManager;
import com.ctop.studentcard.greendao.TemBean;
import com.ctop.studentcard.greendao.TemBeanDao;
import com.ctop.studentcard.netty.NettyClient;
import com.ctop.studentcard.util.AppConst;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PackDataUtil;
import com.ctop.studentcard.util.TimeUtils;

public class TemReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("TemReceiver");
        String action = intent.getAction();
        if (action.equals(BroadcastConstant.TEMPERATURE_RESULT)) {
            String valueTem = intent.getExtras().getString("value");
            LogUtil.e("valueTem===" + valueTem);
            if (!TextUtils.isEmpty(AppConst.ISSUED_TEM_WATERNUMBER)) {//下发：获取实时温度
                String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), AppConst.ISSUED_TEM_WATERNUMBER, AppConst.SET_HEALTH,
                        AppConst.RESPONSE_OF_ISSUED, "0@21@" + valueTem);
                NettyClient.getInstance(context).sendMsgToServer(str, null);
                AppConst.ISSUED_TEM_WATERNUMBER = "";
                //存本地数据库
                TemBeanDao temBeanDao = DaoManager.getInstance().getDaoSession().getTemBeanDao();
                temBeanDao.insert(new TemBean(valueTem, 1, System.currentTimeMillis()));
            } else {//上报：实时温度
                //上报
                BaseSDK.getInstance().sendHealth(
                        TimeUtils.getNowTimeString(TimeUtils.format4) + "-" + TimeUtils.getNowTimeString(TimeUtils.format4) +
                                "@0@" + valueTem + "@" + StepUtils.getStep());
                //测温页面显示温度
                Intent intentTem = new Intent();
                intentTem.putExtra("value", valueTem);
                intentTem.setAction(BroadcastConstant.TEMPERATURE_RESULT_POST);
                context.sendBroadcast(intentTem);// 发送
                if (AppConst.BY_STUDENT) {
                    //存本地数据库
                    TemBeanDao temBeanDao = DaoManager.getInstance().getDaoSession().getTemBeanDao();
                    temBeanDao.insert(new TemBean(valueTem, 0, System.currentTimeMillis()));
                    AppConst.BY_STUDENT = false;
                } else {
                    //存本地数据库
                    TemBeanDao temBeanDao = DaoManager.getInstance().getDaoSession().getTemBeanDao();
                    temBeanDao.insert(new TemBean(valueTem, 1, System.currentTimeMillis()));
                }
            }
        }
    }
}