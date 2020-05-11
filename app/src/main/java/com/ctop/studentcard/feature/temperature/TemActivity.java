package com.ctop.studentcard.feature.temperature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.broadcast.BroadcastConstant;
import com.ctop.studentcard.util.AlphaAnimationUtil;
import com.ctop.studentcard.util.AppConst;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.ai.KdxfSpeechSynthesizerUtil;


public class TemActivity extends BaseActivity implements View.OnClickListener {

    private PercentCircle percentCircle;
    private RippleView mRippleView;
    private RelativeLayout start_rl;
    private RelativeLayout tem_rl;
    private TextView tv_tem;
    private TemPostReceiver mTemPostReceiver;
    private ImageView iv_tem_list;
    private String valueTem;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                percentCircle.setVisibility(View.VISIBLE);
                percentCircle.setTargetPercent(100, mHandler);
                mRippleView.setVisibility(View.GONE);
                percentCircle.init();

            } else if (msg.what == 1) {

                AlphaAnimationUtil.startAlphaOut(percentCircle);
                percentCircle.setVisibility(View.GONE);
                AlphaAnimationUtil.startAlphaIn(percentCircle);
                tem_rl.setVisibility(View.VISIBLE);
                tv_tem.setText(valueTem);
                AlphaAnimationUtil.translationXIn(tem_rl);
                hiddenViews();
                iv_tem_list.setVisibility(View.VISIBLE);


            }
        }
    };

    private void hiddenViews(){
        percentCircle.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tem);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        percentCircle = findViewById(R.id.percentCircle);
        mRippleView = findViewById(R.id.rippleView);
        start_rl = findViewById(R.id.start_rl);
        tem_rl = findViewById(R.id.tem_rl);
        tv_tem = findViewById(R.id.tv_tem);
        iv_tem_list = findViewById(R.id.iv_tem_list);
        iv_tem_list.setVisibility(View.GONE);
        percentCircle.setVisibility(View.GONE);

        start_rl.setOnClickListener(this);
        tem_rl.setOnClickListener(this);
        iv_tem_list.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start_rl) {
            start_rl.setOnClickListener(null);
            KdxfSpeechSynthesizerUtil.getInstance(this,"开始测量，请稍等");
            //注册温度结果广播
            mTemPostReceiver = new TemPostReceiver();
            final IntentFilter intentFilter = new IntentFilter(BroadcastConstant.TEMPERATURE_RESULT_POST);
            registerReceiver(mTemPostReceiver, intentFilter);
            //发广播，请求测温
            sendBrodcast();
            iv_tem_list.setVisibility(View.GONE);

            //简单渐变动画
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);//渐变度从0到1
            alphaAnimation.setDuration(3000);//动画持续时间：2000毫秒
            start_rl.startAnimation(alphaAnimation);

            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    start_rl.setVisibility(View.GONE);
                    tem_rl.setVisibility(View.GONE);
                    mRippleView.setVisibility(View.VISIBLE);
                    mRippleView.setRadius(80, mHandler);
                    start_rl.setOnClickListener(TemActivity.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (id == R.id.tem_rl) {
            tem_rl.setOnClickListener(null);
            KdxfSpeechSynthesizerUtil.getInstance(this,"开始测量，请稍等");
            iv_tem_list.setVisibility(View.GONE);
            //发广播，请求测温
            sendBrodcast();
            //简单渐变动画
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);//渐变度从0到1
            alphaAnimation.setDuration(3000);//动画持续时间：2000毫秒
            tem_rl.startAnimation(alphaAnimation);

            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    start_rl.setVisibility(View.GONE);
                    tem_rl.setVisibility(View.GONE);

                    mRippleView.setVisibility(View.VISIBLE);
                    mRippleView.setRadius(80, mHandler);
                    mRippleView.setEndFlag(false);
                    mRippleView.init();
                    tem_rl.setOnClickListener(TemActivity.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (id == R.id.iv_tem_list) {
            startActivity(new Intent(this,TemListActivity.class));
        }
    }

    private void sendBrodcast() {
        AppConst.BY_STUDENT = true;
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.TEMPERATURE_START);
        sendBroadcast(intent);// 发送
        LogUtil.e("sendTemBrodcast");
    }

    class TemPostReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e("TemReceiver");
            String action = intent.getAction();
            if (action.equals(BroadcastConstant.TEMPERATURE_RESULT_POST)) {
                valueTem = intent.getStringExtra("value");
                LogUtil.e("valueTem===" + valueTem);
                tv_tem.setText(valueTem);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTemPostReceiver != null) {
            unregisterReceiver(mTemPostReceiver);
        }
    }

    private void scaleDown(View view, ScaleAnimation scaleAnimation) {

    }


}
