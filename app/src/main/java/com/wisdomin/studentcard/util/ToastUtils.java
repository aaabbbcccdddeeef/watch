package com.wisdomin.studentcard.util;

import android.content.Context;
import android.widget.Toast;

/**
 * ToastUtils 利用单例模式，解决Toast重复弹出的问题
 */
public class ToastUtils {
    private static ToastUtils mToastUtils;
    private static Toast mToast;

    private ToastUtils(Context context) {
        if (null == mToast) {
            mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG);
        }
    }

    public static ToastUtils getInstance(Context context) {
        if (mToastUtils == null) {
            mToastUtils = new ToastUtils(context.getApplicationContext());
        }
        return mToastUtils;
    }

    public void showShortToast(String mString) {
        if (mToast == null) {
            return;
        }
        mToast.setText(mString);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showLongToast(String mString) {
        if (mToast == null) {
            return;
        }
        mToast.setText(mString);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

}