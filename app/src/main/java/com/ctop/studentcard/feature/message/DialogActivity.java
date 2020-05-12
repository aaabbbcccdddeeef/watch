package com.ctop.studentcard.feature.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseSDK;

import java.util.Timer;
import java.util.TimerTask;

public class DialogActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sms_dialog);
        //亮屏
        screenOn();
        initView();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        final String uuid = bundle.getString("uuid");
        String smsType = bundle.getString("smsType");
        TextView tv_title = findViewById(R.id.sms_title);
        TextView public_see = findViewById(R.id.public_see);
        tv_title.setText(smsType);
        public_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseSDK.getInstance().send_report_sms_read(uuid+"@2");
                Intent intent = new Intent(DialogActivity.this, MessageDetailActivity.class);
                intent.putExtra("uuid", uuid);
                startActivity(intent);
                finish();
            }
        });
        TextView public_close = findViewById(R.id.public_close);
        public_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public  PowerManager.WakeLock mWakeLock = null;
    public void screenOn() {
        PowerManager mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "com.ctop.studentcard:tag");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "com.ctop.studentcard:mywakelocktag");

        mWakeLock.acquire();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWakeLock!=null){
            mWakeLock.release();
        }

    }
}
