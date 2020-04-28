package com.ctop.studentcard.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SmsManagerUtils {
    private static PendingIntent paIntent;

    private static SmsManager smsManager;
    private static SmsManagerUtils smsManagerUtils;

    private SmsManagerUtils() {
    }


    public static SmsManagerUtils getInstence(Context context) {
        if(smsManagerUtils ==null){
            paIntent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
            smsManager = SmsManager.getDefault();
            smsManagerUtils =new SmsManagerUtils();
        }
        return smsManagerUtils;
    }


    public void sendSMS(String destinationAddress, String text) {
        smsManager.sendTextMessage(destinationAddress, null, text, paIntent,
                null);
    }

    public void sendMsg2(Context context,String phoneNumber, String text) {

//        SmsManager sms = SmsManager.getDefault();
//        PendingIntent sentPI;
//        String SENT = "SMS_SENT";
//
//        sentPI = PendingIntent.getBroadcast(context, 0,new Intent(SENT), 0);
//
//        sms.sendTextMessage(phoneNumber, null, text, sentPI, null);

        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
            LogUtil.e("getUrlTest:信息已发送");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
