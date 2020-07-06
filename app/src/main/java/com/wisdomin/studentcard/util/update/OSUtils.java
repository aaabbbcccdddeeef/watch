package com.wisdomin.studentcard.util.update;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OSUtils {


    //安装apk
    public static void installApk(Context context, String apkPath) {
        Process process = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            // 请求root
            process = Runtime.getRuntime().exec("su");
            process = Runtime.getRuntime().exec("sh");

            out = process.getOutputStream();
            // 调用安装
            out.write(("pm install -r " + apkPath + "\n").getBytes());
//            in = process.getInputStream();
//            int len = 0;
//            byte[] bs = new byte[256];
//            while (-1 != (len = in.read(bs))) {
//                String state = new String(bs, 0, len);
//                if (state.equals("success\n")) {
//                    //安装成功后的操作
//
//                    //静态注册自启动广播
//                    Intent intent = new Intent();
//                    //与清单文件的receiver的anction对应
//                    intent.setAction("android.intent.action.PACKAGE_REPLACED");//当有包被更新的时候，系统会发出此广播
//                    //发送广播
//                    context.sendBroadcast(intent);
//                }
//            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



}
