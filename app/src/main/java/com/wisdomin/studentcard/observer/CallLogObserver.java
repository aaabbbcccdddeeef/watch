package com.wisdomin.studentcard.observer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;

import androidx.core.content.ContextCompat;

import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 上报通话记录
 */
public class CallLogObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;
    private int handlerWhat;
    //重写构造方法，传入一个号码
    public CallLogObserver(Context context,int handlerWhat, Handler handler) {
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
        this.handlerWhat = handlerWhat;
    }

    //数据发生变化时调用
    @Override
    public void onChange(boolean selfChange) {
        LogUtil.i("数据库内容变化，产生呼叫记录");
//        deleteCallLog(incomingNumber);
////删除后注销观察者
//        getContentResolver().unregisterContentObserver(this);

        startQuery();
        super.onChange(selfChange);
    }


    /**
     * 读取数据
     *
     * @return 读取到的数据
     */
    private String startQuery() {
        // 1.获得ContentResolver
        ContentResolver  resolver = mContext.getContentResolver();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
        }
        // 2.利用ContentResolver的query方法查询通话记录数据库
        /**
         * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
         * @param projection 需要查询的字段
         * @param selection sql语句where之后的语句
         * @param selectionArgs ?占位符代表的数据
         * @param sortOrder 排序方式
         */

        Uri uri=CallLog.Calls.CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近的最先显示
        );
        // 3.通过Cursor获得数据
       String retStr = "";
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String typeString = "";
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    //"打入"
                    typeString = "0";
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    //"打出"
                    typeString = "1";
                    break;
//                case CallLog.Calls.MISSED_TYPE:
//                    //"未接"
//                    typeString = "未接";
//                    break;
                default:
                    break;
            }
            //计算结束时间
            String endTime= TimeUtils.getEndTime(dateLong,duration);
            //目标电话号码@开始时间!结束时间@通话时长@呼入呼出
            // 0呼入1呼出  通话时长单位：秒

            //18785192785@20180718121221!20180718121258@10@0
            retStr = number+"@"+date+"!"+endTime+"@"+duration+"@"+typeString;
//
//            Map<String, String> map = new HashMap<>();
//            //"未备注联系人"
//            map.put("name", (name == null) ? "未备注联系人" : name);//姓名
//            map.put("number", number);//手机号
//            map.put("date", date);//通话日期
//            // "分钟"
//            map.put("duration", (duration / 60) + "分钟");//时长
//            map.put("type", typeString);//类型
//            map.put("time", time);//通话时间
//            map.put("day", dayString);//
//            map.put("time_lead", TimeStampUtil.compareTime(date));//
            Message msg = Message.obtain();
            msg.what = handlerWhat;
            msg.obj = retStr;
            mHandler.sendMessage(msg);
            break;
        }
        cursor.close();
        return retStr;
    }

}
