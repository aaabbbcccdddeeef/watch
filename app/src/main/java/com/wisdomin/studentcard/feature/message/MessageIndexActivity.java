package com.wisdomin.studentcard.feature.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.adapter.SmsAdapter;
import com.wisdomin.studentcard.base.BaseActivity;
import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.greendao.DaoManager;
import com.wisdomin.studentcard.greendao.SmsMessage;
import com.wisdomin.studentcard.greendao.SmsMessageDao;


import java.util.List;

public class MessageIndexActivity extends BaseActivity {

    private SmsMessageDao smsMessageDao;
    private List<SmsMessage> smsMessageList;
    private RecyclerView recyclerView;
    private ImageView iv_empty;
    private ImageView back_top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_message);
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_message);
        iv_empty = findViewById(R.id.iv_empty);
        back_top = findViewById(R.id.back_top);
        back_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();

    }

    private void initData() {
        smsMessageDao = DaoManager.getInstance().getDaoSession().getSmsMessageDao();
        smsMessageList = smsMessageDao.queryBuilder().orderDesc(SmsMessageDao.Properties.Time).limit(20).list();
        if (smsMessageList.size() > 0) {
            iv_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            SmsAdapter adapter = new SmsAdapter(smsMessageList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickable(new SmsAdapter.OnItemClickable() {
                @Override
                public void onItemClick(int position) {
                    SmsMessage smsMessage = smsMessageList.get(position);
                    if(smsMessage.getStatus()==0){
                        BaseSDK.getInstance().send_report_sms_read(smsMessage.getUuid()+"@2");
                    }
                    Intent intent = new Intent(MessageIndexActivity.this, MessageDetailActivity.class);
                    intent.putExtra(MessageDetailActivity.UUID, smsMessage.getUuid());
                    startActivity(intent);
                }
            });
        } else {
            iv_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

}
