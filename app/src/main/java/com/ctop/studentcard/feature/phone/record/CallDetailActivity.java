package com.ctop.studentcard.feature.phone.record;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.adapter.CallRecordAdapter;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.bean.CallPhoneBean;
import com.ctop.studentcard.bean.PhoneNumber;
import com.ctop.studentcard.greendao.DaoManager;
import com.ctop.studentcard.greendao.SmsMessage;
import com.ctop.studentcard.greendao.SmsMessageDao;
import com.ctop.studentcard.util.JsonUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;
import com.ctop.studentcard.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class CallDetailActivity extends BaseActivity {

    public static String NAME = "name";
    public static String STATE = "state";
    public static String PHONE = "phone";
    public static String TIME = "time";
    private String name,state,phone,time;
    private Bundle bundle;
    Intent intent;

    private Context mContext;
    private TextView tv_name,tv_status,tv_phone,tv_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_detail);
        mContext = this;
        intent = getIntent();
        getBundle();
        initview();
    }

    private void getBundle() {
        bundle = intent.getExtras();
        if (bundle.containsKey(NAME)) {
            name = bundle.getString(NAME);
        }
        if (bundle.containsKey(STATE)) {
            state = bundle.getString(STATE);
        }
        if (bundle.containsKey(PHONE)) {
            phone = bundle.getString(PHONE);
        }
        if (bundle.containsKey(TIME)) {
            time = bundle.getString(TIME);
        }
    }

    private void initview() {
        tv_name = findViewById(R.id.tv_name);
        tv_status = findViewById(R.id.tv_status);
        tv_phone = findViewById(R.id.tv_phone);
        tv_time = findViewById(R.id.tv_time);

        tv_name.setText(name);
        tv_status.setText(state);
        tv_phone.setText(phone);
        tv_time.setText(TimeUtils.changeChaToLongToString(time));
    }


}
