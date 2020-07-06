package com.wisdomin.studentcard.feature.phone.record;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.adapter.CallRecordAdapter;
import com.wisdomin.studentcard.base.BaseActivity;
import com.wisdomin.studentcard.bean.CallPhoneBean;
import com.wisdomin.studentcard.bean.PhoneNumber;
import com.wisdomin.studentcard.util.JsonUtil;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallRecordActivity extends BaseActivity {

    private RecyclerView rv_call_record;
    private ImageView iv_empty;
    private Context mContext;
    List<CallPhoneBean> callList = new ArrayList();
    PhoneNumber phoneNumber;
    CallRecordAdapter callRecordAdapter;
    private ImageView back_top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call_record);
        mContext = this;
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        rv_call_record = findViewById(R.id.rv_call_record);
        iv_empty = findViewById(R.id.iv_empty);
        back_top = findViewById(R.id.back_top);
        back_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        //查找情亲号码和sos号码
        String phoneNumberString = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
        phoneNumber = JsonUtil.parseObject(phoneNumberString, PhoneNumber.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getContentCallLog();
            }
        }).start();


    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (callList.size() > 0) {
                    iv_empty.setVisibility(View.GONE);
                    rv_call_record.setVisibility(View.VISIBLE);
                    callRecordAdapter = new CallRecordAdapter(callList);
                    rv_call_record.setAdapter(callRecordAdapter);
                    callRecordAdapter.notifyDataSetChanged();
                    callRecordAdapter.setOnCallItemClickListener(new CallRecordAdapter.OnCallItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            CallPhoneBean callPhoneBean = callList.get(position);
//                            Intent intent = new Intent(CallRecordActivity.this, CallDetailActivity.class);
//                            intent.putExtra(CallDetailActivity.NAME, callPhoneBean.getName());
//                            intent.putExtra(CallDetailActivity.PHONE, callPhoneBean.getNumber());
//                            intent.putExtra(CallDetailActivity.STATE, callPhoneBean.getState());
//                            intent.putExtra(CallDetailActivity.TIME, callPhoneBean.getTime());
//                            startActivity(intent);
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            Uri data = Uri.parse("tel:" + callPhoneBean.getNumber());
                            intent.setData(data);
                            startActivity(intent);
                            //更新已读状态
                            updateStatus(callPhoneBean.getNumber());
                        }
                    });
                } else {
                    iv_empty.setVisibility(View.VISIBLE);
                    rv_call_record.setVisibility(View.GONE);
                }
            }
        }

    };

    //修改通话记录
    private void updateStatus(String phoneStr){
        ContentValues content = new ContentValues();
//        content.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);
//        content.put(CallLog.Calls.NUMBER,phoneStr);
        content.put(CallLog.Calls.TYPE, 2);
//        content.put(CallLog.Calls.NEW, "1");//0已看1未看
        getContentResolver().update(CallLog.Calls.CONTENT_URI, content,CallLog.Calls.NUMBER+"=?" , new String[]{phoneStr});
    }

    private Uri callUri = CallLog.Calls.CONTENT_URI;// 查询通话记录的URI
    // 查询通话记录 具体字段
    private String[] columns = {
            CallLog.Calls.CACHED_NAME, // 通话记录的联系人
            CallLog.Calls.NUMBER,// 通话记录的电话号码
            CallLog.Calls.DATE,// 通话记录的日期
            CallLog.Calls.DURATION,// 通话时长
            CallLog.Calls.TYPE};// 通话类型}


    //获取通话记录
    private void getContentCallLog() {
        Cursor cursor = getContentResolver().query(callUri, columns
                , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近的最先显示
        );
        LogUtil.e("cursor count:" + cursor.getCount());
        callList.clear();
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

//            LogUtil.e( "Call log: " + "\n"
//                    + "name: " + name + "__"
//                    + "date: " + date + "__"
//                    + "time: " + time + "__"
//                    + "duration: " + duration + "__"
//                    + "type: " + type + "__"
//                    + "dayCurrent: " + dayCurrent + "__"
//                    + "dayRecord: " + dayRecord + "__"
//                    + "phone number: " + number + "\n"
//
//            );
            if (callList.size() == 0) {
                CallPhoneBean callPhoneBean = new CallPhoneBean();
                callPhoneBean.setNumber(number);
                callPhoneBean.setTime(date);
                callPhoneBean.setState(type);
                callPhoneBean.setName(changeName(number));
                callList.add(callPhoneBean);
            } else {
                boolean flagHas = false;
                for (int i = 0; i < callList.size(); i++) {
                    //是否已经包含
                    CallPhoneBean callPhoneBean = callList.get(i);
                    if (callPhoneBean.getNumber().equals(number)) {
                        flagHas = true;
                    }
                }
                //不包含，添加进list
                if (!flagHas) {
                    CallPhoneBean callPhoneBean = new CallPhoneBean();
                    callPhoneBean.setNumber(number);
                    callPhoneBean.setTime(date);
                    callPhoneBean.setState(type);
                    callPhoneBean.setName(changeName(number));
                    callList.add(callPhoneBean);
                }
            }
        }
        cursor.close();
        for (int i = 0; i < callList.size(); i++) {
            //是否已经包含
            CallPhoneBean callPhoneBean = callList.get(i);
            LogUtil.e("callPhoneBean:" + callPhoneBean.toString());
        }

        mHandler.sendEmptyMessage(1);
    }

    private String changeName(String number) {
        String sname = "未知";
        if (phoneNumber != null) {
            List<PhoneNumber.EachPhoneNumber> list = phoneNumber.getItems();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhoneType().equals(number)) {
                    sname = "亲情号码1";
                } else if (list.get(i).getPhoneType().equals(number)) {
                    sname = "亲情号码1";
                } else if (list.get(i).getPhoneType().equals(number)) {
                    sname = "亲情号码1";
                }
            }
            if (phoneNumber.getSosNumber().equals(number)) {
                sname = "sos号码";
            }
        }
        return sname;
    }

}
