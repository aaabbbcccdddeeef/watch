package com.wisdomin.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.util.AppConst;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //网络状态改变
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                LogUtil.d("net changed connected");
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                        || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE
                        || activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    //当没连接，不是待机模式时候，去链接
                    if(!BaseSDK.getInstance().getConnectStatus()){
                        String locationModeNow = PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE);
                        if(!locationModeNow.equals(AppConst.MODEL_AWAIT)){//待机模式时候，不链接平台
                            BaseSDK.getInstance().connect();
                        }
                    }
                }
            } else {
                LogUtil.d("net changed  disconnected");
                BaseSDK.getInstance().stopTcp();
            }
        }

    }
}