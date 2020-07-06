package com.wisdomin.studentcard.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.util.FinishActivityManager;
import com.wisdomin.studentcard.util.LogUtil;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    float x1 = 0.0F;

    float x2 = 0.0F;

    float y1 = 0.0F;

    float y2 = 0.0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;
        FinishActivityManager.getManager().addActivity(this);



    }



    //268435456
    protected void showHome() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
//            return true;//拦截返回键
//        } else {
//            return super.dispatchKeyEvent(event);
//        }
//    }

//
//    public class ScreenOffAdminReceiver extends DeviceAdminReceiver {
//        @Override
//        public void onEnabled(Context context, Intent intent) {
//            Log.d("1234", "设备管理器使用");
//        }
//
//        @Override
//        public void onDisabled(Context context, Intent intent) {
//            LogUtil.e("设备管理器没有使用");
//        }
//    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.back_top) {

            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从活动管理器删除当前Activity
        FinishActivityManager.getManager().finishActivity(this);
    }


    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (paramMotionEvent.getAction() == 0) {
            this.x1 = paramMotionEvent.getX();
        } else if (paramMotionEvent.getAction() == 1) {
            this.x2 = paramMotionEvent.getX();
            int i = ViewConfiguration.get((Context) this).getScaledTouchSlop();
            float f3 = (i * 10);
            if (this.x2 - this.x1 > f3) {
                LogUtil.e("finish is true");
                FinishActivityManager.getManager().currentActivity().finish();
                return true;
            }
        }
        return super.onTouchEvent(paramMotionEvent);
    }
}
