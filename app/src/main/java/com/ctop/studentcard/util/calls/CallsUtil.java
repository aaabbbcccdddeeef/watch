package com.ctop.studentcard.util.calls;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;

import com.ctop.studentcard.bean.CallPhoneBean;
import com.ctop.studentcard.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallsUtil {

    //获取通话记录
    public static void getContentCallLogNoRecieve(Context context, Handler mHandler) {
        Uri callUri = CallLog.Calls.CONTENT_URI;// 查询通话记录的URI
        String[] columns = {
                CallLog.Calls.CACHED_NAME, // 通话记录的联系人
                CallLog.Calls.NUMBER,// 通话记录的电话号码
                CallLog.Calls.DATE,// 通话记录的日期
                CallLog.Calls.DURATION,// 通话时长
                CallLog.Calls.TYPE};// 通话类型}
        List<CallPhoneBean> callList = new ArrayList();
        Cursor cursor = context.getContentResolver().query(callUri, columns
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近的最先显示
        );
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));  //姓名
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));  //号码
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); //获取通话日期
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
            String time = new SimpleDateFormat("HH:mm").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));//获取通话时长，值为多少秒
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)); //获取通话类型：1.呼入2.呼出3.未接
            String dayCurrent = new SimpleDateFormat("dd").format(new Date());
            String dayRecord = new SimpleDateFormat("dd").format(new Date(dateLong));

            if (type == 3) {
                mHandler.sendEmptyMessage(6);
                break;
            }
        }
        mHandler.sendEmptyMessage(7);
    }

}
