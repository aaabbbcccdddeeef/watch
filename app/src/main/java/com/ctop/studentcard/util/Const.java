package com.ctop.studentcard.util;

/**
 * Created by dgl on 08/04/2019.
 */

public class Const {

    /**
     * 接口标识
     */

    //上报
    public static final String LOGIN = "DEVICE_LOGIN";//登陆
    public static final String ALARM_POWER = "ALARM_POWER";//电量上报
    public static final String REPORT_LOCATION_INFO = "REPORT_LOCATION_INFO";//位置信息上报
    public static final String REPORT_HEARTBEAT = "REPORT_HEARTBEAT";//心跳保活
//    public static final String TURN_ONOFF_TERMINAL = "TURN_ONOFF_TERMINAL";//设备开关机报警
    public static final String REPORT_CROSS_BORDER = "REPORT_CROSS_BORDER";//越界上报
    public static final String REPORT_CALL_LOG = "REPORT_CALL_LOG";//通话记录上报
    public static final String REPORT_SOS = "REPORT_SOS";//SOS 触发报警上报
    public static final String DEVICE_STATUS = "DEVICE_STATUS";//设备模式上报
    public static final String REPORT_SMS_READ = "REPORT_SMS_READ";//短消息已阅上报
    //下发
    public static final String REMOTE_OPERATE_TERMINAL = "REMOTE_OPERATE_TERMINAL";//终端设备服务调用
    public static final String SET_NORMAL_BUTTON = "SET_NORMAL_BUTTON";//设置普通按键与SOS号码
    public static final String FREQUENCY_LOCATION_SET = "FREQUENCY_LOCATION_SET";//位置上报频率下发
    public static final String SET_REGIONAL_ALARM = "SET_REGIONAL_ALARM";//设置区域报警
    public static final String SET_LOCATION_MODE = "SET_LOCATION_MODE";//设置定位模式
    public static final String SET_REDAY_MODE = "SET_REDAY_MODE";//设置待机模式
    public static final String SET_INCOMING_CALL = "SET_INCOMING_CALL";//设置呼入号码
    public static final String SET_SERVER_INFO = "SET_SERVER_INFO";//设置服务信息
    public static final String LOCATION_INFO_GET = "LOCATION_INFO_GET";//实时位置获取
    public static final String SET_HEARTBEAT = "SET_HEARTBEAT";//设置终端心跳
    public static final String SET_MODEL = "SET_MODEL";//设置情景模式
    public static final String REQUEST_CALL = "REQUEST_CALL";//安全防护
    public static final String SET_CLASS_MODEL = "SET_CLASS_MODEL";//课堂模式
    public static final String SET_CLOCK = "SET_CLOCK";//设置闹铃
    public static final String SET_CALL_DURATION = "SET_CALL_DURATION";//设置通话时长
    public static final String SEND_SMS = "SEND_SMS";//短消息

    //设备请求
    public static final String GET_NORMAL_BUTTON = "GET_NORMAL_BUTTON";//按键获取
    public static final String GET_CLASS_MODEL = "GET_CLASS_MODEL";//课堂模式获取
    public static final String GET_INCOMING_CALL = "GET_INCOMING_CALL";//呼入号码获取
    public static final String GET_SMS_PORT = "GET_SMS_PORT";//端口获取
    public static final String GET_SERVICE_MSG = "GET_SERVICE_MSG";//更新apk



    //自定义接口
    public static final String DEVICE_DATA_TRANSPORT = "DEVICE_DATA_TRANSPORT";//设备自定义

    /**
     * 报文类型
     * 1.下发请求
     * 2.下发响应
     * 3.上报请求
     * 4.上报响应
     */
    public static final String ISSUED_THE_REQUEST = "1";//(下)服务器主动下发
    public static final String RESPONSE_OF_ISSUED = "2";//(上)响应服务器的下发
    public static final String REPORT_THE_REQUEST = "3";//(上)上报请求
    public static final String RESPONSE_OF_REPORT = "4";//(下)上报的响应

    //设置标记：检测到设备开机的发送上报
    public static boolean BOOTBROADCAST = false;
    //登陆是否成功
    public static boolean LOGIN_SUCCESS = true;

}
