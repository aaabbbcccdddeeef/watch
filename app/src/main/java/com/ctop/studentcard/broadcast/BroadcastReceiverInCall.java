package com.ctop.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.ctop.studentcard.bean.ClassModel;
import com.ctop.studentcard.bean.ContextualModel;
import com.ctop.studentcard.bean.IncomingCall;
import com.ctop.studentcard.bean.PhoneNumber;
import com.ctop.studentcard.feature.phone.PhoneInActivity;
import com.ctop.studentcard.feature.phone.PhoneOutActivity;
import com.ctop.studentcard.util.FinishActivityManager;
import com.ctop.studentcard.util.JsonUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;
import com.ctop.studentcard.util.TimeUtils;

/**
 * 呼入限制
 */
public class BroadcastReceiverInCall extends BroadcastReceiver {

    private int lastetState = -10;
    private String lastetActionState = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.e("action:" + action.toString());
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {//呼出
            lastetActionState = Intent.ACTION_NEW_OUTGOING_CALL;
            String phoneData = getResultData();
            LogUtil.e("outgoing:" + phoneData);

            //通话时长
            int callTimeLongAlready = PreferencesUtils.getInstance(context).getInt("callTimeLongAlready", 0);
            String callSetting = PreferencesUtils.getInstance(context).getString("callSetting", "");
            int callTimeLong = PreferencesUtils.getInstance(context).getInt("callTimeLong", -1);
            if ("0".equals(callSetting))
                return;
            if (-1 != callTimeLong) {
                if (callTimeLongAlready >= callTimeLong) {
                    setResultData(null);
                }
            }
            //打chu的号码，不是 按键号码 ，拒绝
            String phontNu = PreferencesUtils.getInstance(context).getString("phoneNumber", "");
            PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
            ArrayList listNum = new ArrayList();
            listNum.add(phoneNumber.getSosNumber());
            List<PhoneNumber.EachPhoneNumber> eachPhoneNumberList =  phoneNumber.getItems();
            for(int i = 0;i<eachPhoneNumberList.size();i++){
                listNum.add(eachPhoneNumberList.get(i).getPhoneNumber());
            }

            if(!listNum.contains(phoneData)){
                setResultData(null);
            }

            //跳转到接听activity
            if (!TextUtils.isEmpty(phoneData)) {
                Intent intent1 = new Intent(context, PhoneOutActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("phoneNumber", phoneData);
                context.startActivity(intent1);
            }

        } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {//呼入电话
            if (lastetActionState.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                return;
            }
            doReceivePhone(context, intent);
        }

//        if ((intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL) || (intent
//                .getAction()
//                .equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)))) {
//
//            String phoneState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//
//            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
//                LogUtil.d("Outgoing call");
//
//                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(phoneState)) {
//                    LogUtil.d("Incoming call/Outgng call Started");
//
//                }
//                if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
//                    LogUtil.d("Call ended");
//                }
//                if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneState)) {
//                    LogUtil.d("Call ringing");
//                }
//            }
//
//        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        String incomePhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        LogUtil.i("[Broadcast]等待接电话=" + incomePhoneNumber);

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();
        LogUtil.i("lastetState====" + lastetState);
        LogUtil.i("state====" + state);
        LogUtil.i("state====" + telephony.getCallState());
        // 如果当前状态为空闲,上次状态为响铃中的话,则认为是未接来电
        if (lastetState == TelephonyManager.CALL_STATE_RINGING && state == TelephonyManager.CALL_STATE_IDLE) {
            noReceiveCall();
            return;
        }
        //最后改变当前值
        lastetState = state;

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:// 来电响铃，呼入限制
                if (TextUtils.isEmpty(incomePhoneNumber) || "null".equals(incomePhoneNumber)) {
                    return;
                }
                //课堂模式 > 情景模式 > 其他模式
                LogUtil.i("有电话 进入");
                String classModelString = PreferencesUtils.getInstance(context).getString("classModel", "");
                ClassModel classModel = JsonUtil.parseObject(classModelString, ClassModel.class);
                if (classModel != null) {
                    //sos号码
                    String phontNu = PreferencesUtils.getInstance(context).getString("phoneNumber", "");
                    PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                    //sos号码不可呼入
//                    if (incomePhoneNumber.equals(phoneNumber.getSosNumber())) {
//                        if ("0".equals(classModel.getSosInFlag())) {
//                            rejectCall(context);
//                            return;
//                        }
//                    }
                    //普通号码
                    if (classModel.getItems().size() > 0) {
                        List<ClassModel.ItemsBean> itemsBeanList = classModel.getItems();
                        for (int i = 0; i < itemsBeanList.size(); i++) {
                            if ("0".equals(itemsBeanList.get(i).getIsEffect())) continue;//不生效
                            List<ClassModel.ItemsBean.PeriodBean> periodBeans = itemsBeanList.get(i).getPeriod();
                            for (int j = 0; j < periodBeans.size(); j++) {
                                if (TimeUtils.getWeekInCome().equals(periodBeans.get(j).getWeek())) {
                                    int awaitstartInt = Integer.parseInt(itemsBeanList.get(i).getStartTime());
                                    int awaitendInt = Integer.parseInt(itemsBeanList.get(i).getEndTime());
                                    String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                                    int timeNowInt = Integer.parseInt(timeNow);
                                    if (awaitstartInt < awaitendInt) {//同一天
                                        if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {
                                            rejectCall(context);
                                            return;
                                        }
                                    } else {//不同天
                                        if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                                            rejectCall(context);
                                            return;
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
                    if ("1".equals(contextualModel.getInBound())) {
                        rejectCall(context);
                        return;
                    }
                }
                //白名单：取出本地数据
                String incomingCallString = PreferencesUtils.getInstance(context).getString("incomingCall", "");
                IncomingCall incomingCallOld = JsonUtil.parseObject(incomingCallString, IncomingCall.class);
                if (incomingCallOld != null) {
                    //1、无限制 2、限制白名单以外的号码呼入 3、限制所有号码呼入
//                    if (incomingCallOld.getCallLimit().equals("1")) ;
                    if (incomingCallOld.getCallLimit().equals("3")) rejectCall(context);
                    if (incomingCallOld.getCallLimit().equals("2")) {
                        List<IncomingCall.AddPhoneBean> addPhoneBeanList = incomingCallOld.getAddPhone();
                        boolean flagPeriod = false;
                        List<IncomingCall.PeriodBean> periodBeans = incomingCallOld.getPeriod();
                        for (int i = 0; i < periodBeans.size(); i++) {
                            if (TimeUtils.getWeekInCome().equals(periodBeans.get(i).getWeek())) {
                                flagPeriod = true;
                                break;
                            }
                        }
                        if (flagPeriod) {
                            for (int j = 0; j < addPhoneBeanList.size(); j++) {
                                if (addPhoneBeanList.get(j).getPhone().equals(incomePhoneNumber)) {
                                    List<IncomingCall.AddPhoneBean.TimeBean> timeBeanList = addPhoneBeanList.get(j).getTime();
                                    for (int i = 0; i < timeBeanList.size(); i++) {
                                        int awaitstartInt = Integer.parseInt(timeBeanList.get(i).getStart());
                                        int awaitendInt = Integer.parseInt(timeBeanList.get(i).getEnd());
                                        String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                                        int timeNowInt = Integer.parseInt(timeNow);
                                        if (awaitstartInt < awaitendInt) {//同一天
                                            if (timeNowInt > awaitendInt || timeNowInt < awaitstartInt) {
                                                rejectCall(context);
                                                return;
                                            }
                                        } else {//不同天22-04  09
                                            if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {
                                                rejectCall(context);
                                                return;
                                            }
                                        }
                                    }

                                }
                            }
                        } else {
                            rejectCall(context);
                            return;
                        }

                    }
                }
                //打进的号码，不是 按键号码 和 白名单 ，拒绝
                ArrayList listNum = new ArrayList();

                //按键号码
                String phontNu = PreferencesUtils.getInstance(context).getString("phoneNumber", "");
                PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                if (phoneNumber != null) {
                    listNum.add(phoneNumber.getSosNumber());
                    List<PhoneNumber.EachPhoneNumber> eachPhoneNumberList = phoneNumber.getItems();
                    for (int i = 0; i < eachPhoneNumberList.size(); i++) {
                        listNum.add(eachPhoneNumberList.get(i).getPhoneNumber());
                    }
                }
                //白名单
                if (incomingCallOld != null) {
                    if (incomingCallOld.getCallLimit().equals("2")) {
                        List<IncomingCall.AddPhoneBean> addPhoneBeanList = incomingCallOld.getAddPhone();
                        for(int i = 0;i<addPhoneBeanList.size();i++){
                            listNum.add(addPhoneBeanList.get(i).getPhone());
                        }
                    }
                }
                if(!listNum.contains(incomePhoneNumber)){
                    setResultData(null);
                }

                //跳转到接听activity
                if (!TextUtils.isEmpty(incomePhoneNumber)) {
                    Intent intent1 = new Intent(context, PhoneInActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("phoneNumber", incomePhoneNumber);
                    context.startActivity(intent1);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE://电话挂断
                LogUtil.i("[Broadcast]电话挂断=" + incomePhoneNumber);
                rejectCall(context);
                noReceiveCall();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机，即接通
                LogUtil.i("[Broadcast]通话中=" + incomePhoneNumber);

//                TelephonyManager.P
//                ContactsContract.CommonDataKinds.Phone curPhone=telephony.getPhone();
//                Call.State callState=curPhone.getForgroundCall().getState();

                break;
        }
    }

//    public void rejectCall() {
//        try {
//            Method method = Class.forName("android.os.ServiceManager")
//                    .getMethod("getService", String.class);
//            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
//            ITelephony telephony = ITelephony.Stub.asInterface(binder);
//            telephony.endCall();
//        } catch (NoSuchMethodException e) {
//            LogUtil.i( "", e);
//        } catch (ClassNotFoundException e) {
//            LogUtil.i( "", e);
//        } catch (Exception e) {
//        }
//    }

    public static void rejectCall(Context context) {
        try {
            TelephonyManager telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<TelephonyManager> c = TelephonyManager.class;

            // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            //允许访问私有方法
            mthEndCall.setAccessible(true);
            final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

            // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
            Method mt = obj.getClass().getMethod("endCall");
            //允许访问私有方法
            mt.setAccessible(true);
            mt.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void noReceiveCall() {
        //关闭接听activity
        FinishActivityManager.getManager().finishActivity(PhoneInActivity.class);
        FinishActivityManager.getManager().finishActivity(PhoneOutActivity.class);

    }


}