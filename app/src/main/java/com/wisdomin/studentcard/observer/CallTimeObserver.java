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

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 通话时长限制
 */
public class CallTimeObserver extends ContentObserver {
    private Context mContext;
    private Handler mHandler;
    private int handlerWhat;
    //重写构造方法，传入一个号码
    public CallTimeObserver(Context context, int handlerWhat, Handler handler) {
        super(handler);
        this.mContext = context;
        this.mHandler = handler;
        this.handlerWhat = handlerWhat;
    }

    //数据发生变化时调用
    @Override
    public void onChange(boolean selfChange) {
        LogUtil.i("数据库内容变化，产生呼叫记录");
        startQuery();
        super.onChange(selfChange);
    }


    /**
     * 读取数据
     *
     * @return 读取到的数据
     */
    private void startQuery() {
        String dateNowMonth = new SimpleDateFormat("yyyyMM").format(new Date(System.currentTimeMillis()));
        int durationLong=0;
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
        while (cursor.moveToNext()) {
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("yyyyMM").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            if(date.equals(dateNowMonth)){

                switch (type) {
                    case CallLog.Calls.INCOMING_TYPE:
                        //"打入"
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        //"打出"
                        durationLong += duration;
                        break;
//                case CallLog.Calls.MISSED_TYPE:
//                    //"未接"
//                    break;
                    default:
                        break;
                }
            }else {
                break;
            }
        }
        cursor.close();
        Message msg = Message.obtain();
        msg.what = handlerWhat;
        msg.obj = durationLong;
        mHandler.sendMessage(msg);
    }

}
