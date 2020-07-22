package com.wisdomin.studentcard.util;

/**
 * Created by dgl on 08/04/2019.
 */

public class AppConst {

    /**
     * 模式
     * 0待机模式
     * 1省电模式
     * 2平衡模式
     * 3实时模式
     */
    public static final String MODEL_AWAIT = "0";//待机模式
    public static final String MODEL_POWER_SAVING = "1";//省电模式
    public static final String MODEL_BALANCE = "2";//平衡模式
    public static final String MODEL_REAL_TIME = "3";//实时模式


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
    public static final String REPORT_HEALTH = "REPORT_HEALTH";//上报 温度 心率
    public static final String REPORT_DEVICE_INFO = "REPORT_DEVICE_INFO";//设备参数上报
    //下发
    public static final String REMOTE_OPERATE_TERMINAL = "REMOTE_OPERATE_TERMINAL";//终端设备服务调用
    public static final String SET_NORMAL_BUTTON = "SET_NORMAL_BUTTON";//设置普通按键与SOS号码
    public static final String FREQUENCY_LOCATION_SET = "FREQUENCY_LOCATION_SET";//位置上报频率下发
    public static final String SET_REGIONAL_ALARM = "SET_REGIONAL_ALARM";//设置区域报警
    public static final String SET_LOCATION_MODE = "SET_LOCATION_MODE";//设置定位模式
    public static final String SET_REDAY_MODE = "SET_REDAY_MODE";//设置待机模式
    public static final String SET_INCOMING_CALL = "SET_INCOMING_CALL";//设置呼入号码 白名单
    public static final String SET_SERVER_INFO = "SET_SERVER_INFO";//设置服务信息
    public static final String LOCATION_INFO_GET = "LOCATION_INFO_GET";//实时位置获取
    public static final String SET_HEARTBEAT = "SET_HEARTBEAT";//设置终端心跳
    public static final String SET_MODEL = "SET_MODEL";//设置情景模式
    public static final String REQUEST_CALL = "REQUEST_CALL";//安全防护
    public static final String SET_CLASS_MODEL = "SET_CLASS_MODEL";//课堂模式
    public static final String SET_CLOCK = "SET_CLOCK";//设置闹铃
    public static final String SET_CALL_DURATION = "SET_CALL_DURATION";//设置通话时长
    public static final String SEND_SMS = "SEND_SMS";//短消息
    public static final String SET_HEALTH = "SET_HEALTH";//设置心率，温度的上报频率
    public static final String SET_DEVICE_INFO = "SET_DEVICE_INFO";//设备参数下发

    //设备请求
    public static final String GET_NORMAL_BUTTON = "GET_NORMAL_BUTTON";//按键获取
    public static final String GET_CLASS_MODEL = "GET_CLASS_MODEL";//课堂模式获取
    public static final String GET_INCOMING_CALL = "GET_INCOMING_CALL";//呼入号码获取 白名单
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

    //登陆是否成功
    public static boolean LOGIN_SUCCESS = false;

    //设置标记：检测到设备开机的发送上报
    public static boolean BOOTBROADCAST = false;

    //设置标记：更新apk
    //待机模式时候，添加标记，先连接平台，连接成功后，在更新apk，更新完apk，再次进去待机模式
    public static boolean TO_UPDATE = false;
    //设置标记：更新apk
    public static String TO_UPDATE_TIME = "";


    //平台下发获取测温数据时候，设置标记，测温结束后，标记复位
    public static String ISSUED_TEM_WATERNUMBER = "";
    public static boolean BY_STUDENT = false;
}
