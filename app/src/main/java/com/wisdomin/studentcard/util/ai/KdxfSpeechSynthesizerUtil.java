package com.wisdomin.studentcard.util.ai;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;

import com.wisdomin.studentcard.util.LogUtil;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;


//科大讯飞的语音工具
public class KdxfSpeechSynthesizerUtil {


    private static Context mContext;
    private static KdxfSpeechSynthesizerUtil kdxfSpeechSynthesizerUtil;

    // 语音合成对象
    private static SpeechSynthesizer mSpeechSynthesizer;

    private KdxfSpeechSynthesizerUtil() {
    }

    public static void getInstance(Context context, String str) {
        try {

            mContext = context;
            if (kdxfSpeechSynthesizerUtil == null) {
                kdxfSpeechSynthesizerUtil = new KdxfSpeechSynthesizerUtil();
            }
            if (mSpeechSynthesizer == null) {
                // 初始化合成对象
                mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, mTtsInitListener);
            }
            //使用科大讯飞的语音播放
            // 设置参数
            setParam();
            mSpeechSynthesizer.startSpeaking(str, mTtsListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 默认本地发音人
    public static String voicerLocal = "xiaoyan";

    /**
     * 初始化监听。
     */
    public static InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            LogUtil.d("InitListener init() code = " + code);
        }
    };

    /**
     * 合成回调监听。
     */
    private static SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
            LogUtil.e("开始播放：");
        }

        @Override
        public void onSpeakPaused() {
//			showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
//			showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
//			mPercentForBuffering = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//			mPercentForPlaying = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                LogUtil.e("播放完成");
            } else if (error != null) {
                LogUtil.e(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                LogUtil.d("session id =" + sid);
            }

        }
    };


    /**
     * 参数设置
     */
    private static void setParam() {
        // 清空参数
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        //设置使用本地引擎
        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        mSpeechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
        //设置合成语速
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC + "");
        // 设置播放合成音频打断音乐播放，默认为true
        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }


    //获取发音人资源路径
    private static String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "tts";
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + voicerLocal + ".jet"));
        return tempBuffer.toString();
    }


}
