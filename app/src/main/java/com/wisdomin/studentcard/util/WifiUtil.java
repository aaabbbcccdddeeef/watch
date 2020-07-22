package com.wisdomin.studentcard.util;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiUtil {

    //关闭wifi
    public static void closeWifi(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);
    }

    //打开wifi
    public static void openWifi(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }
}
