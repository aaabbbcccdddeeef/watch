package com.wisdomin.studentcard.broadcast;

/**
 * 广播的常量
 */
public class BroadcastConstant {


    public static final String SHOW_SCREEN_SMS = "com.wisdomin.studentcard.SHOW_SCREEN_SMS";// 屏幕显示  短消息
    public static final String PHONE_STATE = "com.wisdomin.studentcard.phone_state";// 屏幕显示  短消息
    public static final String BUTTERY_STATE = "com.wisdomin.studentcard.buttery";//电量
    public static final String LASTSTRENGTH_STATE = "com.wisdomin.studentcard.lastStrength";//信号强度
    public static final String SMS_STATE = "com.wisdomin.studentcard.sms";//收到系统短信
    //重启
    public static final String REBOOT = "com.wisdomin.studentcard.action.reboot";//
    //关机
    public static final String SHUT_DOWN = "com.wisdomin.studentcard.action.shut_down";//
    //UPDATE
    public static final String UPDATE = "com.wisdomin.studentcard.action.update";//
    //测温的广播
    public static final String TEMPERATURE_START = "com.gwl.temperature.action.start";//
    public static final String TEMPERATURE_RESULT = "com.gwl.temperature.action.result";//
    public static final String TEMPERATURE_RESULT_POST = "com.gwl.temperature.action.result.post";//

    //gps
    public static final String GPS = "com.wisdomin.studentcard.gps";//
    public static final String GPS_GET = "com.wisdomin.studentcard.gps_get";//

    //心跳
    public static final String HEART_BEAT = "com.wisdomin.studentcard.heart";//


    //限制呼出
    public static final String REJECT_CALL_OUT = "com.wisdomin.studentcard.REJECT_CALL_OUT";//

    //启动
    public static final String BOOT_STATE = "com.wisdomin.studentcard.boot";
}
