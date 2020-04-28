package com.ctop.studentcard.feature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.Nullable;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.bean.PhoneNumber;
import com.ctop.studentcard.util.JsonUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;
import com.ctop.studentcard.util.ai.KdxfSpeechSynthesizerUtil;

import java.util.List;

public class CallNumberActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.public_one_dialog);
        mContext = this;
        screenOn();
        initView();
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

    private void onButtonClick(int buttonNum) {
        if (buttonNum == 1) {
            String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
            if (TextUtils.isEmpty(phontNu)) {
//                TextToSpeechUtils.getInstance(mContext,"www", TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                return;
            } else {
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
        } else if (buttonNum == 2) {
            String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
            if (TextUtils.isEmpty(phontNu)) {
//                TextToSpeechUtils.getInstance(mContext,"请设置亲情号码", TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                return;
            } else {
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
        } else if (buttonNum == 3) {
            String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
            if (TextUtils.isEmpty(phontNu)) {
//                TextToSpeechUtils.getInstance(mContext,"请设置亲情号码", TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                return;
            } else {
                PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                List<PhoneNumber.EachPhoneNumber> eachPhoneNumbers = phoneNumber.getItems();
                for (PhoneNumber.EachPhoneNumber eachPhoneNumber : eachPhoneNumbers) {
                    if (eachPhoneNumber.getPhoneType().equals("3")) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
//                            Uri data = Uri.parse("tel:" +"18309280898");
                        Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                        intent.setData(data);
                        startActivity(intent);
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
                PreferencesUtils.getInstance(mContext).setString("locationModeOld", PreferencesUtils.getInstance(mContext).getString("locationMode", "2"));
                PreferencesUtils.getInstance(mContext).setString("locationMode", "3");
                BaseSDK.getInstance().setPeriod(3 * 60);
                //计算结束时间 realTime
                long endTime = System.currentTimeMillis() + 30 * 60 * 1000;
                PreferencesUtils.getInstance(mContext).setLong("realTimeModeEnd", endTime);

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
        finish();
        overridePendingTransition(R.anim.up_to_down_in, R.anim.up_to_down_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.up_to_down_in, R.anim.up_to_down_exit);
    }
}