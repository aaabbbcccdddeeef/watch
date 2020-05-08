package com.ctop.studentcard.broadcast;

/**
 * 广播的常量
 */
public class BroadcastConstant {


    public static final String SHOW_SCREEN_SMS = "com.ctop.studentcard.SHOW_SCREEN_SMS";// 屏幕显示  短消息
    public static final String PHONE_STATE = "com.ctop.studentcard.phone_state";// 屏幕显示  短消息
    public static final String BUTTERY_STATE = "com.ctop.studentcard.buttery";//电量
    public static final String LASTSTRENGTH_STATE = "com.ctop.studentcard.lastStrength";//信号强度
    public static final String SMS_STATE = "com.ctop.studentcard.sms";//收到系统短信
    //重启
    public static final String REBOOT = "com.ctop.studentcard.action.reboot";//
    //关机
    public static final String SHUT_DOWN = "com.ctop.studentcard.action.shut_down";//
    //UPDATE
    public static final String UPDATE = "com.ctop.studentcard.action.update";//
    //测温的广播
    public static final String TEMPERATURE_START = "com.gwl.temperature.action.start";//
    public static final String TEMPERATURE_RESULT = "com.gwl.temperature.action.result";//
    public static final String TEMPERATURE_RESULT_POST = "com.gwl.temperature.action.result.post";//

    //gps
    public static final String GPS = "com.ctop.studentcard.gps";//
    public static final String GPS_GET = "com.ctop.studentcard.gps_get";//


}
