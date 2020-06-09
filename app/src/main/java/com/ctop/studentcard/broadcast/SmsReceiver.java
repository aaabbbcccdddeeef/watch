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

import java.util.ArrayList;
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
                    BaseSDK.getInstance().sendSMS(context, number.toString(), "Done, connect in 5 seconds");
                    sendSmsSETSERVER(context, 2, body.toString());
                    return;
                } else if ("SUPERPASS#".equals(body.toString())) {//打开短信指令30分钟
                    BaseSDK.getInstance().canalAlarm(BaseSDK.getInstance().getBaseContext(), BroadcastConstant.GPS,0);
                    sendSmsWhat(context, 0);
                    BaseSDK.getInstance().sendSMS(context, number.toString(), "Device unlocked，lock again in 30 minutes");
                    return;
//                    mHandler.sendEmptyMessage(0);
                } else if ("WAKEUP#".equals(body.toString())) {//设备唤醒
                    BaseSDK.getInstance().init(context);
//                    sendSmsWhat(context, 1);
                    PreferencesUtils.getInstance(context).setString("awaitModeStart", "");
                    PreferencesUtils.getInstance(context).setString("awaitModeEnd", "");
                    PreferencesUtils.getInstance(context).setString("locationModeOld", AppConst.MODEL_AWAIT);
                    LogUtil.e("上报设备模式5");
                    BaseSDK.getInstance().send_device_status(PreferencesUtils.getInstance(context).getString("locationMode", AppConst.MODEL_BALANCE));

                    BaseSDK.getInstance().sendSMS(context, number.toString(), "Done, connect in 5 seconds");
                    return;
                } else if ("SERVER#".equals(body.toString())) {//获取设备服务地址
                    String data = PropertiesUtil.getInstance().getHost(context) + "," + PropertiesUtil.getInstance().getTcp_port(context);
                    BaseSDK.getInstance().sendSMS(context, number.toString(), data);

                    return;
                } else if ("RESTART#".equals(body.toString())) {//重启设备
//                    BaseSDK.getInstance().sendSMS(context, number.toString(), "Done");
//                    DeviceUtil.reboot(context);
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
//                        sendSmsWhatAndObj(context, 5, waterNumber, body.toString(), "1");
                        return;
                    }

//                    Message msg = Message.obtain();
//                    msg.what = 5;
//                    msg.obj = smsMessageReceive;

                    List phoneList = new ArrayList<String>();
                    //呼入限制：取出本地数据
                    String incomingCallString = PreferencesUtils.getInstance(context).getString("incomingCall", "");
                    if (!incomingCallString.equals("") && !incomingCallString.equals("null")) {
                        IncomingCall incomingCallOld = JsonUtil.parseObject(incomingCallString, IncomingCall.class);
                        List<IncomingCall.AddPhoneBean> addPhoneBeanList = incomingCallOld.getAddPhone();
                        for (int j = 0; j < addPhoneBeanList.size(); j++) {
                            phoneList.add(addPhoneBeanList.get(j).getPhone());
                        }
                    }
                    //按键号码
                    String phontNu = PreferencesUtils.getInstance(context).getString("phoneNumber", "");
                    if (!phontNu.equals("") && !phontNu.equals("null")) {
                        PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                        phoneList.add(phoneNumber.getSosNumber());
                        List<PhoneNumber.EachPhoneNumber> eachPhoneNumberList = phoneNumber.getItems();
                        for (int i = 0; i < eachPhoneNumberList.size(); i++) {
                            phoneList.add(eachPhoneNumberList.get(i).getPhoneNumber());
                        }
                    }

                    if (phoneList.contains(number.toString())) {
                        insertDb(waterNumber, body.toString());
                    }
                }

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
    public static void sendSmsSTATUS(Context context, int what, String number) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SMS_STATE);
        intent.putExtra("what", what);
        intent.putExtra("number", number);
        context.sendBroadcast(intent);// 发送
    }

    //发送广播 短信
    public static void sendSmsSETSERVER(Context context, int what, String param) {
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
