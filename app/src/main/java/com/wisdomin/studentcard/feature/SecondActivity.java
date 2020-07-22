package com.wisdomin.studentcard.feature;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.ToastUtils;


public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(0x80000000,0x80000000);
//        getWindow().addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.web_view);

        WebView mWebView = findViewById(R.id.webView);
        mWebView.loadUrl("https://www.baidu.com");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });

        //不使用缓存:
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

    }

//    SensorManager mSensorManager;
//    SensorEventListener sensorEventListener;
//
//    private Button mTemp_one;
//    private Button mTemp_zero;
//    private Button mTemp_count;
//    private Button mStep_one;
//    private Button mStep_zero;
//    private Button mStep_count;
//    private Button heart;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.second);
//
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//        mTemp_one = findViewById(R.id.temp_one);
//        mTemp_zero = findViewById(R.id.temp_zero);
//        mTemp_count = findViewById(R.id.temp_count);
//        mStep_one = findViewById(R.id.step_one);
//        mStep_zero = findViewById(R.id.step_zero);
//        mStep_count = findViewById(R.id.step_count);
//        heart = findViewById(R.id.heart);
//        mTemp_one.setOnClickListener(this);
//        mTemp_zero.setOnClickListener(this);
//        mTemp_count.setOnClickListener(this);
//        mStep_one.setOnClickListener(this);
//        mStep_zero.setOnClickListener(this);
//        mStep_count.setOnClickListener(this);
//        heart.setOnClickListener(this);
//
//
//    }
//
//    @Override
//    public void onClick(View view) {
//
//        switch (view.getId())
//        {
//            case R.id.temp_one:
//                NoteUtil.sendCommand(NoteUtil.V8_TEMPERATURE_NODE,"0");
//                LogUtil.e("V8_TEMPERATURE_NODE = " + NoteUtil.readFile(NoteUtil.V8_TEMPERATURE_NODE));
//                ToastUtils.getInstance(SecondActivity.this).showShortToast("温度："+NoteUtil.readFile(NoteUtil.V8_TEMPERATURE_NODE));
//
//                break;
//            case R.id.temp_zero:
//                NoteUtil.sendCommand(NoteUtil.V8_TEMPERATURE_NODE,"2");
//                LogUtil.e("V8_TEMPERATURE_NODE = " + NoteUtil.readFile(NoteUtil.V8_TEMPERATURE_NODE));
//                ToastUtils.getInstance(SecondActivity.this).showShortToast("温度："+NoteUtil.readFile(NoteUtil.V8_TEMPERATURE_NODE));
//
//                break;
//            case R.id.temp_count:
//                ToastUtils.getInstance(SecondActivity.this).showShortToast("温度："+NoteUtil.readFile(NoteUtil.V8_TEMPERATURE_VALUE));
////                LogUtil.e("V8_TEMPERATURE_VALUE = " + NoteUtil.readFile(NoteUtil.V8_TEMPERATURE_VALUE));
//                break;
//            case R.id.step_one:
//                NoteUtil.sendCommand(NoteUtil.V8_STEP_NODE,"1");
//                LogUtil.e("V8_STEP_NODE = " + NoteUtil.readFile(NoteUtil.V8_STEP_NODE));
//                ToastUtils.getInstance(SecondActivity.this).showShortToast("步数："+NoteUtil.readFile(NoteUtil.V8_STEP_NODE));
//
//                break;
//            case R.id.step_zero:
//                NoteUtil.sendCommand(NoteUtil.V8_STEP_NODE,"0");
//                LogUtil.e("V8_STEP_NODE = " + NoteUtil.readFile(NoteUtil.V8_STEP_NODE));
//                ToastUtils.getInstance(SecondActivity.this).showShortToast("步数："+NoteUtil.readFile(NoteUtil.V8_STEP_NODE));
//
//                break;
//            case R.id.step_count:
//                ToastUtils.getInstance(SecondActivity.this).showShortToast("步数："+NoteUtil.readFile(NoteUtil.V8_STEP_DATA));
//
////                LogUtil.e("V8_STEP_DATA = " + NoteUtil.readFile(NoteUtil.V8_STEP_DATA));
//                break;
//            case R.id.heart:
//                getSensor();
//                break;
//            default:
//                break;
//        }
//    }
//
//
//
//    //心率
//    public void getSensor(){
//        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
//        sensorEventListener = new SensorEventListener() {
//            public void onSensorChanged(SensorEvent e) {
//                LogUtil.e("e.values[0] = " + e.values[0]);
//                LogUtil.e("e.values[1] = " + e.values[1]);
//                LogUtil.e("e.values[2] = " + e.values[2]);
//                LogUtil.e("e.values[3] = " + e.values[3]);
//
//                ToastUtils.getInstance(SecondActivity.this).showShortToast("心率："+e.values[0]+"\n"+
//                        "高压："+e.values[1]+"\n"+
//                        "低压："+e.values[2]+"\n"+
//                        "血氧："+e.values[3]);
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//            }
//        };
//        mSensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_GAME);
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mSensorManager.unregisterListener(sensorEventListener);
//
//    }
}

