package com.ctop.studentcard.feature;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.bean.ClassModel;
import com.ctop.studentcard.bean.ContextualModel;
import com.ctop.studentcard.bean.PhoneNumber;
import com.ctop.studentcard.broadcast.BroadcastConstant;
import com.ctop.studentcard.util.AppConst;
import com.ctop.studentcard.util.JsonUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;
import com.ctop.studentcard.util.TimeUtils;
import com.ctop.studentcard.util.ai.KdxfSpeechSynthesizerUtil;

import java.util.ArrayList;
import java.util.List;

public class CallNumberActivity extends Activity {

    private Context mContext;
    private Reject_call_out_receiver reject_call_out_receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.public_one_dialog);
        mContext = this;
        screenOn();
        initView();

        reject_call_out_receiver = new Reject_call_out_receiver();
        final IntentFilter intentFilter = new IntentFilter(BroadcastConstant.REJECT_CALL_OUT);
        mContext.registerReceiver(reject_call_out_receiver, intentFilter);
    }


    class Reject_call_out_receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e( "onReceive: action: " + action);
            if (action.equals(BroadcastConstant.REJECT_CALL_OUT)) {//Action
                String msg = intent.getExtras().getString("msg");
                Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initView() {
        ImageView button1 = findViewById(R.id.iv_one);
        ImageView button2 = findViewById(R.id.iv_two);
        ImageView button3 = findViewById(R.id.iv_three);
        ImageView buttonsos = findViewById(R.id.iv_sos);
        button1.setClickable(true);
        button2.setClickable(true);
        button3.setClickable(true);
        buttonsos.setClickable(true);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(2);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(3);
            }
        });
        buttonsos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick(4);
            }
        });
    }

    String toastStr = "禁止呼出";
    private void onButtonClick(int buttonNum) {

        if (buttonNum == 1) {
            String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
            if (TextUtils.isEmpty(phontNu)) {
//                TextToSpeechUtils.getInstance(mContext,"www", TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                return;
            } else {
                if(needRejectCall()){
                    Toast.makeText(mContext,toastStr,Toast.LENGTH_LONG).show();
                }else {
                    PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                    List<PhoneNumber.EachPhoneNumber> eachPhoneNumbers = phoneNumber.getItems();
                    for (PhoneNumber.EachPhoneNumber eachPhoneNumber : eachPhoneNumbers) {
                        if (eachPhoneNumber.getPhoneType().equals("1")) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
//                            Uri data = Uri.parse("tel:" +"18309280898");
                            Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                            intent.setData(data);
                            startActivity(intent);
                        }
                    }
                }

            }
        } else if (buttonNum == 2) {
            String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
            if (TextUtils.isEmpty(phontNu)) {
//                TextToSpeechUtils.getInstance(mContext,"请设置亲情号码", TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                return;
            } else {
                if(needRejectCall()){
                    Toast.makeText(mContext,toastStr,Toast.LENGTH_LONG).show();
                }else {
                    PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                    List<PhoneNumber.EachPhoneNumber> eachPhoneNumbers = phoneNumber.getItems();
                    for (PhoneNumber.EachPhoneNumber eachPhoneNumber : eachPhoneNumbers) {
                        if (eachPhoneNumber.getPhoneType().equals("2")) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
//                            Uri data = Uri.parse("tel:" +"18309280898");
                            Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                            intent.setData(data);
                            startActivity(intent);
                        }
                    }

                }

            }
        } else if (buttonNum == 3) {
            String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
            if (TextUtils.isEmpty(phontNu)) {
//                TextToSpeechUtils.getInstance(mContext,"请设置亲情号码", TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                return;
            } else {
                if(needRejectCall()){
                    Toast.makeText(mContext,toastStr,Toast.LENGTH_LONG).show();
                }else {
                    PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                    List<PhoneNumber.EachPhoneNumber> eachPhoneNumbers = phoneNumber.getItems();
                    for (PhoneNumber.EachPhoneNumber eachPhoneNumber : eachPhoneNumbers) {
                        if (eachPhoneNumber.getPhoneType().equals("3")) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                            intent.setData(data);
                            startActivity(intent);
                        }
                    }
                }
            }
        } else if (buttonNum == 4) {
            BaseSDK.getInstance().send_report_sos();
            String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
            if (TextUtils.isEmpty(phontNu)) {
//                TextToSpeechUtils.getInstance(mContext,"请设置S O S号码", TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置S O S号码");
                return;
            } else {
                if(needRejectCall()){
                    Toast.makeText(mContext,toastStr,Toast.LENGTH_LONG).show();
                }else {
                    PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + phoneNumber.getSosNumber());
//                            Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                    intent.setData(data);
                    startActivity(intent);
                    //上报设备模式
                    LogUtil.e("上报设备模式1");
                    BaseSDK.getInstance().send_device_status("3");
                    //设置30分钟的实时模式
                    PreferencesUtils.getInstance(mContext).setString("locationModeOld", PreferencesUtils.getInstance(mContext).getString("locationMode", AppConst.MODEL_BALANCE));
                    PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_REAL_TIME);
                    BaseSDK.getInstance().setPeriod(3 * 60);
                    //计算结束时间 realTime
                    long endTime = System.currentTimeMillis() + 30 * 60 * 1000;
                    PreferencesUtils.getInstance(mContext).setLong("realTimeModeEnd", endTime);
                }
            }
        }
    }


    public void screenOn() {
        PowerManager mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "com.ctop.studentcard:tag");
        mWakeLock.acquire();
        mWakeLock.release();
    }

    float y1 = 0.0F;
    float y2 = 0.0F;

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            this.y1 = motionEvent.getY();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            this.y2 = motionEvent.getY();
            int i = ViewConfiguration.get(this).getScaledTouchSlop();
            float f3 = (i * 10);
            LogUtil.e("f3===" + f3);
            if (this.y2 - this.y1 > f3) {
                startActivity(new Intent(CallNumberActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.up_to_down_in, R.anim.up_to_down_exit);
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this);

        finish();
        overridePendingTransition(R.anim.up_to_down_in, R.anim.up_to_down_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.up_to_down_in, R.anim.up_to_down_exit);
    }


    private boolean needRejectCall(){
        boolean rejectflag = false;
        //课堂模式
        String classModelString = PreferencesUtils.getInstance(mContext).getString("classModel", "");
        ClassModel classModel = JsonUtil.parseObject(classModelString, ClassModel.class);
        if (classModel != null) {
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
                                    toastStr = "您正处于课堂模式时间，呼出已受限";
                                    rejectflag = true;
                                    return rejectflag;
                                }
                            } else {//不同天
                                if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                                    toastStr = "您正处于课堂模式时间，呼出已受限";
                                    rejectflag = true;
                                    return rejectflag;
                                }
                            }
                        }
                    }

                }
            }
        }

        //通话时长
        int callTimeLongAlready = PreferencesUtils.getInstance(mContext).getInt("callTimeLongAlready", 0);
        String callSetting = PreferencesUtils.getInstance(mContext).getString("callSetting", "");
        int callTimeLong = PreferencesUtils.getInstance(mContext).getInt("callTimeLong", -1);
        if ("1".equals(callSetting)){
            if (-1 != callTimeLong) {
                if (callTimeLongAlready >= callTimeLong) {
                    toastStr = "您本月的通话时长已用完";
                    rejectflag = true;
                    return rejectflag;
                }
            }
        }


        return rejectflag;
    }


    private void unregisterReceiver(Context context) {
        LogUtil.i("reject_call_out_receiver");
        if (null != reject_call_out_receiver) {
            context.unregisterReceiver(reject_call_out_receiver);
        }
    }


}