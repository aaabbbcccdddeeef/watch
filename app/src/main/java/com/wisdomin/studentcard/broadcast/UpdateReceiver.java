package com.wisdomin.studentcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wisdomin.studentcard.base.BaseApplication;
import com.wisdomin.studentcard.util.LogUtil;

public class UpdateReceiver extends BroadcastReceiver {
 
    private static final String TAG = UpdateReceiver.class.getSimpleName();
 
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {//接收升级广播
            LogUtil.showLog(TAG, "onReceive:升级了一个安装包，重新启动此程序");
            if (packageName.equals("package:" + BaseApplication.getPackageInfo().packageName)) {
                restartAPP(context);//升级完自身app,重启自身
            }
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {//接收安装广播
            LogUtil.showLog(TAG, "onReceive:安装了" + packageName);
            if (packageName.equals("package:" + BaseApplication.getPackageInfo().packageName)) {
                /*SystemUtil.reBootDevice();*/
            }
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) { //接收卸载广播
            LogUtil.showLog(TAG, "onReceive:卸载了" + packageName);
        }
    }


    /**
     * 重启整个APP
     * @param context
     */
    public static void restartAPP(Context context){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}