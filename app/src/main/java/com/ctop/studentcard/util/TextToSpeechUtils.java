package com.ctop.studentcard.util;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class TextToSpeechUtils {

    private static TextToSpeech instance;


    public synchronized static TextToSpeech getInstance(Context context) {
        if (instance == null) {
            instance = initTextToSpeech(context);
        }
        return instance;
    }

    public void speak(){

    }

    private static TextToSpeech initTextToSpeech(final Context context) {
        // 参数Context,TextToSpeech.OnInitListener
        instance = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //初始化成功
                    LogUtil.e("初始化成功");
                } else {
                    LogUtil.e("status==="+status);
                    LogUtil.e("初始化失败");
//                    Toast.makeText(context, "初始化失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        instance.setPitch(1.0f);
        // 设置语速
        instance.setSpeechRate(0.7f);
//        new AudioMngHelper(context).setVoice100(100);

        toggleSpeaker(context,true);
        return instance;
    }


    public static void toggleSpeaker(Context context,boolean open) {
        try {
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
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

}
