package com.wisdomin.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.NetworkUtil;

import java.util.List;

public class WifiReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        LogUtil.e("WifiReceiver=="+intent.getAction());
        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {//当扫描结果后，进行刷新列表
            refreshLocalWifiListData(context);
//        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接网络状态变化
//            refreshLocalWifiListData(context);
        }
    }

    public void refreshLocalWifiListData(Context context) {
        //得到扫描结果
        NetworkUtil.mWifiList.clear();
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> tmpList = mWifiManager.getScanResults();
        for (ScanResult tmp : tmpList) {
            NetworkUtil.mWifiList.add(tmp);
        }
//        LogUtil.e("mWifiList.size=="+ NetworkUtil.mWifiList.size());
    }

}