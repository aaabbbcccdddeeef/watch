package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.bean.ClassModel;
import com.ctop.studentcard.bean.ContextualModel;
import com.ctop.studentcard.bean.IncomingCall;
import com.ctop.studentcard.bean.PhoneNumber;
import com.ctop.studentcard.bean.SmsMessageReceive;
import com.ctop.studentcard.greendao.DaoManager;
import com.ctop.studentcard.greendao.SmsMessageDao;
import com.ctop.studentcard.util.AppConst;
import com.ctop.studentcard.util.DeviceUtil;
import com.ctop.studentcard.util.JsonUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PackDataUtil;
import com.ctop.studentcard.util.PreferencesUtils;
import com.ctop.studentcard.util.PropertiesUtil;
import com.ctop.studentcard.util.TimeUtils;

import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            LogUtil.e("收到短信");
            // 第一步、获取短信的内容和发件人
            StringBuilder body = new StringBuilder();// 短信内容
            StringBuilder number = new StringBuilder();// 短信发件人
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] _pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] message = new SmsMessage[_pdus.length];
                for (int i = 0; i < _pdus.length; i++) {
                    message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
                }
                for (SmsMessage currentMessage : message) {
                    body.append(currentMessage.getDisplayMessageBody());
                    number.append(currentMessage.getDisplayOriginatingAddress());
                }
                LogUtil.e("body===" + body.toString());

                String waterNumber = PackDataUtil.createWaterNumber();
                insertDb(waterNumber, body.toString());
                if (body.toString().startsWith("SETSERVER")) {//设置IP地址
                    sendSmsSETSERVER(context, 2, body.toString());
                    BaseSDK.getInstance().sendSMS(context,number.toString(),"Done, connect in 5 seconds");
                    return;
                } else if ("SUPERPASS#".equals(body.toString())) {//打开短信指令30分钟
                    sendSmsWhat(context, 0);
                    BaseSDK.getInstance().sendSMS(context,number.toString(),"Device unlocked，lock again in 30 minutes");
                    return;
//                    mHandler.sendEmptyMessage(0);
                } else if ("WAKEUP#".equals(body.toString())) {//设备唤醒
                    BaseSDK.getInstance().init(context);
//                    sendSmsWhat(context, 1);
                    PreferencesUtils.getInstance(context).setString("awaitModeStart", "");
                    PreferencesUtils.getInstance(context).setString("awaitModeEnd", "");
                    PreferencesUtils.getInstance(context).setString("locationModeOld", "4");
                    LogUtil.e("上报设备模式5");
                    BaseSDK.getInstance().send_device_status(PreferencesUtils.getInstance(context).getString("locationMode",  AppConst.MODEL_BALANCE));

                    BaseSDK.getInstance().sendSMS(context,number.toString(),"Done, connect in 5 seconds");
                    return;
                } else if ("SERVER#".equals(body.toString())) {//获取设备服务地址
                    String data = PropertiesUtil.getInstance().getHost(context) + "," + PropertiesUtil.getInstance().getTcp_port(context);
                    BaseSDK.getInstance().sendSMS(context,number.toString(),data);

                    return;
                } else if ("RESTART#".equals(body.toString())) {//重启设备
                    BaseSDK.getInstance().sendSMS(context,number.toString(),"Done");
                    DeviceUtil.reboot(context);
                    return;
                } else if ("STATUS#".equals(body.toString())) {//设备状态
                    //设备状态应包含以下信息：
                    //1，设备是否在线
                    //2，电量
                    //3，设备网络信号强度
                    //4,  设备GPS是否开启，开启时需包含GPS信号强度
//                    mHandler.sendEmptyMessage(4);
                    sendSmsSTATUS(context, 4, number.toString());

                    return;
                } else {
                    //打开短信指令30分钟
                    boolean iotsuperpass = PreferencesUtils.getInstance(context).getBoolean("IOTSUPERPASS", false);
                    if (iotsuperpass) {
//                        Message msg = Message.obtain();
//                        msg.what = 5;
//                        msg.obj = smsMessageReceive;
//                        mHandler.sendMessage(msg);
                        sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                        return;
                    }

//                    Message msg = Message.obtain();
//                    msg.what = 5;
//                    msg.obj = smsMessageReceive;

                    //亲情号码、sos、白名单
                    // //课堂模式 > 情景模式 > 其他模式
                    //课堂模式
                    String classModelString = PreferencesUtils.getInstance(context).getString("classModel", "");
                    ClassModel classModel = JsonUtil.parseObject(classModelString, ClassModel.class);
                    if (classModel != null) {
                        //sos号码
                        String phontNu = PreferencesUtils.getInstance(context).getString("phoneNumber", "");
                        PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                        //sos号码不可呼入
//                        if (number.equals(phoneNumber.getSosNumber())) {
//                            if (!"0".equals(classModel.getSosInFlag())) {
////                                mHandler.sendMessage(msg);
//                                sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
//                                return;
//                            }
//                        }

                        //普通号码
                        if (classModel.getItems().size() > 0) {
                            List<ClassModel.ItemsBean> itemsBeanList = classModel.getItems();
                            for (int i = 0; i < itemsBeanList.size(); i++) {
                                if ("0".equals(itemsBeanList.get(i).getIsEffect())) continue;//不生效
                                List<ClassModel.ItemsBean.PeriodBean> periodBeans = itemsBeanList.get(i).getPeriod();
                                for (int j = 0; j < periodBeans.size(); j++) {
                                    if (TimeUtils.getWeekInCome().equals(periodBeans.get(i).getWeek())) {
                                        int awaitstartInt = Integer.parseInt(itemsBeanList.get(i).getStartTime());
                                        int awaitendInt = Integer.parseInt(itemsBeanList.get(i).getEndTime());
                                        String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                                        int timeNowInt = Integer.parseInt(timeNow);
                                        if (awaitstartInt < awaitendInt) {//同一天
                                            if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {

                                            } else {
//                                                mHandler.sendMessage(msg);
                                                sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                                                return;
                                            }
                                        } else {//不同天
                                            if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {

                                            } else {
                                                sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                                                return;
//                                                mHandler.sendMessage(msg);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                    //情景模式：取出本地数据
                    String contextualModelString = PreferencesUtils.getInstance(context).getString("contextualModel", "");
                    ContextualModel contextualModel = JsonUtil.parseObject(contextualModelString, ContextualModel.class);
                    if (contextualModel != null) {
                        if (contextualModel.getRing().equals("1") && contextualModel.getInBound().equals("0")) {
//                            mHandler.sendMessage(msg);
                            sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                            return;
                        }

                    }
                    //呼入限制：取出本地数据
                    String incomingCallString = PreferencesUtils.getInstance(context).getString("incomingCall", "");
                    IncomingCall incomingCallOld = JsonUtil.parseObject(incomingCallString, IncomingCall.class);
                    if (incomingCallOld != null) {
                        //1、无限制 2、限制白名单以外的号码呼入 3、限制所有号码呼入
                        if (incomingCallOld.getCallLimit().equals("1")) {
//                            mHandler.sendMessage(msg);
                            sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                            return;
                        }
                        if (incomingCallOld.getCallLimit().equals("2")) {
                            List<IncomingCall.AddPhoneBean> addPhoneBeanList = incomingCallOld.getAddPhone();
                            boolean flagPeriod = false;
                            List<IncomingCall.PeriodBean> periodBeans = incomingCallOld.getPeriod();
                            for (int i = 0; i < periodBeans.size(); i++) {
                                if (TimeUtils.getWeekInCome().equals(periodBeans.get(i).getWeek())) {
                                    flagPeriod = true;
                                }
                            }
                            if (flagPeriod) {
                                for (int j = 0; j < addPhoneBeanList.size(); j++) {
                                    if (addPhoneBeanList.get(j).getPhone().equals(number)) {
                                        List<IncomingCall.AddPhoneBean.TimeBean> timeBeanList = addPhoneBeanList.get(j).getTime();
                                        for (int i = 0; i < timeBeanList.size(); i++) {
                                            int awaitstartInt = Integer.parseInt(timeBeanList.get(i).getStart());
                                            int awaitendInt = Integer.parseInt(timeBeanList.get(i).getEnd());
                                            String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                                            int timeNowInt = Integer.parseInt(timeNow);
                                            if (awaitstartInt < awaitendInt) {//同一天
                                                if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {
                                                } else {
//                                                    mHandler.sendMessage(msg);
                                                    sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                                                    return;
                                                }
                                            } else {//不同天
                                                if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                                                } else {
//                                                    mHandler.sendMessage(msg);
                                                    sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                                                    return;
                                                }
                                            }
                                        }

                                    }
                                }
                            }

                        }
                    }

                }


//                String smsBody = body.toString();
//                String smsNumber = number.toString();
//                LogUtil.e("smsNumber===" + smsNumber);
//                if (smsNumber.contains("+86")) {
//                    smsNumber = smsNumber.substring(3);
//                }
//                // 第二步:确认该短信内容是否满足过滤条件
//                boolean flags_filter = false;
//                if (smsNumber.equals("18309280898")) {// 屏蔽10086发来的短信
//                    flags_filter = true;
//                    LogUtil.e("SmsReceiver,sms_number.equals(18309280898)");
//                }
//                // 第三步:取消
//                if (flags_filter) {
//                    this.abortBroadcast();
//                }
            }
        }
    }


    private void insertDb(String waterNumber, String body) {
        //存本地数据库
        SmsMessageDao smsMessageDao = DaoManager.getInstance().getDaoSession().getSmsMessageDao();
        smsMessageDao.insert(new com.ctop.studentcard.greendao.SmsMessage(System.currentTimeMillis() + "", "【通知】" + body, 0, System.currentTimeMillis(), waterNumber));
    }


    //发送广播 短信
    public static void sendSmsWhat(Context context, int what) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SMS_STATE);
        intent.putExtra("what", what);
        context.sendBroadcast(intent);// 发送
    }

    //发送广播 短信
    public static void sendSmsSTATUS(Context context, int what, String number ) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SMS_STATE);
        intent.putExtra("what", what);
        intent.putExtra("number", number);
        context.sendBroadcast(intent);// 发送
    }

    //发送广播 短信
    public static void sendSmsSETSERVER(Context context, int what, String param ) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SMS_STATE);
        intent.putExtra("what", what);
        intent.putExtra("param", param);
        context.sendBroadcast(intent);// 发送
    }

    //发送广播 短信
    public static void sendSmsWhatAndObj(Context context, int what, String waterNumber, String message, String smsType) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SMS_STATE);
        intent.putExtra("what", what);
        intent.putExtra("uuid", waterNumber);
        intent.putExtra("message", message);
        intent.putExtra("smsType", smsType);
        context.sendBroadcast(intent);// 发送
    }


}
