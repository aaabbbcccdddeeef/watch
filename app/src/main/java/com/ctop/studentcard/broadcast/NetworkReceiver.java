package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ctop.studentcard.util.LogUtil;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //网络状态改变
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                LogUtil.d("activeNetwork != null");
                LogUtil.d("activeNetwork:" + activeNetwork.getType());
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                        || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE
                        || activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
//                    connect();
                }
            } else {
                LogUtil.d("activeNetwork == null");
            }
        }

    }
}