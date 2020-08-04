package com.wisdomin.studentcard.feature.setting;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.api.OnReceiveListener;
import com.wisdomin.studentcard.base.BaseActivity;
import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.broadcast.BroadcastConstant;
import com.wisdomin.studentcard.feature.setting.about.AboutActivity;
import com.wisdomin.studentcard.feature.setting.sound.SoundActivity;
import com.wisdomin.studentcard.feature.setting.wallpaper.WallpaperActivity;
import com.wisdomin.studentcard.util.DeviceUtil;
import com.wisdomin.studentcard.util.LogUtil;

import java.util.Locale;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back_top;
    private RelativeLayout rl_voice;
    private RelativeLayout rl_wall_paper;
    private RelativeLayout rl_update_version;
    private RelativeLayout rl_about;
    private RelativeLayout rl_setting;

    private Context mContext;
    private UpdateReceiver mUpdateReceiver;

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContext = this;
        initView();
        textToSpeech=new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale language = textToSpeech.getLanguage();
                    LogUtil.e("getCountry=="+language.getCountry());
                    LogUtil.e("getLanguage=="+language.getLanguage());
//                    int result = textToSpeech.setLanguage(Locale.CHINA);
//                    if (result == TextToSpeech.LANG_MISSING_DATA
//                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        Toast.makeText(mContext, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(mContext, "支持", Toast.LENGTH_SHORT).show();
//
//                    }
                }
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        mUpdateReceiver = new UpdateReceiver();
        final IntentFilter intentFilter = new IntentFilter(BroadcastConstant.UPDATE);
        mContext.registerReceiver(mUpdateReceiver, intentFilter);
    }

    private void initView() {
        back_top = findViewById(R.id.back_top);
        back_top.setOnClickListener(this);
        rl_voice = findViewById(R.id.rl_voice);
        rl_wall_paper = findViewById(R.id.rl_wall_paper);
        rl_update_version = findViewById(R.id.rl_update_version);
        rl_about = findViewById(R.id.rl_about);
        rl_setting = findViewById(R.id.rl_setting);
        rl_voice.setOnClickListener(this);
        rl_wall_paper.setOnClickListener(this);
        rl_update_version.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_setting.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.rl_voice) {

            startActivity(new Intent(SettingActivity.this, SoundActivity.class));

        } else if (id == R.id.rl_wall_paper) {
            startActivity(new Intent(SettingActivity.this, WallpaperActivity.class));

        } else if (id == R.id.rl_update_version) {
            //电量
//            if (MainActivity.getSystemBattery(mContext) >= 50) {//电量大于50%
            //请求更新apk
            BaseSDK.getInstance().geUpdate("1@", new OnReceiveListener() {
                @Override
                public void onResponse(final String msg) {
                }
            });
            Toast.makeText(mContext, "正在检测新版本更新", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(mContext,"电量少于50%，无法更新",Toast.LENGTH_SHORT).show();
//            }
//            startActivity(new Intent(Settings.ACTION_SETTINGS));
        } else if (id == R.id.rl_about) {

            startActivity(new Intent(mContext, AboutActivity.class));

        } else if (id == R.id.back_top) {

            finish();
        } else if (id == R.id.rl_setting) {

//            startActivity(new Intent(mContext, SecondActivity.class));
//            startActivity(new Intent(Settings.ACTION_SETTINGS));
//            callPhone("10000");

//            startActivity(new Intent("com.android.settings.TTS_SETTINGS"));

            DeviceUtil.shutDowmMore(mContext);
        }
    }

    /**
     * 拨打电话（直接拨打电话）
     * @param phoneNum 电话号码
     */
    public void callPhone(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    public void test_engine(View view) {
                LogUtil.e("tv_time== OnClickListener");

                textToSpeech.speak("小羊智斗狐狸小羊一家住在河的旁边，而河对面住着一只坏狐狸。\n" , TextToSpeech.QUEUE_FLUSH, null);
//                textToSpeech.speak("哈哈哈，大家记得科技路反馈结果金风科技个接口", TextToSpeech.QUEUE_FLUSH, null,null);

    }


    class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e("onReceive: action: " + action);
            if (action.equals(BroadcastConstant.UPDATE)) {//Action

                boolean is_head = intent.getExtras().getBoolean("is_head");
                if (!is_head) {
                    Toast.makeText(mContext, "正在更新，请勿操作", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
        textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
        textToSpeech.shutdown(); // 关闭，释放资源
    }

    private void unregisterReceiver() {
        if (null != mUpdateReceiver) {
            mContext.unregisterReceiver(mUpdateReceiver);
        }
    }
}
