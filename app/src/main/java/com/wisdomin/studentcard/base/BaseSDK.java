package com.wisdomin.studentcard.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.wisdomin.studentcard.api.OnReceiveListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.wisdomin.studentcard.api.Iback;
import com.wisdomin.studentcard.api.InstructionCallBack;
import com.wisdomin.studentcard.bean.ClassModel;
import com.wisdomin.studentcard.bean.ClickBean;
import com.wisdomin.studentcard.bean.ContextualModel;
import com.wisdomin.studentcard.bean.IncomingCall;
import com.wisdomin.studentcard.bean.PhoneNumber;
import com.wisdomin.studentcard.bean.RegionalAlarmEntity;
import com.wisdomin.studentcard.bean.RegionalReturn;
import com.wisdomin.studentcard.bean.RequestContent;
import com.wisdomin.studentcard.bean.SmsMessageReceive;
import com.wisdomin.studentcard.bean.TemFrequency;
import com.wisdomin.studentcard.broadcast.BroadcastConstant;
import com.wisdomin.studentcard.feature.step.StepUtils;
import com.wisdomin.studentcard.greendao.DaoManager;
import com.wisdomin.studentcard.greendao.SmsMessage;
import com.wisdomin.studentcard.greendao.SmsMessageDao;
import com.wisdomin.studentcard.netty.ChannelListener;
import com.wisdomin.studentcard.netty.NettyClient;
import com.wisdomin.studentcard.netty.SendBack;
import com.wisdomin.studentcard.service.APKDownloadService;
import com.wisdomin.studentcard.util.AppConst;
import com.wisdomin.studentcard.util.DeviceUtil;
import com.wisdomin.studentcard.util.GpsUtil;
import com.wisdomin.studentcard.util.JsonUtil;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.NetworkUtil;
import com.wisdomin.studentcard.util.PackDataUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;
import com.wisdomin.studentcard.util.PropertiesUtil;
import com.wisdomin.studentcard.util.SmsManagerUtils;
import com.wisdomin.studentcard.util.TimeUtils;
import com.wisdomin.studentcard.util.UIUtil;
import com.wisdomin.studentcard.util.ai.KdxfSpeechSynthesizerUtil;

/**
 * 智汇的基础sdk
 *
 * @author dgl
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class BaseSDK implements ChannelListener {

    private static Context mContext;
    private static BaseSDK baseSDK;
    //    private IAuthCallBack mAuthCallBack;
    //map中键 放的是流水号
    private ArrayMap<String, OnReceiveListener> listenerArrayMap = new ArrayMap<>();
    SendBack sendBack;
//    private NetworkReceiver networkReceiver;

    public int period;
    public int period_heart = 5 * 60;

    private int count_send_login = 0;
    AlarmManager alarmManager;

    public boolean await_stoptcp = false;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                timingLocation();//位置上报频率下发
            } else if (msg.what == 1) {
                timingLocationGet();//位置信息定时上传
            } else if (msg.what == 2) {
                timingLocationInit(0);
            } else if (msg.what == 3) {
                new CountDownTimer(120000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        count_send_login += 1;
                        LogUtil.e("2分钟倒计时结束，重新登陆");
                        //组装报文
                        String waterNumber = PackDataUtil.createWaterNumber();
                        String login_code = PropertiesUtil.getInstance().getLoginString(mContext);
                        String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.LOGIN, AppConst.REPORT_THE_REQUEST, login_code);
                        NettyClient.getInstance(mContext).userAuth(request, null);
                    }
                }.start();
            } else if (msg.what == 4) {
                new CountDownTimer(5 * 60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        LogUtil.e("5分钟倒计时结束，重新登陆");
                        //组装报文
                        String waterNumber = PackDataUtil.createWaterNumber();
                        String login_code = PropertiesUtil.getInstance().getLoginString(mContext);
                        String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.LOGIN, AppConst.REPORT_THE_REQUEST, login_code);
                        NettyClient.getInstance(mContext).userAuth(request, null);
                    }
                }.start();
            } else if (msg.what == 5) {
                String data = (String) msg.obj;


            } else if (msg.what == 6) {
                timingLocationInit(period);
            } else if (msg.what == 7) {
                NettyClient.getInstance(mContext).connect();
            } else if (msg.what == 8) {

            }
        }
    };

    private BaseSDK() {
    }

    public static BaseSDK getInstance() {
        if (mContext == null) {
            return null;
        }
//        if(!NetworkUtil.isAvailable(mContext)){
//            return null;
//        }
        if (baseSDK == null) {
            baseSDK = new BaseSDK();
        }
        return baseSDK;
    }

    static {
        LogUtil.setShow(true);//关闭日志
    }

    public static Context getBaseContext() {
        return mContext;
    }

    public static void setBaseContext(Context context) {
        mContext = context;
    }

    public synchronized void init(Context context) {
//        if(!NetworkUtil.isAvailable(context)){
//            return;
//        }
        if (baseSDK == null) {
            return;
        }
//        mContext = context;

        connect();
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }


    public void connect() {
        LogUtil.e("go to connect");
        NettyClient.getInstance(mContext).setListener(this);
        if (!NettyClient.getInstance(mContext).getConnectStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyClient.getInstance(mContext).connect();//连接服务器
                }
            }).start();
        }
    }

    @Override
    public void onServiceStatusConnectChanged(int statusCode) {
        LogUtil.e("tatusChanged:thread:name:" + Thread.currentThread().getName());
        if (statusCode == ChannelListener.STATUS_CONNECT_SUCCESS) {
            LogUtil.d("tcp connect:" + PropertiesUtil.getInstance().getHost(mContext) + ":" +
                    PropertiesUtil.getInstance().getTcp_port(mContext));
            LogUtil.d("tcp connect status: success");
            System.out.println("sdk : tcp connect status: success");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    authenticData();
                }
            }).start();

        } else {
            LogUtil.d("tcp connect status: failed");
            System.out.println("sdk : tcp connect status: failed");
            //如果是主动断开，不用重连
            boolean setstopTcp = PreferencesUtils.getInstance(mContext).getBoolean("stopTcp", false);
            if (!setstopTcp) {
                LogUtil.d("setstopTcp =false: connect");
                handler.sendEmptyMessage(7);
            }
        }
    }

    /**
     * 认证数据请求
     */
    private void authenticData() {
        LogUtil.e("authenticData:thread:name:" + Thread.currentThread().getName());
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        String login_code = PropertiesUtil.getInstance().getLoginString(mContext);
        String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.LOGIN, AppConst.REPORT_THE_REQUEST, login_code);
        NettyClient.getInstance(mContext).userAuth(request, null);
    }


    private void timingLocationInit(int per) {
        LogUtil.e("timingLocationInit ***");
        //先取消上一个任务，防止重复的任务
        canalAlarm(mContext, BroadcastConstant.GPS, 0);
        if (per == 0) {
            period = 20 * 60;
        } else {
            period = per;
        }
        LogUtil.e("LOCATION_init===" + period);
        LogUtil.e("LOCATION_initialDelay===" + initialDelay);

        setAlarmTime(mContext, System.currentTimeMillis(), BroadcastConstant.GPS, initialDelay, 0);
    }

    public void heartInit(int per) {
        LogUtil.e("heartInit ***");
        //先取消上一个任务，防止重复的任务
        canalAlarm(mContext, BroadcastConstant.HEART_BEAT, 1);
        if (per == 0) {
            period_heart = 5 * 60;
        } else {
            period_heart = per;
        }
        LogUtil.e("period_heart_init===" + period);

        setAlarmTime(mContext, System.currentTimeMillis() + (5 * 60 * 1000), BroadcastConstant.HEART_BEAT, 0, 1);
    }

    private void timingLocation() {
        LogUtil.e("timingLocation ***");
        //先取消上一个任务，防止重复的任务
        canalAlarm(mContext, BroadcastConstant.GPS, 0);
        setAlarmTime(mContext, System.currentTimeMillis(), BroadcastConstant.GPS, initialDelay, 0);
    }

    private void timingLocationGet() {
        setAlarmTime(mContext, System.currentTimeMillis(), BroadcastConstant.GPS_GET, initialDelay, 0);
    }

    int initialDelay = 0;

    private void executor(final String locationInfo) {
        GPS_ING = false;
        reportLocationInfo(locationInfo, new OnReceiveListener() {
            @Override
            public void onResponse(String msg) {
            }
        });
        if (await_stoptcp) {
            await_stoptcp = false;
            stopTcp();
        }
        //定位结束，关闭gps
        GpsUtil.closeGPSSettings(mContext);
    }

    private void executorGet(final String locationInfo) {
        GPS_GET_ING = false;
        reportLocationInfoGet(locationInfo, new OnReceiveListener() {
            @Override
            public void onResponse(String msg) {
            }
        });
        //定位结束，关闭gps
        GpsUtil.closeGPSSettings(mContext);
    }

    //上报位置信息
    public void reportLocationInfo(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.REPORT_LOCATION_INFO, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
    }

    //响应平台的 获取实时位置
    public void reportLocationInfoGet(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PreferencesUtils.getInstance(mContext).getString("LOCATION_INFO_GET", "");
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.LOCATION_INFO_GET, AppConst.RESPONSE_OF_ISSUED, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
    }

    //电量报警
    public void sendAlarmPower(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.ALARM_POWER, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }

    //开机，关机
    public void sendBootAndShutdown(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.ALARM_POWER, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }

    //自定义
    public void sendCustom(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.DEVICE_DATA_TRANSPORT, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }

    //越界上报
    public void sendReport_cross_border(double latitude, double longitude) {
        //判断是否越界
        RegionalReturn regionalReturn = RegionalReturn.isCrossBorder(mContext, latitude, longitude, lastLatitude, lastLongitude);
        if (regionalReturn != null) {
            //组装报文  父母卡类型@进入某区域或离开某区域@位置信息@区域编号
            String data = regionalReturn.getCardType() + "@" + regionalReturn.getInOrOut() + "@" + regionalReturn.getGpsString() + "@" + regionalReturn.getArea();
            final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.REPORT_CROSS_BORDER, AppConst.REPORT_THE_REQUEST, data);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyClient.getInstance(mContext).sendMsgToServer(request, null);
                }
            }).start();
        }
    }

    //通话记录上报
    public void sendCallRecord(String data) {
        if (!TextUtils.isEmpty(data)) {
            //组装报文
            final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.REPORT_CALL_LOG, AppConst.REPORT_THE_REQUEST, data);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyClient.getInstance(mContext).sendMsgToServer(request, null);
                }
            }).start();
        }
    }

    //发送心跳
    public void send_report_heart() {
        //组装报文
        final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.REPORT_HEARTBEAT, AppConst.REPORT_THE_REQUEST, DeviceUtil.getBattery() + "@" + StepUtils.getStep() + "@");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, null);
            }
        }).start();
    }

    //SOS 触发报警上报
    public void send_report_sos() {
        //组装报文
        final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.REPORT_SOS, AppConst.REPORT_THE_REQUEST, "1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, null);
            }
        }).start();
    }

    //设备模式上报
    public void send_device_status(String mode) {
        //组装报文
        //切换前模式@切换后模式@切换时间（格林威治时间到现在毫秒数）@已在之前模式下工作时长（分钟）
        String locationModeOld = PreferencesUtils.getInstance(mContext).getString("locationModeOld", AppConst.MODEL_BALANCE);
        long startMode = PreferencesUtils.getInstance(mContext).getLong("locationModeStart", 0l);
        long durationOld = (System.currentTimeMillis() - startMode) / 60000;
        String data = locationModeOld + "@" + mode + "@" + System.currentTimeMillis() + "@" + durationOld;
        final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.DEVICE_STATUS, AppConst.REPORT_THE_REQUEST, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, null);
            }
        }).start();
    }


    //短消息已阅上报
    public void send_report_sms_read(String data) {
        //组装报文
        final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.REPORT_SMS_READ, AppConst.REPORT_THE_REQUEST, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, null);
            }
        }).start();
    }


    //端口获取
    public void getSmsPort(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.GET_SMS_PORT, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }

    //更新版本
    public void geUpdate(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.GET_SERVICE_MSG, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }

    //呼入号码获取
    public void getIncomingCall(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.GET_INCOMING_CALL, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }

    //按键获取
    public void getNormalButton(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.GET_NORMAL_BUTTON, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }

    //课堂模式获取
    public void getClassModel(String data, final OnReceiveListener onReceiveListener) {
        //组装报文
        String waterNumber = PackDataUtil.createWaterNumber();
        final String request = PackDataUtil.packRequestStr(mContext, waterNumber, AppConst.GET_CLASS_MODEL, AppConst.REPORT_THE_REQUEST, data);
        listenerArrayMap.put(waterNumber, onReceiveListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, onReceiveListener);
            }
        }).start();
    }


    //上报 温度 心率
    public void sendHealth(String data) {
        //组装报文
        final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.REPORT_HEALTH, AppConst.REPORT_THE_REQUEST, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, null);
            }
        }).start();
    }

    //设备参数上报
    public void sendDevice(String data) {
        //组装报文
        final String request = PackDataUtil.packRequestStr(mContext, PackDataUtil.createWaterNumber(), AppConst.REPORT_DEVICE_INFO, AppConst.REPORT_THE_REQUEST, data);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyClient.getInstance(mContext).sendMsgToServer(request, null);
            }
        }).start();
    }


    //收到的消息
    @Override
    public void onMessageResponse(String msg) {
        LogUtil.e("onMessageResponse:Thread:name:" + Thread.currentThread().getName());
        //解析Response
        RequestContent response = PackDataUtil.parseResponse(mContext, msg);
        if (response != null) {
            String data = response.getData();
            data = data.replace("]", "");
            final String waterNumber = response.getWaterNumber();
            if (response.getMessageType().equals(AppConst.ISSUED_THE_REQUEST)) {//服务器主动下发
                if (response.getCmd().equals(AppConst.REMOTE_OPERATE_TERMINAL)) {//终端设备服务调用
                    //重启终端@恢复除平台地址以外的出厂设置@关机
                    //0=不执行，1=执行
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.REMOTE_OPERATE_TERMINAL, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                    String[] strings = data.split("@");
                    if (strings[1].equals("1")) {//恢复除平台地址以外的出厂设置
                        clearPreferences();

                    }
                    if (strings[0].equals("1")) {//重启终端
                        DeviceUtil.reboot(mContext);
                    }
                } else if (response.getCmd().equals(AppConst.SET_NORMAL_BUTTON)) {//设置普通按键与SOS号码
                    //0=13900000000!1=!2=13900000002!3=13900000003!4=13900000004!5=13900000005!
                    PhoneNumber phoneNumber = PhoneNumber.parseJson(data);
                    PreferencesUtils.getInstance(mContext).setString("phoneNumber", JsonUtil.toJSONString(phoneNumber));

                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_NORMAL_BUTTON, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.DEVICE_DATA_TRANSPORT)) {//自定义下发
                    sendBack.getSend(response.getData(), new Iback() {
                        @Override
                        public void returnToServer(String ret) {
                            String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.DEVICE_DATA_TRANSPORT, AppConst.RESPONSE_OF_ISSUED, ret);
                            NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                        }
                    });
                } else if (response.getCmd().equals(AppConst.SET_REGIONAL_ALARM)) {//设置区域报警
                    //取出本地数据
                    Set regionalAlarmEntityList = PreferencesUtils.getInstance(mContext).getStringSet("regionalAlarm", new HashSet<String>());
                    RegionalAlarmEntity regionalAlarmEntity = RegionalAlarmEntity.parseJson(data);
                    if (regionalAlarmEntity.getOperateType().equals("1")) {//增加
                        Set set1 = new HashSet(regionalAlarmEntityList);
                        set1.add(regionalAlarmEntity);
                        PreferencesUtils.getInstance(mContext).setStringSet("regionalAlarm", regionalAlarmEntityList);
                    } else if (regionalAlarmEntity.getOperateType().equals("2")) {//修改
                        Iterator<RegionalAlarmEntity> it = regionalAlarmEntityList.iterator();
                        while (it.hasNext()) {
                            RegionalAlarmEntity regionalAlarmEntity1 = it.next();
                            if (regionalAlarmEntity1.getArea().equals(regionalAlarmEntity1.getArea())) {
                                regionalAlarmEntityList.remove(regionalAlarmEntity1);
                                regionalAlarmEntityList.add(regionalAlarmEntity);
                                PreferencesUtils.getInstance(mContext).setStringSet("regionalAlarm", regionalAlarmEntityList);
                            }
                        }
                    } else if (regionalAlarmEntity.getOperateType().equals("3")) {//删除
                        Iterator<RegionalAlarmEntity> it = regionalAlarmEntityList.iterator();
                        while (it.hasNext()) {
                            RegionalAlarmEntity regionalAlarmEntity1 = it.next();
                            if (regionalAlarmEntity1.getArea().equals(regionalAlarmEntity1.getArea())) {
                                regionalAlarmEntityList.remove(regionalAlarmEntity1);
                                PreferencesUtils.getInstance(mContext).setStringSet("regionalAlarm", regionalAlarmEntityList);
                            }
                        }
                    }
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_REGIONAL_ALARM, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.SET_LOCATION_MODE)) {//定位模式
                    //定位模式@工作时长
                    // 1省电模式不上报位置
                    // 2平衡模式20min/次
                    // 3实时模式3min/次
//                    String[] strings = data.split("@");
//                    String type = strings[0];
//                    int num = Integer.parseInt(strings[1]);
                    if (data.equals(AppConst.MODEL_POWER_SAVING)) {
                        PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_POWER_SAVING);
                        PreferencesUtils.getInstance(mContext).setLong("locationModeStart", System.currentTimeMillis());
                        canalAlarm(mContext, BroadcastConstant.GPS, 0);
                    } else if (data.equals(AppConst.MODEL_BALANCE)) {
                        PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_BALANCE);
                        PreferencesUtils.getInstance(mContext).setLong("locationModeStart", System.currentTimeMillis());
                        period = 20 * 60;
                        canalAlarm(mContext, BroadcastConstant.GPS, 0);
                        setAlarmTime(mContext, System.currentTimeMillis(), BroadcastConstant.GPS, initialDelay, 0);
                    } else if (data.equals(AppConst.MODEL_REAL_TIME)) {
                        PreferencesUtils.getInstance(mContext).setString("locationModeOld", PreferencesUtils.getInstance(mContext).getString("locationMode", AppConst.MODEL_BALANCE));
                        PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_REAL_TIME);
                        PreferencesUtils.getInstance(mContext).setLong("locationModeStart", System.currentTimeMillis());
                        period = 3 * 60;
                        canalAlarm(mContext, BroadcastConstant.GPS, 0);
                        setAlarmTime(mContext, System.currentTimeMillis(), BroadcastConstant.GPS, initialDelay, 0);
                        //计算结束时间 realTime
                        long endTime = System.currentTimeMillis() + 30 * 60 * 1000;
                        PreferencesUtils.getInstance(mContext).setLong("realTimeModeEnd", endTime);
                    }
//                    handler.sendEmptyMessage(6);
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_LOCATION_MODE, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.SET_REDAY_MODE)) {//设置待机模式
                    //开关@待机时间段
                    // tcp断开
                    //默认2300-0600    1@2200-0600
                    //延时规则：如设备处于实时模式则延时至实时模式结束进入待机模式
                    String[] strings = data.split("@");
                    String[] startEnd = strings[1].split("-");
                    String type = strings[0];
                    if (type.equals("1")) {
//                        String locationModeNow = PreferencesUtils.getInstance(mContext).getString("locationMode", AppConst.MODEL_BALANCE);
//                        if (locationModeNow.equals(AppConst.MODEL_REAL_TIME)) {//实时模式
//                            PreferencesUtils.getInstance(mContext).setString("locationModeOld", AppConst.MODEL_AWAIT);
//                            return;
//                        }
//                        PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_AWAIT);
                        PreferencesUtils.getInstance(mContext).setString("WAKEUPTime","");
                        PreferencesUtils.getInstance(mContext).setString("awaitModeStart", startEnd[0]);
                        PreferencesUtils.getInstance(mContext).setString("awaitModeEnd", startEnd[1]);
//
                        String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_REDAY_MODE, AppConst.RESPONSE_OF_ISSUED, "0");
                        NettyClient.getInstance(mContext).sendMsgToServer(str, null);
//
//                        await_stoptcp = true;//关闭tcp

                    } else {
                        PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_BALANCE);
                        PreferencesUtils.getInstance(mContext).setString("awaitModeStart", "");
                        PreferencesUtils.getInstance(mContext).setString("awaitModeEnd", "");
                    }
//                    handler.sendEmptyMessage(6);

                } else if (response.getCmd().equals(AppConst.FREQUENCY_LOCATION_SET)) {//位置上报频率下发
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.FREQUENCY_LOCATION_SET, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                    period = Integer.valueOf(data) * 60;
                    initialDelay = 0;
                    handler.sendEmptyMessage(6);
                } else if (response.getCmd().equals(AppConst.SET_INCOMING_CALL)) {//设置呼入号码 白名单
                    IncomingCall incomingCallNew = IncomingCall.parseJson(data);
                    //覆盖之前的数据
                    PreferencesUtils.getInstance(mContext).setString("incomingCall", JsonUtil.toJSONString(incomingCallNew));
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_INCOMING_CALL, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.SET_SERVER_INFO)) {//设置服务信息
                    String[] strings = data.split("@");
                    if (!TextUtils.isEmpty(strings[0]))
                        PropertiesUtil.getInstance().setProperties(mContext, "host", strings[0]);
                    if (!TextUtils.isEmpty(strings[1]))
                        PropertiesUtil.getInstance().setProperties(mContext, "tcp_port", strings[1]);
                    //重启tcp
                    NettyClient.getInstance(mContext).stopTcp();
                    NettyClient.getInstance(mContext).connect();
                } else if (response.getCmd().equals(AppConst.LOCATION_INFO_GET)) {//实时位置获取
                    PreferencesUtils.getInstance(mContext).setString("LOCATION_INFO_GET", waterNumber);
                    handler.sendEmptyMessage(1);
                } else if (response.getCmd().equals(AppConst.SET_HEARTBEAT)) {//设置终端心跳
//                    setHeart(Integer.valueOf(data));
                    setPeriod_heart(Integer.valueOf(data) * 60);
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_HEARTBEAT, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.SET_MODEL)) {//设置情景模式
                    //课堂模式 > 情景模式 > 其他模式
                    //静音@响玲@限制呼入@限制呼出
                    ContextualModel contextualModel = ContextualModel.parseJson(data);
                    PreferencesUtils.getInstance(mContext).setString("contextualModel", JsonUtil.toJSONString(contextualModel));
                    if ("1".equals(contextualModel.getSilence())) {//静音
                        DeviceUtil.silentSwitchOn(mContext);
                    }
                    if ("1".equals(contextualModel.getRing())) {//响铃
                        DeviceUtil.silentSwitchOff(mContext);
                    }
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_MODEL, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.REQUEST_CALL)) {//安全防护
                    DeviceUtil.callSOSPhone(mContext);
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.REQUEST_CALL, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.SET_CLASS_MODEL)) {//课堂模式
                    ClassModel classModel = ClassModel.parseJson(mContext, data);
                    PreferencesUtils.getInstance(mContext).setString("classModel", JsonUtil.toJSONString(classModel));
                    //静音
                    DeviceUtil.silentSwitchOn(mContext);
                    String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_CLASS_MODEL, AppConst.RESPONSE_OF_ISSUED, "0");
                    NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                } else if (response.getCmd().equals(AppConst.SET_CLOCK)) {//设置闹铃
                    //1=0900!1+2+3!0@2=1000!1+2+3!1
                    ClickBean clickBean = ClickBean.parseJson(data);
                    PreferencesUtils.getInstance(mContext).setString("clickBean", JsonUtil.toJSONString(clickBean));
                } else if (response.getCmd().equals(AppConst.SET_CALL_DURATION)) {//设置通话时长
                    //新增/删除@时长(分钟)：1@110  1 设置 0 删除
                    String[] strings = data.split("@");
                    String callsetting = strings[0];
                    String calltimeLong = strings[1];
                    PreferencesUtils.getInstance(mContext).setString("callSetting", callsetting);
                    PreferencesUtils.getInstance(mContext).setInt("callTimeLong", Integer.valueOf(calltimeLong));

                } else if (response.getCmd().equals(AppConst.SEND_SMS)) {//短消息
                    //1 短信类型：0 紧急消息 1通知 2作业 3考勤 4消费 5普通消息
                    //2 消息编号：uuid
                    //3 播报还是屏显：0语音播报1屏幕显示2语音播报并屏幕显示
                    //4 播报报次数 播报时默认1次，不播报则0，上限三次
                    //5 是否闪烁屏幕或LED：0否 1是
                    //6 是否震动：0否 1是
                    //7 是否响铃：0否 1是
                    //8 短信内容
                    //4@990cb152f17248a98458527f2e79ad87@0@1@0@0@0@好好好
                    try {
                        String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SEND_SMS, AppConst.RESPONSE_OF_ISSUED, "0");
                        NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                        SmsMessageReceive smsMessageReceive = SmsMessageReceive.parseJson(data);
                        //存本地数据库
                        SmsMessageDao smsMessageDao = DaoManager.getInstance().getDaoSession().getSmsMessageDao();
                        smsMessageDao.insert(new SmsMessage(System.currentTimeMillis() + "", "【" + smsMessageReceive.getSmsType() + "】" + smsMessageReceive.getMessage(), 0, System.currentTimeMillis(), smsMessageReceive.getUuid()));

                        if (smsMessageReceive.getSpeackOrShow().equals("0")) {//语音播报
                            int stime = Integer.parseInt(smsMessageReceive.getSpeackTime());
                            if (stime == 0 || stime > 3) {
                                return;
                            } else {
                                if (stime >= 1) {
                                    for (int i = 0; i < stime; i++) {
//                                        TextToSpeechUtils.getInstance(mContext,smsMessageReceive.getSmsType() + "," + smsMessageReceive.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
                                        KdxfSpeechSynthesizerUtil.getInstance(mContext, smsMessageReceive.getSmsType() + "," + smsMessageReceive.getMessage());
                                    }
                                }
                            }
                        } else if (smsMessageReceive.getSpeackOrShow().equals("1")) {//屏幕显示
                            //发送广播 屏幕显示
                            sendScreenBrocast(smsMessageReceive.getUuid(), smsMessageReceive.getSmsType());
                        } else if (smsMessageReceive.getSpeackOrShow().equals("2")) {//语音播报并屏幕显示
                            int stime = Integer.parseInt(smsMessageReceive.getSpeackTime());
                            if (stime == 0 || stime > 3) {
                                return;
                            } else {
                                if (stime >= 1) {
                                    for (int i = 0; i < stime; i++) {
//                                        TextToSpeechUtils.getInstance(mContext,smsMessageReceive.getSmsType() + "," + smsMessageReceive.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
                                        KdxfSpeechSynthesizerUtil.getInstance(mContext, smsMessageReceive.getSmsType() + "," + smsMessageReceive.getMessage());
                                    }
                                }
                            }
                            //发送广播 屏幕显示
                            sendScreenBrocast(smsMessageReceive.getUuid(), smsMessageReceive.getSmsType());
                        }
                        //6 是否震动：0否 1是
                        if (smsMessageReceive.getShake().equals("1")) {
                            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                        }
                        //7 是否响铃：0否 1是
                        if (smsMessageReceive.getRing().equals("1")) {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
                            r.play();
                        }
                        //led闪烁
//                        if (smsMessageReceive.getFlash().equals("1")) {
//                            //判断设备是否有led
//                            if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                                Camera camera = Camera.open();
//                                Camera.Parameters parameter = camera.getParameters();
//                                //打开闪光灯
//                                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                                camera.setParameters(parameter);
//                                //关闭闪光灯
//                                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                                camera.setParameters(parameter);
//                            }
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    Message message = Message.obtain();
//                    message.what = 5;
//                    message.obj = data;
//                    //向主线程发送消息
//                    handler.sendMessage(message);

                } else if (response.getCmd().equals(AppConst.SET_HEALTH)) {//设置心率，温度的上报频率：参数标识@参数@
                    //第一位:参数标识
                    //10 心率上报频率设置
                    //11 获取实时心率
                    //20 温度上报频率设置
                    //21 获取实时温度
                    //12 获取实时温度和心率
                    //第二位:参数
                    //1=0900!1200#2=1200
                    //其中0-6标识星期， 0表示星期日，1表示星期一6标识星期六，
                    //每天最多可设置6组定时测温或者心率测试时间
                    // 每个时间点之间用！符分割，每天用#分割
                    try {
                        String[] datas = data.split("@");
                        if (datas[0].equals("10")) {//心率上报频率设置

                        } else if (datas[0].equals("11")) {//获取实时心率

                        } else if (datas[0].equals("20")) {//温度上报频率设置
                            TemFrequency temFrequency = TemFrequency.parseJson(datas[1]);
                            PreferencesUtils.getInstance(mContext).setString("temFrequency", JsonUtil.toJSONString(temFrequency));
                            String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_HEALTH, AppConst.RESPONSE_OF_ISSUED, "0@0@0");
                            NettyClient.getInstance(mContext).sendMsgToServer(str, null);
                        } else if (datas[0].equals("21")) {//获取实时温度
                            AppConst.ISSUED_TEM_WATERNUMBER = waterNumber;
                            //发送广播 获取实时温度
                            Intent intent = new Intent();
                            intent.setAction(BroadcastConstant.TEMPERATURE_START);
                            mContext.sendBroadcast(intent);// 发送
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (response.getCmd().equals(AppConst.SET_DEVICE_INFO)) {//设备参数下发
                    //WIFI信息@预留功能@预留功能
                    //家庭WIFI MAC地址!学校WIFI MAC地址
                    //AC:BC:32:78:A2:5F#AC:BC:32:78:A2:52!AC:BC:32:78:A2:5F#AC:BC:32:78:A2:51
                    //开发时间：2020.7.14，后续还需加功能
                    try {
                        String[] datas = data.split("!");
                        String[] homeWifis = datas[0].split("#");
                        String[] schoolWifis = datas[1].split("#");

                        PreferencesUtils.getInstance(mContext).setString("homeWifis", Arrays.toString(homeWifis));
                        PreferencesUtils.getInstance(mContext).setString("schoolWifis", Arrays.toString(schoolWifis));

                        String str = PackDataUtil.packRequestStr(BaseSDK.getBaseContext(), waterNumber, AppConst.SET_DEVICE_INFO, AppConst.RESPONSE_OF_ISSUED, "0");
                        NettyClient.getInstance(mContext).sendMsgToServer(str, null);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            } else if (response.getMessageType().equals(AppConst.RESPONSE_OF_REPORT)) {//上报的响应回复
                LogUtil.e("this is 上报的响应回复:" + data);
                if (response.getCmd().equals(AppConst.LOGIN)) {//判断是否登录成功
                    String[] strings = data.split("@");//0@0@0  成功
                    if (strings[2].equals("1")) {//需要 恢复出厂设置
                        clearPreferences();
                    }
                    if (strings[0].equals("0") && strings[1].equals("0") && strings[2].equals("0")) {
                        AppConst.LOGIN_SUCCESS = true;
                        PreferencesUtils.getInstance(mContext).setBoolean("loginLater", false);//
                        LogUtil.e("登陆成功");
                        handler.sendEmptyMessage(2);//上报GPS数据
                        handler.sendEmptyMessage(8);//心跳
                        PreferencesUtils.getInstance(mContext).setString("locationModeOld", AppConst.MODEL_BALANCE);//平衡模式
                        PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_BALANCE);//平衡模式

                        LogUtil.d("AppConst.BOOTBROADCAST：" + AppConst.BOOTBROADCAST);
                        if (AppConst.BOOTBROADCAST) {
                            AppConst.BOOTBROADCAST = false;
                            String dataButtery = "4@" + DeviceUtil.getBattery();
                            BaseSDK.getInstance().sendBootAndShutdown(dataButtery, new OnReceiveListener() {
                                @Override
                                public void onResponse(final String msg) {
                                    LogUtil.d("自动开机的return: " + msg);
                                }
                            });
                        }
                        if (AppConst.TO_UPDATE) {
                            AppConst.TO_UPDATE = false;
                            //请求更新apk
                            BaseSDK.getInstance().geUpdate(UIUtil.getVersionName(mContext)+"@", new OnReceiveListener() {
                                @Override
                                public void onResponse(final String msg) {
                                }
                            });
                        }
                        //端口获取
                        BaseSDK.getInstance().getSmsPort("1", new OnReceiveListener() {
                            @Override
                            public void onResponse(final String msg) {
                            }
                        });
                        //呼入号码获取
                        BaseSDK.getInstance().getIncomingCall("1", new OnReceiveListener() {
                            @Override
                            public void onResponse(final String msg) {

                            }
                        });
                        //按键获取
                        BaseSDK.getInstance().getNormalButton("1", new OnReceiveListener() {
                            @Override
                            public void onResponse(final String msg) {

                            }
                        });
                        //课堂模式获取
                        BaseSDK.getInstance().getClassModel("1", new OnReceiveListener() {
                            @Override
                            public void onResponse(final String msg) {

                            }
                        });
                    } else if (strings[0].equals("1")) {//设备认为登录失败，会在下一次心跳时间（5 分钟后）重新发送登录报文
                        AppConst.LOGIN_SUCCESS = false;
                        LogUtil.e("设备认为登录失败，5分钟以后重复登陆");
                        PreferencesUtils.getInstance(mContext).setBoolean("loginLater", true);//
                        //5分钟以后重复登陆
                        handler.sendEmptyMessage(4);
                    } else if (strings[0].equals("2")) {//终端不会再向平台发送信息，除非重启设备
                        AppConst.LOGIN_SUCCESS = false;
                        stopTcp();
                    } else if (strings[2].equals("1")) {//需要发送短信
                        LogUtil.e("登陆失败，发送短信");
                        AppConst.LOGIN_SUCCESS = false;
                        if (count_send_login >= 10) {
                            stopTcp();
                            return;
                        }
                        if (!PropertiesUtil.getInstance().isWeixiao(mContext)) {
                            sendSMS(mContext, strings[1], DeviceUtil.getPhoneIMEI(mContext) + "@" + DeviceUtil.getSimSerialNumber(mContext));
                        }
                        //2分钟以后重复登陆
                        handler.sendEmptyMessage(3);
                    }
                } else if (response.getCmd().equals(AppConst.REPORT_HEARTBEAT)) {
                    LogUtil.e("heart return");
                } else if (response.getCmd().equals(AppConst.GET_SERVICE_MSG)) {//更新
                    try {
                        //第一位是更新apk
                        String[] strings = data.split("@");//apk地址@
                        if (!"0".equals(strings[0])) {
                            Intent intent = new Intent(mContext, APKDownloadService.class);
                            intent.putExtra("downloadUrl", strings[0]);
                            intent.putExtra("apkName", "iot");
                            mContext.startService(intent);
                            sendupdateBrocast(false);
                        } else {
                            sendupdateBrocast(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.getCmd().equals(AppConst.GET_SMS_PORT)) {//获取端口
                    //异常应答：0无设置  1非平台用户  2其他异常
                    if (data.equals("0") || data.equals("1") || data.equals("2")) {
                        LogUtil.e("异常应答:" + data);
                    } else {
                        PreferencesUtils.getInstance(mContext).setString("smsPort", data);
                    }
                } else if (response.getCmd().equals(AppConst.GET_NORMAL_BUTTON)) {//按键
                    //异常应答：0无设置  1非平台用户  2其他异常
                    if (data.equals("0") || data.equals("1") || data.equals("2")) {
                        LogUtil.e("异常应答:" + data);
                    } else {
                        //0=13900000000!1=!2=13900000002!3=13900000003!4=13900000004!5=13900000005!
                        PhoneNumber phoneNumber = PhoneNumber.parseJson(data);
                        PreferencesUtils.getInstance(mContext).setString("phoneNumber", JsonUtil.toJSONString(phoneNumber));
                    }
                } else if (response.getCmd().equals(AppConst.GET_CLASS_MODEL)) {//课堂模式
                    //异常应答：0无设置  1非平台用户  2其他异常
                    if (data.equals("0") || data.equals("1") || data.equals("2")) {
                        LogUtil.e("异常应答:" + data);
                    } else {
                        ClassModel classModel = ClassModel.parseJson(mContext, data);
                        PreferencesUtils.getInstance(mContext).setString("classModel", JsonUtil.toJSONString(classModel));
                    }
                } else if (response.getCmd().equals(AppConst.GET_INCOMING_CALL)) {//呼入号码
                    //异常应答：0无设置  1非平台用户  2其他异常
                    if (data.equals("0") || data.equals("1") || data.equals("2")) {
                        LogUtil.e("异常应答:" + data);
                    } else {
                        IncomingCall incomingCallNew = IncomingCall.parseJson(data);
                        PreferencesUtils.getInstance(mContext).setString("incomingCall", JsonUtil.toJSONString(incomingCallNew));
                    }
                }else if (response.getCmd().equals(AppConst.REPORT_LOCATION_INFO)) {//上报完成位置后，如果是进入待机模式，就切换模式
                    String chaneMode = PreferencesUtils.getInstance(mContext).getString("changeMode", "");
                    if(AppConst.MODEL_AWAIT.equals(chaneMode)){
                        PreferencesUtils.getInstance(mContext).setString("changeMode", "");
                        LogUtil.e("设备 进入  待机模式");
                        BaseSDK.getInstance().send_device_status(AppConst.MODEL_AWAIT);
                        BaseSDK.getInstance().setAwait_stoptcp(true);
                    }
                } else {
                    OnReceiveListener listener = listenerArrayMap.remove(waterNumber);
                    if (listener != null) {
                        listener.onResponse(data);
                    }
                }


            }
        } else {
            LogUtil.e("------------------- parse response error -------------------");
        }
    }

    //发送广播 版本更新
    private void sendupdateBrocast(boolean is_head) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.UPDATE);
        intent.putExtra("is_head", is_head);
        mContext.sendBroadcast(intent);// 发送
        LogUtil.e("sendupdateBrocast ok");
    }

    //发送广播 屏幕显示
    private void sendScreenBrocast(String uuid, String smsType) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SHOW_SCREEN_SMS);
        intent.putExtra("uuid", uuid);
        intent.putExtra("smsType", smsType);

        mContext.sendBroadcast(intent);// 发送
        LogUtil.e("sendScreenBrocast ok");
    }

    public void sendSMS(Context context, String phoneNumber, String text) {
        SmsManagerUtils.getInstence(context).sendSMS(phoneNumber, text);
    }

    //定时上报，wifi优先
    public void findWifi() {
        if (NetworkUtil.mWifiList.size() >= 5) {//5个以上有效
            String locationInfo = NetworkUtil.packNetInfo(mContext, 0, 0, "");
            executor(locationInfo);
        } else {//5个一下，上报gps
            findGPS();
        }
    }

    public void findGPS() {
        GPS_ING = true;
        try {
            getLocation(mContext); //去定位
//            if (gpsstate.equals("0")) {
            new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    LogUtil.e("CountDownTimer：onTick：" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    LogUtil.e("CountDownTimer：onFinish");
                    LogUtil.writeInFile(mContext, "CountDownTimer：onFinish, no location return!!!!!!!");
                    if (GETGPS == true) {//已经在20秒内请求到位置信息
                        GETGPS = false;
                    } else {//20秒内没有收到GPS信息
                        String locationInfo = NetworkUtil.packNetInfo(mContext, 0, 0, "");
                        executor(locationInfo);
                    }
                }
            }.start();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findGPSGet() {
        GPS_GET_ING = true;
        getLocation(mContext);
        //去定位
//        if (gpsstate.equals("0")) {
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                    LogUtil.e("CountDownTimer：onTick：" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                LogUtil.e("CountDownTimer：onFinish");
                LogUtil.writeInFile(mContext, "CountDownTimer：onFinish, no location return!!!!!!!");
                if (GETGPSGET == true) {//已经在20秒内请求到位置信息
                    GETGPSGET = false;
                } else {//20秒内没有收到GPS信息
                    String locationInfo = NetworkUtil.packNetInfo(mContext, 0, 0, "");
                    executorGet(locationInfo);
                }
            }
        }.start();
//        }
    }

    public static final double[] latitude = {0};
    public static final double[] longitude = {0};

    public static String lastLatitude = "";
    public static String lastLongitude = "";
    public static boolean GETGPS = false;
    public static boolean GPS_ING = false;
    public static boolean GETGPSGET = false;
    public static boolean GPS_GET_ING = false;

    @SuppressLint("MissingPermission")
    public void getLocation(Context context) {
        GpsUtil.openGPSSettings(context);//打开gps
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new GPSListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, mlocListener);
    }

    public void reconnectTcp() {
        LogUtil.e("reconnectTcp");
        if (!NettyClient.getInstance(mContext).getConnectStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyClient.getInstance(mContext).reconnect();//连接服务器
                }
            }).start();
        }
    }

    public void stopTcp() {
        PreferencesUtils.getInstance(mContext).setBoolean("stopTcp", true);
        LogUtil.e("stopTcp主动断开tcp连接");
        if (NettyClient.getInstance(mContext).getConnectStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyClient.getInstance(mContext).stopTcp();//断开服务器
                }
            }).start();
        }
    }

    public void setHeart(final int allIdleTime) {
        LogUtil.e("setHeart");
        if (NettyClient.getInstance(mContext).getConnectStatus()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NettyClient.getInstance(mContext).setHeart(allIdleTime);//
                }
            }).start();
        }
    }

    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (GPS_ING == true) {
                latitude[0] = location.getLatitude();
                longitude[0] = location.getLongitude();
                String locationInfo = NetworkUtil.packNetInfo(mContext, latitude[0], longitude[0], TimeUtils.getNowTimeString(TimeUtils.format6));
                executor(locationInfo);
                lastLongitude = PreferencesUtils.getInstance(mContext).getString(PreferencesUtils.OLD_GPS_LO, "");
                lastLatitude = PreferencesUtils.getInstance(mContext).getString(PreferencesUtils.OLD_GPS_LA, "");
                sendReport_cross_border(latitude[0], longitude[0]);
                PreferencesUtils.getInstance(mContext).setString(PreferencesUtils.OLD_GPS_LO, location.getLongitude() + "");
                PreferencesUtils.getInstance(mContext).setString(PreferencesUtils.OLD_GPS_LA, location.getLatitude() + "");
                GETGPS = true;
            }

            if (GPS_GET_ING == true) {
                latitude[0] = location.getLatitude();
                longitude[0] = location.getLongitude();
                String locationInfo = NetworkUtil.packNetInfo(mContext, latitude[0], longitude[0], TimeUtils.getNowTimeString(TimeUtils.format6));
                executorGet(locationInfo);
                lastLongitude = PreferencesUtils.getInstance(mContext).getString(PreferencesUtils.OLD_GPS_LO, "");
                lastLatitude = PreferencesUtils.getInstance(mContext).getString(PreferencesUtils.OLD_GPS_LA, "");
                sendReport_cross_border(latitude[0], longitude[0]);
                PreferencesUtils.getInstance(mContext).setString(PreferencesUtils.OLD_GPS_LO, location.getLongitude() + "");
                PreferencesUtils.getInstance(mContext).setString(PreferencesUtils.OLD_GPS_LA, location.getLatitude() + "");
                GETGPSGET = true;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            LogUtil.e("getLocation:onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
//            LogUtil.e("getLocation:onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
//            LogUtil.e("getLocation:onProviderDisabled");
        }
    }


    public void setOnInstructionListener(InstructionCallBack instructionCallBack) {
        sendBack = new SendBack(instructionCallBack);
        NettyClient.getInstance(mContext).setsendBackListener(sendBack);
    }


    public long getPeriodHeart() {
        return period_heart;
    }

    public void setPeriod_heart(int period) {
        period_heart = period;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }


    //链接，并登陆成功
    public boolean getConnectStatus() {
        return NettyClient.getInstance(mContext).getConnectStatus() && AppConst.LOGIN_SUCCESS;
    }

    public void setAlarmTime(Context context, long timeInMillis, String action, int interval, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //参数2是开始时间、参数3是允许系统延迟的时间
        alarmManager.setExact(AlarmManager.RTC, timeInMillis, pendingIntent);
//        alarmManager.setWindow(AlarmManager.RTC, timeInMillis, interval, pendingIntent);

    }

    public void canalAlarm(Context context, String action, int requestCode) {
        Intent intent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void clearPreferences() {
        PreferencesUtils.getInstance(mContext).setString("phoneNumber", "");
        PreferencesUtils.getInstance(mContext).setString("locationModeOld", "");
        PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_BALANCE);
        PreferencesUtils.getInstance(mContext).setLong("locationModeStart", 0l);
        PreferencesUtils.getInstance(mContext).setLong("realTimeModeEnd", 0l);
        PreferencesUtils.getInstance(mContext).setString("awaitModeStart", "");
        PreferencesUtils.getInstance(mContext).setString("awaitModeEnd", "");
        PreferencesUtils.getInstance(mContext).setString("callSetting", "");
        PreferencesUtils.getInstance(mContext).setInt("callTimeLong", 0);
        PreferencesUtils.getInstance(mContext).setString("classModel", "");
        PreferencesUtils.getInstance(mContext).setString("incomingCall", "");
        //围栏
        PreferencesUtils.getInstance(mContext).setString("incomingCall", "");

    }


    public boolean isAwait_stoptcp() {
        return await_stoptcp;
    }

    public void setAwait_stoptcp(boolean await_stoptcp) {
        this.await_stoptcp = await_stoptcp;
    }


}
