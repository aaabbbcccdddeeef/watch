package com.ctop.studentcard.feature.setting.sound;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

public class SoundActivity extends BaseActivity implements View.OnClickListener {

    public int[] MENU_ITEM_ICON = new int[]{R.drawable.volume0, R.drawable.volume1, R.drawable.volume2, R.drawable.volume3, R.drawable.volume4, R.drawable.volume5};

    private ImageView adjustVoice;

    private int currentVolume;

    private ImageView heighVoice;

    private ImageView lowerVoice;

    private AudioManager mAudioManager;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message param1Message) {
            if (SoundActivity.this.mMediaPlayer != null) {
                int currentPosition = SoundActivity.this.mMediaPlayer.getCurrentPosition();//记录音频文件播放的位置
                int duration = SoundActivity.this.mMediaPlayer.getDuration();
                LogUtil.e("position = "+currentPosition+" duration = "+duration);
                if (currentPosition + 100 >= duration)
                    SoundActivity.this.stopPlayRing();
            }
        }
    };


    public int mIsRingAndVibrate;

    public boolean mIsSilent;

    public boolean mIsSilentOrVibrate;

    public MediaPlayer mMediaPlayer;

//    private BroadcastReceiver mRingReceiver = new BroadcastReceiver() {
//        public void onReceive(Context param1Context, Intent param1Intent) {
//            if ("android.media.RINGER_MODE_CHANGED".equals(param1Intent.getAction())) {
//                int ringerMode = ((AudioManager)SoundActivity.this.getSystemService(Context.AUDIO_SERVICE)).getRingerMode();
//                LogUtil.e("ringerMode ="+ringerMode);
//                switch (ringerMode) {
//                    default:
//                        return;
//                    case 2:
//                        SoundActivity.this.mIsSilentOrVibrate = false;
//                        SoundActivity.this.mIsSilent = false;
//                        if (SoundActivity.this.currentVolume == 0) {
//                            SoundActivity.access$002(SoundActivity.this, SoundActivity.this.currentVolume + SoundActivity.this.mInitVolume);
//                            SoundActivity.this.mAudioManager.setStreamVolume(2, SoundActivity.this.currentVolume, 0);
//                            SoundActivity.this.mAudioManager.setStreamVolume(3, SoundActivity.this.currentVolume, 0);
//                            SoundActivity.this.adjustVoice.setImageResource(SoundActivity.this.MENU_ITEM_ICON[1]);
//                        }
//                        SoundActivity.this.choiceVolumeIndex(SoundActivity.this.currentVolume);
//                        Settings.System.putInt(SoundActivity.this.getContentResolver(), "vibrate_when_ringing", SoundActivity.this.mIsRingAndVibrate);
//                        return;
//                    case 1:
//                        SoundActivity.this.mIsSilentOrVibrate = true;
//                        SoundActivity.this.mIsSilent = false;
//                        SoundActivity.this.adjustVoice.setImageResource(SoundActivity.this.MENU_ITEM_ICON[0]);
//                        return;
//                    case 0:
//                        break;
//                }
//                SoundActivity.this.mIsSilentOrVibrate = true;
//                SoundActivity.this.mIsSilent = true;
//                SoundActivity.this.adjustVoice.setImageResource(SoundActivity.this.MENU_ITEM_ICON[0]);
//            }
//        }
//    };

    private Timer mTimer;

    private TimerTask mTimerTask;

    private int maxVolume;

    private int stepVolume;

    private void stopTimer() {
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
        if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
            this.mTimerTask = null;
        }
    }

    public void choiceVolumeIndex(int currentVolume) {
        if (currentVolume > 0 && currentVolume <= this.maxVolume) {
            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[currentVolume  / this.stepVolume ]);
            return;
        }
//        if (currentVolume == this.mInitVolume) {
//            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[1]);
//            return;
//        }
        if (currentVolume == 0)
            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[0]);
    }

    public Ringtone getDefaultRingtone(int paramInt) {
        return RingtoneManager.getRingtone((Context) this, RingtoneManager.getActualDefaultRingtoneUri((Context) this, paramInt));
    }

    public void initView() {
        this.lowerVoice = (ImageView) findViewById(R.id.lower);
        this.heighVoice = (ImageView) findViewById(R.id.heigh);
        this.adjustVoice = (ImageView) findViewById(R.id.volume_adjust);
        this.lowerVoice.setOnClickListener(this);
        this.heighVoice.setOnClickListener(this);

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.media.RINGER_MODE_CHANGED");
//        registerReceiver(this.mRingReceiver, intentFilter);
    }

    public void lowerVolume() {
        if (this.currentVolume > 0 && this.currentVolume <= this.maxVolume) {
            this.currentVolume -= this.stepVolume;
            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[this.currentVolume / this.stepVolume ]);
        } else if (this.currentVolume == 0) {
            this.currentVolume = 0;
            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[0]);
        }
        LogUtil.e("currentVolume ="+currentVolume);
        this.mAudioManager.setStreamVolume(AudioManager.STREAM_RING, this.currentVolume, 0);
        this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, this.currentVolume, 0);
    }

    @Override
    public void onClick(View paramView) {
        int id = paramView.getId();

        if (id == R.id.lower) {
            LogUtil.d("lower");
            lowerVolume();
            playRing();
            return;

        }

        if (id == R.id.heigh) {
            LogUtil.d("height");
            upVolume();
            playRing();
        }

    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.sound_layout);
        initView();
        setUpView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayRing();
//        unregisterReceiver(this.mRingReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayRing();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void playRing() {
        if (this.mMediaPlayer != null && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        try {
            this.mMediaPlayer = new MediaPlayer();
            this.mMediaPlayer.setDataSource(this, RingtoneManager.getActualDefaultRingtoneUri(this,  RingtoneManager.TYPE_RINGTONE));
//            this.mMediaPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring1));
            this.mMediaPlayer.prepare();
            this.mMediaPlayer.start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        startTimer();
    }

    public void setUpView() {
        this.mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        this.maxVolume = this.mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        this.currentVolume = this.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        this.stepVolume = maxVolume/5;
//        int ringerMode = this.mAudioManager.getRingerMode();
//        if (ringerMode == AudioManager.RINGER_MODE_SILENT || ringerMode ==AudioManager.RINGER_MODE_VIBRATE) {
//            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[0]);
//        } else {
            choiceVolumeIndex(this.currentVolume);
//        }
//        this.mIsRingAndVibrate = Settings.System.getInt(getContentResolver(), "vibrate_when_ringing", 0);

        LogUtil.e("maxVolume ="+maxVolume);
        LogUtil.e("currentVolume ="+currentVolume);
    }

    public void startTimer() {
        stopTimer();
        if (this.mTimer == null)
            this.mTimer = new Timer();
        if (this.mTimerTask == null)
            this.mTimerTask = new TimerTask() {
                public void run() {
                    try {
                        if (SoundActivity.this.mMediaPlayer == null)
                            return;
                        if (SoundActivity.this.mMediaPlayer.isPlaying())
                            SoundActivity.this.mHandler.sendEmptyMessage(0);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            };
        this.mTimer.schedule(this.mTimerTask, 0L, 100L);
    }

    public void stopPlayRing() {
        if (this.mMediaPlayer != null && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        stopTimer();
    }

    public void upVolume() {

        if (this.currentVolume < this.maxVolume) {
            this.currentVolume += this.stepVolume;
            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[this.currentVolume / this.stepVolume ]);
        } else if (this.currentVolume == this.maxVolume) {
            this.currentVolume = this.maxVolume;
            this.adjustVoice.setImageResource(this.MENU_ITEM_ICON[5]);
        }
        this.mAudioManager.setStreamVolume(AudioManager.STREAM_RING, this.currentVolume, 0);
        this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, this.currentVolume, 0);

        LogUtil.e("currentVolume ="+currentVolume);
    }
}
