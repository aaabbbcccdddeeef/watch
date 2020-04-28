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
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.internal.telephony.ITelephony;
import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.broadcast.BroadcastConstant;
import com.ctop.studentcard.broadcast.BroadcastReceiverInCall;
import com.ctop.studentcard.util.LogUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhoneOutActivity extends BaseActivity {

    private TextView out_phone_number;
    private TextView tv_operate;
    private TextView out_phone_drop;
    private TextView in_time;
    private long mCallAcceptedTime = 0;


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                toggleSpeaker(true);
            }else if (msg.what == 1) {
                counting();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_out);
        initView();
        this.mHandler.sendEmptyMessageDelayed(0, 1500L);
    }

    private void initView() {
        out_phone_number = findViewById(R.id.out_phone_number);
        tv_operate = findViewById(R.id.tv_operate);
        out_phone_drop = findViewById(R.id.out_phone_drop);
        in_time = findViewById(R.id.in_time);

        Bundle bundle = getIntent().getExtras();
        String phoneNumber = bundle.getString("phoneNumber");
        out_phone_number.setText(phoneNumber);
        out_phone_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BroadcastReceiverInCall.rejectCall(PhoneOutActivity.this);
                finish();
            }
        });
    }


    private void counting() {
        long l1 = System.currentTimeMillis();
        long l2 = this.mCallAcceptedTime;
        String str = (new SimpleDateFormat("mm:ss", Locale.CHINA)).format(new Date(l1 - l2));
        this.in_time.setText(str);
        this.in_time.setVisibility(View.VISIBLE);
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
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


    public boolean isBlueToothHeadsetConnected() {
        try {
            int i = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET);
            return (i != 0);
        } catch (Exception exception) {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerHomeKeyReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterHomeKeyReceiver(this);
        mHandler.removeMessages(1);
    }

    private HomeWatcherReceiver mHomeKeyReceiver = null;

    private void registerHomeKeyReceiver(Context context) {
        LogUtil.i( "registerHomeKeyReceiver");
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(BroadcastConstant.PHONE_STATE);
        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private void unregisterHomeKeyReceiver(Context context) {
        LogUtil.i( "unregisterHomeKeyReceiver");
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }


    class HomeWatcherReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e( "onReceive: action: " + action);
            if (action.equals(BroadcastConstant.PHONE_STATE)) {//Action
                String state = intent.getExtras().getString("state");
                if(state.equals("hook")){
                    answerRingingCall();
                    out_phone_drop.setText("挂断");
                    out_phone_drop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BroadcastReceiverInCall.rejectCall(PhoneOutActivity.this);
                            finish();
                        }
                    });
                }
            }
        }
    }

    /**
     * 接听电话
     */
    public void answerRingingCall() {
//        OpenSpeaker();
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.answerRingingCall();
        } catch (Exception e) {
            e.printStackTrace();
            //4.1系统以后的电话接听
            answerRingingCall_4_1(PhoneOutActivity.this);
        }
        mCallAcceptedTime = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(1,2000l);
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
