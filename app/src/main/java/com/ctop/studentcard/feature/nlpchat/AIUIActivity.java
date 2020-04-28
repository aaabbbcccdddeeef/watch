package com.ctop.studentcard.feature.nlpchat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ctop.studentcard.R;
import com.ctop.studentcard.util.LogUtil;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AIUIActivity extends Activity implements View.OnClickListener {
    private static String TAG = AIUIActivity.class.getSimpleName();

    //录音权限
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private Toast mToast;
//    private TextView mNlpText;

    private AIUIAgent mAIUIAgent = null;

    //交互状态
    private int mAIUIState = AIUIConstant.STATE_IDLE;

    private ImageView speak_iv_wait;//默认
    private ImageView speak_iv_ing;//说话
    private ImageView speak_iv_analyze;//分析
    private ImageView speak_iv_result;//结果
    private TextView tv_speak_result;
    private TextView tv_tip;


    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aiui_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initLayout();

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        requestPermission();


        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

        toggleSpeaker(true);
    }

    /**
     * 初始化Layout。
     */
    private void initLayout() {
//        findViewById(R.id.nlp_start).setOnClickListener(this);

//        mNlpText = findViewById(R.id.nlp_text);

        speak_iv_wait = findViewById(R.id.speak_iv_wait);
        speak_iv_ing = findViewById(R.id.speak_iv_ing);
        speak_iv_analyze = findViewById(R.id.speak_iv_analyze);
        speak_iv_result = findViewById(R.id.speak_iv_result);
        tv_speak_result = findViewById(R.id.tv_speak_result);
        tv_tip = findViewById(R.id.tv_tip);
        speak_iv_wait.setClickable(true);
        speak_iv_wait.setOnClickListener(this);
        speak_iv_result.setClickable(true);
        speak_iv_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!checkAIUIAgent()) {
            return;
        }

        switch (view.getId()) {
            // 开始语音理解
            case R.id.speak_iv_wait:
                cancelSynthesizer();//停止播放
                noShake();
                shakeSpeaking();
                startVoiceNlp();
                break;
            default:
                break;
        }
    }

    /**
     * 读取配置
     */
    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }

    private boolean checkAIUIAgent() {
        if (null == mAIUIAgent) {
            Log.i(TAG, "create aiui agent");

            //创建AIUIAgent
            mAIUIAgent = AIUIAgent.createAgent(this, getAIUIParams(), mAIUIListener);
        }
        if (null == mAIUIAgent) {
//            final String strErrorTip = "创建 AIUI Agent 失败！";
//            showTip(strErrorTip);
            LogUtil.e( "创建 AIUI Agent 失败！");
//            this.mNlpText.setText(strErrorTip);
        }

        return null != mAIUIAgent;
    }

    //打断
    public void cancelSynthesizer() {
        Log.d(TAG, "cancel pressed");
        if( null != mTts ) {
            mTts.stopSpeaking();
        }
    }

    //开始录音
    private void startVoiceNlp() {
        tv_speak_result.setText("");
        LogUtil.e( "start voice nlp");
//        mNlpText.setText("");

        // 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
        // 默认为oneshot 模式，即一次唤醒后就进入休眠，如果语音唤醒后，需要进行文本语义，请将改段逻辑copy至startTextNlp()开头处
        if (AIUIConstant.STATE_WORKING != this.mAIUIState) {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }

        // 打开AIUI内部录音机，开始录音
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(writeMsg);
    }

    //AIUI事件监听器
    private AIUIListener mAIUIListener = new AIUIListener() {

        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_WAKEUP:
                    //唤醒事件
                    Log.i(TAG, "on event: " + event.eventType);
                    showTip("进入识别状态");
                    break;

                case AIUIConstant.EVENT_RESULT: {
                    //结果事件
                    Log.i(TAG, "on event: " + event.eventType);
                    try {
                        JSONObject bizParamJson = new JSONObject(event.info);
                        JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                        JSONObject params = data.getJSONObject("params");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if (content.has("cnt_id")) {
                            String cnt_id = content.getString("cnt_id");
                            JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));

                            String sub = params.optString("sub");
                            LogUtil.e("sub==="+sub);
                            JSONObject result = cntJson.optJSONObject("intent");
                            if ("nlp".equals(sub) && result.length() > 2) {
                                // 解析得到语义结果
                                String str = "";
                                //在线语义结果
                                if (result.optInt("rc") == 0) {
                                    JSONObject answer = result.optJSONObject("answer");
                                    if (answer != null) {
                                        str = answer.optString("text");
                                    }
                                } else {
                                    str = "rc4，无法识别";
                                }
                                if (!TextUtils.isEmpty(str)) {
//                                    mNlpText.append("\n");
//                                    mNlpText.append(str);
//                                    mNlpText.setText(str);
//                                    SpeechSynthesizerUtil.getInstance(AIUIActivity.this).startSynthesizer(str);
                                    showResult();
                                    tv_speak_result.setText(str);
                                    //使用科大讯飞的语音播放
                                    // 设置参数
                                    setParam();
                                    mTts.startSpeaking(str, mTtsListener);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        LogUtil.e( "错误: " +e.getLocalizedMessage());
//                        mNlpText.append("\n");
//                        mNlpText.append(e.getLocalizedMessage());
                    }

//                    mNlpText.append("\n");
                }
                break;

                case AIUIConstant.EVENT_ERROR: {
                    //错误事件
                    LogUtil.e( "EVENT_ERROR: " + event.arg1 + "\n" + event.info);
//                    mNlpText.append("\n");
//                    mNlpText.append("错误: " + event.arg1 + "\n" + event.info);
                }
                break;

                case AIUIConstant.EVENT_VAD: {
                    //vad事件
                    if (AIUIConstant.VAD_BOS == event.arg1) {
                        //找到语音前端点
                        showTip("找到vad_bos找到语音前端点");
                    } else if (AIUIConstant.VAD_EOS == event.arg1) {
                        //找到语音后端点
                        showTip("找到vad_eos找到语音后端点");
                    } else {
                        showTip("" + event.arg2);
                    }
                }
                break;

                case AIUIConstant.EVENT_START_RECORD: {
                    //开始录音事件
                    Log.i(TAG, "on event: " + event.eventType);
                    showTip("开始录音");
                }
                break;

                case AIUIConstant.EVENT_STOP_RECORD: {
                    //停止录音事件
                    Log.i(TAG, "on event: " + event.eventType);
                    showTip("停止录音");
                    shakeAnalyze();
                }
                break;

                case AIUIConstant.EVENT_STATE: {    // 状态事件
                    mAIUIState = event.arg1;

                    if (AIUIConstant.STATE_IDLE == mAIUIState) {
                        // 闲置状态，AIUI未开启
                        showTip("闲置状态，AIUI未开启");
                    } else if (AIUIConstant.STATE_READY == mAIUIState) {
                        // AIUI已就绪，等待唤醒
                        showTip("AIUI已就绪，等待唤醒");
                    } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                        // AIUI工作中，可进行交互
                        showTip("AIUI工作中，可进行交互");
                    }
                }
                break;


                default:
                    break;
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != this.mAIUIAgent) {
            AIUIMessage stopMsg = new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, null, null);
            mAIUIAgent.sendMessage(stopMsg);

            this.mAIUIAgent.destroy();
            this.mAIUIAgent = null;
        }
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        finish();
        overridePendingTransition(R.anim.main_in,R.anim.aiui_out);
    }

    private void showTip(final String str) {
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                mToast.setText(str);
//                mToast.show();
//            }
//        });
    }


    //申请录音权限
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    this.finish();
                }
            }
        }
    }

    private List<Animator> anims = new ArrayList<>();

    //开始说话
    @SuppressLint("ResourceType")
    public void shakeSpeaking() {
        speak_iv_wait.setVisibility(View.GONE);
        speak_iv_analyze.setVisibility(View.GONE);
        speak_iv_ing.setVisibility(View.VISIBLE);
        Animator anim = AnimatorInflater.loadAnimator(this, R.anim.shake_anim);
        anim.setTarget(speak_iv_ing);
        anim.start();
        anims.add(anim);
    }

    //分析
    @SuppressLint("ResourceType")
    public void shakeAnalyze() {
        speak_iv_wait.setVisibility(View.GONE);
        speak_iv_ing.setVisibility(View.GONE);
        speak_iv_analyze.setVisibility(View.VISIBLE);
        Animator anim = AnimatorInflater.loadAnimator(this, R.anim.shake_anim);
        anim.setTarget(speak_iv_analyze);
        anim.start();
        anims.add(anim);
    }


    //停止图片震动
    public void noShake() {
        if (anims.size() > 0) {
            for (int i = 0; i < anims.size(); i++) {
                Animator animIndex = anims.get(i);
                if (animIndex != null) {
                    animIndex.end();
                }
                anims.remove(anims.get(i));
            }
        }
    }

    //显示结果
    private void showResult() {
        speak_iv_wait.setVisibility(View.GONE);
        speak_iv_ing.setVisibility(View.GONE);
        speak_iv_analyze.setVisibility(View.GONE);
        speak_iv_result.setVisibility(View.VISIBLE);
        tv_tip.setVisibility(View.GONE);
        tv_speak_result.setVisibility(View.VISIBLE);
        resultShake();
    }

    public void resultShake() {
        TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(100);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        speak_iv_result.startAnimation(animation);
    }

    private void reset() {
        noShake();
        tv_tip.setVisibility(View.VISIBLE);
        speak_iv_wait.setVisibility(View.VISIBLE);
        speak_iv_ing.setVisibility(View.GONE);
        speak_iv_analyze.setVisibility(View.GONE);
        speak_iv_result.clearAnimation();
        speak_iv_result.setVisibility(View.GONE);
        tv_speak_result.setVisibility(View.GONE);
        cancelSynthesizer();//停止播放
    }

    //语音合成
    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认本地发音人
    public static String voicerLocal="xiaoyan";

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
        }
    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

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
                Log.d(TAG, "session id =" + sid);
            }

        }
    };



    public void toggleSpeaker(boolean open) {
        try {
            AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
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

    /**
     * 参数设置
     */
    private void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置使用本地引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }


    //获取发音人资源路径
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        String type= "tts";
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type+"/common.jet"));
        tempBuffer.append(";");
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + voicerLocal + ".jet"));
        return tempBuffer.toString();
    }


    float x1= 0.0F;
    float x2= 0.0F;
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (paramMotionEvent.getAction() == 0) {
            x1 = paramMotionEvent.getX();
        } else if (paramMotionEvent.getAction() == 1) {
            x2 = paramMotionEvent.getX();
            int i = ViewConfiguration.get( this).getScaledTouchSlop();
            float f3 = (i * 10);
            if (  x1 - x2 > f3) {
                LogUtil.e("finish is true");
                finish();
                overridePendingTransition(R.anim.main_in,R.anim.aiui_out);

                return true;
            }
        }
        return super.onTouchEvent(paramMotionEvent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.main_in,R.anim.aiui_out);
    }
}
