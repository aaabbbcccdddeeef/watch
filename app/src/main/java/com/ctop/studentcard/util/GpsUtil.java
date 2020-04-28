package com.ctop.studentcard.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

import java.util.Iterator;

public class GpsUtil {

    public static int signalCont = 0;
    //定义LocationManage
    private static LocationManager locationManager;

    @SuppressLint("MissingPermission")
    public static void getInstence(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //监听卫星信号强度
        locationManager.addGpsStatusListener(statusListener);

    }

    /**
     * 卫星信号强度监听
     * 第一种：监听卫星信噪比
     */

    static GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //获取当前状态
                    @SuppressLint("MissingPermission") GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();

                    //获取所有的卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    //卫星颗数统计
                    int count = 0;
                    signalCont = 0;//每监听一次对之前的信号清0
                    while (iters.hasNext() && count <= maxSatellites) {
                        count++;
                        GpsSatellite s = iters.next();
                        if (s.usedInFix()) {//有效卫星
                            signalCont++;
                        }
                        //卫星的信噪比
                        float snr = s.getSnr();
//                        signalCont = signalCont + snr;
//                        LogUtil.e("signalCont==="+signalCont);
                    }
                    break;
                default:
                    break;
            }
        }
    };


}
