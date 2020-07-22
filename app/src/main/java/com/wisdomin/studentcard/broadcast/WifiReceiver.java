package com.wisdomin.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.feature.step.StepUtils;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.NetworkUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;
import com.wisdomin.studentcard.util.TimeUtils;

import java.util.Arrays;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        LogUtil.e("WifiReceiver==" + intent.getAction());
        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {//当扫描结果后，进行刷新列表
            refreshLocalWifiListData(context);
//        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接网络状态变化
//            refreshLocalWifiListData(context);
        }
    }

    public void refreshLocalWifiListData(Context context) {
        String homeWifis = PreferencesUtils.getInstance(context).getString("homeWifis", "");
        String schoolWifis = PreferencesUtils.getInstance(context).getString("schoolWifis", "");
        boolean at_home = PreferencesUtils.getInstance(context).getBoolean("at_home", false);//在家
        boolean at_school = PreferencesUtils.getInstance(context).getBoolean("at_school", false);//在校
        String home_string = "";
        String school_string = "";
        //得到扫描结果
        NetworkUtil.mWifiList.clear();
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        for (ScanResult scanResult : scanResults) {
            NetworkUtil.mWifiList.add(scanResult);
            if (!"".equals(scanResult.SSID)) {
                if (homeWifis.contains(scanResult.SSID)) {
                    home_string = "1!" + scanResult.SSID + "!" + scanResult.level;
                }
                if (schoolWifis.contains(scanResult.SSID)) {
                    school_string = "3!" + scanResult.SSID + "!" + scanResult.level;
                }
            }
        }

        if (!"".equals(home_string) && !at_home) {//到家
            PreferencesUtils.getInstance(context).setBoolean("at_home", true);
            BaseSDK.getInstance().sendDevice(home_string);
        }
        if ("".equals(home_string) && at_home) {//离家
            PreferencesUtils.getInstance(context).setBoolean("at_home", false);
            BaseSDK.getInstance().sendDevice("2!0!0");
        }

        if (!"".equals(school_string) && !at_school) {//到校
            PreferencesUtils.getInstance(context).setBoolean("at_school", true);
            BaseSDK.getInstance().sendDevice(school_string);
        }
        if ("".equals(school_string) && at_school) {//离校
            PreferencesUtils.getInstance(context).setBoolean("at_school", false);
            BaseSDK.getInstance().sendDevice("4!0!0");
        }
    }

}