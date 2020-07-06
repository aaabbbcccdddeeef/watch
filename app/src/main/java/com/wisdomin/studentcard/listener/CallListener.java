package com.wisdomin.studentcard.listener;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallListener extends PhoneStateListener {
    //上个电话状态，当上个状态为响铃，当前状态为OFFHOOK时即为来电接通
    private int mLastState = TelephonyManager.CALL_STATE_IDLE;
    private OnIncomingListener mOnIncomingListener;
    private OnCallOffListener mOnCallOffListener;
    //来电号码
    private String mIncomingNumber;

    public CallListener(OnIncomingListener listener, OnCallOffListener callOffListener) {
        this.mOnIncomingListener = listener;
        this.mOnCallOffListener = callOffListener;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: // 空闲状态，即无来电也无去电
                if (mLastState == TelephonyManager.CALL_STATE_OFFHOOK && mOnCallOffListener != null) {
                    mOnCallOffListener.off();
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING: // 来电响铃
                mIncomingNumber = String.valueOf(incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机，即接通
                //此处添加一系列功能代码
                if (mLastState == TelephonyManager.CALL_STATE_RINGING && mOnIncomingListener != null) {
                    mOnIncomingListener.incomingOn(mIncomingNumber);
                }
                break;
        }
        mLastState = state;
        super.onCallStateChanged(state, incomingNumber);
    }

    //来电接通
    public interface OnIncomingListener {
        void incomingOn(String number);
    }

    //电话挂断监听，不分来去
    public interface OnCallOffListener {
        void off();
    }
}  