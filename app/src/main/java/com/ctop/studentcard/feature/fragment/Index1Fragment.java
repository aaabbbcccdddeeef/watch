package com.ctop.studentcard.feature.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ctop.studentcard.R;
import com.ctop.studentcard.util.DeviceUtil;
import com.ctop.studentcard.util.GSMCellLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Index1Fragment extends Fragment {

    private Context mContext;
    public static final int DELAY_MILLIS = 1000;

    public static final int FOUR_HOUR = 14400000;

    private static final int WHAT_TIME = 1;

    private TextView tv_mobile_company, tv_time, tv_date, tv_week;

    private SimpleDateFormat format1 = new SimpleDateFormat("E", Locale.CHINA);

    private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.CHINA);

    private SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);


    private Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message param1Message) {
            if (param1Message.what == 1)
                Index1Fragment.this.handleTime();
            return false;
        }
    });


    private void handleTime() {
        Date date = new Date();
        this.tv_time.setText(this.format2.format(date));
        this.tv_date.setText(this.format3.format(date));
        this.tv_week.setText(this.format1.format(date));
        this.mHandler.sendEmptyMessageDelayed(1, 1000L);
    }


    public View onCreateView(LayoutInflater layoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        View view = layoutInflater.inflate(R.layout.fragment_1, paramViewGroup, false);
        mContext = getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_mobile_company = view.findViewById(R.id.tv_mobile_company);
        tv_time = view.findViewById(R.id.tv_time);
        tv_date = view.findViewById(R.id.tv_date);
        tv_week = view.findViewById(R.id.tv_week);

        if (DeviceUtil.noSim(mContext)) {
            this.tv_mobile_company.setText("未插卡");
        } else {
            String state = GSMCellLocation.getNetType(mContext);
            if (state.equals("1")) {
                this.tv_mobile_company.setText("中国移动");
            } else if (state.equals("2")) {
                this.tv_mobile_company.setText("中国联通");
            } else if (state.equals("3")) {
                this.tv_mobile_company.setText("中国电信");
            }
        }

    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onPause() {
        super.onPause();
        this.mHandler.removeMessages(1);
    }

    public void onResume() {
        super.onResume();
        this.mHandler.sendEmptyMessage(1);
    }

    public void onViewCreated(@NonNull View paramView, @Nullable Bundle paramBundle) {
        super.onViewCreated(paramView, paramBundle);
    }


}
