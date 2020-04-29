package com.ctop.studentcard.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.ctop.studentcard.base.BaseApplication;
import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.bean.PhoneNumber;
import com.ctop.studentcard.broadcast.BroadcastConstant;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;


import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

public class DeviceUtil {

//    public static String getBattery() {
//        String battery = "";
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                BatteryManager manager = (BatteryManager) BaseSDK.getBaseContext().getSystemService(BATTERY_SERVICE);
//                manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
//                manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
//                manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
//                battery = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%";///当前电量百分比
//            }else {
//                BatteryManager batteryManager = (BatteryManager)BaseSDK.getBaseContext().getSystemService(BATTERY_SERVICE);
////                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//                battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%";///当前电量百分比
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if ("".equals(battery))
//                battery = "100%";
//        }
//
//        return battery;
//    }

    /**
     * 实时获取电量
     */
    public static String getBattery() {
        int level = 0;
        Intent batteryInfoIntent = BaseApplication.getAppContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        level = batteryInfoIntent.getIntExtra("level", 0);
        int batterySum = batteryInfoIntent.getIntExtra("scale", 100);
        int percentBattery = 100 * level / batterySum;
        return percentBattery + "%";
    }


    /**
     * 获取手机的IMEI号码
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission")
        String ret = mTm.getDeviceId();
//        String ret = "260696040000202";
        return ret;
    }

    /**
     * 获取手机的序列号
     */
    public static String getSimSerialNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String simSerialNumber = telephonyManager.getSimSerialNumber();
        return simSerialNumber;
    }

    public static String getMacAddress() {
        /*获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回
        "02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了
        getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。*/
        //        String macAddress= "";
//        WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        macAddress = wifiInfo.getMacAddress();
//        return macAddress;

        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }


    //    /**获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的*/
//    public static String getIPAddress(Context context) {
//        NetworkInfo info = ((ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
//        if (info != null && info.isConnected()) {
//            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
//                try {
//                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
//                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
//                        NetworkInterface intf = en.nextElement();
//                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
//                            InetAddress inetAddress = enumIpAddr.nextElement();
//                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
//                                return inetAddress.getHostAddress();
//                            }
//                        }
//                    }
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
//            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
//                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                //调用方法将int转换为地址字符串
//                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
//                return ipAddress;
//            }
//        } else {
//            //当前无网络连接,请在设置中打开网络
//        }
//        return null;
//    }
//
//    /**
//     * 将得到的int类型的IP转换为String类型
//     *
//     * @param ip
//     * @return
//     */
//    public static String intIP2StringIP(int ip) {
//        return (ip & 0xFF) + "." +
//                ((ip >> 8) & 0xFF) + "." +
//                ((ip >> 16) & 0xFF) + "." +
//                (ip >> 24 & 0xFF);
//    }

    /**
     * 关机
     */
    public static void shutDowm() {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SHUT_DOWN);
        BaseApplication.getAppContext().sendBroadcast(intent);// 发送

//        LogUtil.v("shutDowm");
//        try {
//            //获得ServiceManager类
//            Class ServiceManager = Class
//                    .forName("android.os.ServiceManager");
//            //获得ServiceManager的getService方法
//            Method getService = ServiceManager.getMethod("getService", String.class);
//            //调用getService获取RemoteService
//            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);
//            //获得IPowerManager.Stub类
//            Class cStub = Class.forName("android.os.IPowerManager$Stub");
//            //获得asInterface方法
//            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
//            //调用asInterface方法获取IPowerManager对象
//            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
//            //获得shutdown()方法
//            Method shutdown = oIPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
//            //调用shutdown()方法
//            shutdown.invoke(oIPowerManager, false, true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    //重启
    public static void reboot(Context context) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.REBOOT);
        BaseApplication.getAppContext().sendBroadcast(intent);// 发送
    }


    public static void callSOSPhone(Context context) {
        String phoneNumberString = PreferencesUtils.getInstance(context).getString("phoneNumber", "");
        PhoneNumber phoneNumber = JsonUtil.parseObject(phoneNumberString, PhoneNumber.class);
        if (phoneNumber != null) {
            //如果需要手动拨号将Intent.ACTION_CALL改为Intent.ACTION_DIAL（跳转到拨号界面，用户手动点击拨打）
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNumber.getSosNumber());
            intent.setData(data);
            context.startActivity(intent);
        }

    }

    /**
     * RINGER_MODE_SILENT 静音,且无振动
     * RINGER_MODE_VIBRATE 静音,但有振动
     * RINGER_MODE_NORMAL 正常声音,振动开关由setVibrateSetting决定.
     */
    //静音
    public static void silentSwitchOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            LogUtil.d("RINGING 已被静音");
        }
    }

    //响铃
    public static void silentSwitchOff(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            LogUtil.d("RINGING 取消静音");
        }
    }

    @SuppressLint("MissingPermission")
    public static boolean noSim(Context paramContext) {
        return TextUtils.isEmpty(getSimSerialNumber(paramContext));
    }


    public static int getVersionCode() {
        return BaseApplication.getPackageInfo().versionCode;
    }

    public static String getVersionName() {
        return BaseApplication.getPackageInfo().versionName;
    }


    /**
     * 根据手机的分辨率从  dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     * @author hexiaodong
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
