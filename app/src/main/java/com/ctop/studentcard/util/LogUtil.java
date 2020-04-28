package com.ctop.studentcard.util;
 
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shen on 2016/2/28.
 */
public class LogUtil {
    public static String tagPrefix = "";
    public static boolean showV = true;
    public static boolean showD = true;
    public static boolean showI = true;
    public static boolean showW = true;
    public static boolean showE = true;
    public static boolean writeInFile = true;

    /**
     * 得到tag（所在类.方法（L:行））
     * @return
     */
    private static String generateTag() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[0];
        String callerClazzName = stackTraceElement.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        String tag = "%s.%s(L:%d)";
        tag = String.format(tag, new Object[]{callerClazzName, stackTraceElement.getMethodName(), Integer.valueOf(stackTraceElement.getLineNumber())});
        //给tag设置前缀
        tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":" + tag;
        return tag;
    }

    public static void setShow(boolean isShow){
        showV = isShow;
        showD = isShow;
        showI = isShow;
        showW = isShow;
        showE = isShow;
        writeInFile = isShow;
    }
 
    public static void v(String msg) {
        if (showV) {
            String tag = generateTag();
            Log.v(tag, msg);
        }
    }
 
    public static void v(String msg, Throwable tr) {
        if (showV) {
            String tag = generateTag();
            Log.v(tag, msg, tr);
        }
    }
 
    public static void d(String msg) {
        if (showD) {
            String tag = generateTag();
            Log.d(tag, msg);
        }
    }
 
    public static void d(String msg, Throwable tr) {
        if (showD) {
            String tag = generateTag();
            Log.d(tag, msg, tr);
        }
    }
 
    public static void i(String msg) {
        if (showI) {
            String tag = generateTag();
            Log.i(tag, msg);
        }
    }
 
    public static void i(String msg, Throwable tr) {
        if (showI) {
            String tag = generateTag();
            Log.i(tag, msg, tr);
        }
    }
 
    public static void w(String msg) {
        if (showW) {
            String tag = generateTag();
            Log.w(tag, msg);
        }
    }
 
    public static void w(String msg, Throwable tr) {
        if (showW) {
            String tag = generateTag();
            Log.w(tag, msg, tr);
        }
    }
 
    public static void e(String msg) {
        if (showE) {
            String tag = generateTag();
            Log.e(tag, msg);
        }
    }
 
    public static void e(String msg, Throwable tr) {
        if (showE) {
            String tag = generateTag();
            Log.e(tag, msg, tr);
        }
    }


    public static void showLog(String tag, String msg) {
        //	boolean debug = true;//是否debug,true表示处于debug状态，不可输错日志
        if (showD) {
            Log.i(tag, msg);
        }
    }
    public static void writeInFile(Context context,String msg) {
        return;
//        if (writeInFile){
//            String  bath=getFileCrashPath(context);
//            LogUtil.d("===bath===" + bath);
//            PrintToFileUtil.input2File(msg,bath+File.separator+"log.txt");
//        }
    }
    public static final String getCachePath(Context context) {
        return Environment.getExternalStorageDirectory().toString() ;
    }
    public static final String getFileCrashPath(Context context) {
        String path = getCachePath(context) + File.separator + "aaaaaaa"+getMillisecondFormatTime(System.currentTimeMillis());

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }
    /**
     * 将返回的毫秒值转换为yyyy-MM-dd 时间格式
     *
     * @return
     */
    public final static String getMillisecondFormatTime(long datetiem) {

        Date date = new Date(datetiem);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

        return sdf.format(date);

    }
}