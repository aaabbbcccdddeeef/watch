package com.ctop.studentcard.feature.phone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.internal.telephony.ITelephony;
import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.broadcast.BroadcastConstant;
import com.ctop.studentcard.broadcast.BroadcastReceiverInCall;
import com.ctop.studentcard.feature.MainActivity;
import com.ctop.studentcard.netty.NettyClient;
import com.ctop.studentcard.util.Const;
import com.ctop.studentcard.util.KeyUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PackDataUtil;
import com.ctop.studentcard.util.PropertiesUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PhoneInActivity extends BaseActivity {

    private TextView in_phone_number;
    private TextView tv_operate;
    private RelativeLayout out_phone_off;
    private RelativeLayout out_phone_on;
    private TextView in_time;
    private long mCallAcceptedTime = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                toggleSpeaker(true);
                //开始计时
                tv_operate.setVisibility(View.GONE);
                counting();
            }else if(msg.what == 2){
                toggleSpeaker(true);
            }
        }
    };

    private void counting() {
        long l1 = System.currentTimeMillis();
        long l2 = this.mCallAcceptedTime;
        String str = (new SimpleDateFormat("mm:ss", Locale.CHINA)).format(new Date(l1 - l2));
        this.in_time.setText(str);
        this.in_time.setVisibility(View.VISIBLE);
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_in);
        initView();
    }

    private void initView() {
        in_phone_number = findViewById(R.id.in_phone_number);
        tv_operate = findViewById(R.id.tv_operate);
        out_phone_off = findViewById(R.id.out_phone_off);
        out_phone_on = findViewById(R.id.out_phone_on);
        in_time = findViewById(R.id.in_time);

        Bundle bundle = getIntent().getExtras();
        String phoneNumber = bundle.getString("phoneNumber");
        in_phone_number.setText(phoneNumber);

        screenOn();
        out_phone_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BroadcastReceiverInCall.rejectCall(PhoneInActivity.this);
                finish();
            }
        });
        out_phone_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerRingingCall();
                answerRinging(PhoneInActivity.this);
                out_phone_on.setVisibility(View.GONE);
                out_phone_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BroadcastReceiverInCall.rejectCall(PhoneInActivity.this);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mHandler.sendEmptyMessageDelayed(2, 1000L);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(1);
    }

    public boolean isBlueToothHeadsetConnected() {
        try {
            int i = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET);
            return (i != 0);
        } catch (Exception exception) {
            return true;
        }
    }


    public void screenOn() {
        PowerManager mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "com.kksxa.launcher:tag");
        mWakeLock.acquire();
        mWakeLock.release();
    }

    public void toggleSpeaker(boolean open) {
        try {
            AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if (isBlueToothHeadsetConnected()) {
                audioManager.setMode(3);
                audioManager.startBluetoothSco();
                audioManager.setBluetoothScoOn(true);
                audioManager.setSpeakerphoneOn(false);
                return;
            }
            audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
            audioManager.setSpeakerphoneOn(open);
            if(open){
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public void answerRinging(Context context) {
        try {
            TelephonyManager telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<TelephonyManager> c = TelephonyManager.class;

            // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
            Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            //允许访问私有方法
            mthEndCall.setAccessible(true);
            final Object obj = mthEndCall.invoke(telMag, (Object[]) null);

            // 再通过ITelephony对象去反射
            Method mt = obj.getClass().getMethod("answerRingingCall");
            //允许访问私有方法
            mt.setAccessible(true);
            mt.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接听电话
     */
    public void answerRingingCall() {
        openSpeaker();
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.answerRingingCall();
        } catch (Exception e) {
            e.printStackTrace();
            //4.1系统以后的电话接听
            answerRingingCall_4_1(PhoneInActivity.this);
        }
        mCallAcceptedTime = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(3,2000l);
    }

    public void openSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 4.1版本以上接听电话
     */
    private void answerRingingCall_4_1(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //模拟无线耳机的按键来接听电话
        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = "HTC".equalsIgnoreCase(Build.MANUFACTURER) && !audioManager.isWiredHeadsetOn();
        if (broadcastConnected) {
            broadcastHeadsetConnected(context);
        }
        try {
            try {
                Runtime.getRuntime().exec("input keyevent " + Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_HEADSETHOOK));
                context.sendOrderedBroadcast(btnDown, enforcedPerm);
                context.sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            LogUtil.i("answerRingingCall_4_1" );
            if (broadcastConnected) {
                broadcastHeadsetConnected(context);
            }
        }
    }
    /**
     * 对HTC的手机，需要进行一点特殊的处理，也就是通过广播的形式，让手机误以为连上了无线耳机。
     */
    private void broadcastHeadsetConnected(Context context) {
        LogUtil.i("htc broadcastHeadsetConnected" );
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", 0);
        i.putExtra("name", "mysms");
        try {
            context.sendOrderedBroadcast(i, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public boolean onKeyDown(int paramInt, KeyEvent event) {
//        LogUtil.e("paramInt："+paramInt);
//        if (event.getKeyCode() == 74){
//            if(mCallAcceptedTime!=0){//已经接听
//                return true;
//            }
//            answerRingingCall();
//            out_phone_off.setText("挂断");
//            out_phone_on.setVisibility(View.GONE);
//            out_phone_off.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    BroadcastReceiverInCall.rejectCall(PhoneInActivity.this);
//                    finish();
//                }
//            });
//        }
//        return super.onKeyDown(paramInt, event);
//    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
}
