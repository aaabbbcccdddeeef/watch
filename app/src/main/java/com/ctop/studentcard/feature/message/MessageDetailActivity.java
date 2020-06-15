package com.ctop.studentcard.feature.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.greendao.DaoManager;
import com.ctop.studentcard.greendao.SmsMessage;
import com.ctop.studentcard.greendao.SmsMessageDao;

import java.util.List;


public class MessageDetailActivity extends BaseActivity {

    public static String UUID = "uuid";
    private String uuid;
    private Bundle bundle;
    Intent intent;
    private SmsMessageDao smsMessageDao;
    private List<SmsMessage> smsMessageList;
    private ImageView back_top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        intent = getIntent();
        smsMessageDao =  DaoManager.getInstance().getDaoSession().getSmsMessageDao();
        getBundle();
        initview();
    }

    private void getBundle() {
        bundle = intent.getExtras();
        if (bundle.containsKey(UUID)) {
            uuid = bundle.getString(UUID);
        }
    }

    private void initview() {
        if(!TextUtils.isEmpty(uuid)){
            smsMessageList =   smsMessageDao.queryBuilder().where(SmsMessageDao.Properties.Uuid.eq(uuid)).orderDesc(SmsMessageDao.Properties.Time).limit(20).list();
//            smsMessageList = commonDaoUtils.queryByNativeSql(" where uuid = ? order by time desc",new String[]{uuid});
        }
        if(smsMessageList.size()>0){
            ((TextView)findViewById(R.id.tv_content)).setText(smsMessageList.get(0).getMessage());
            updateStatus(smsMessageList.get(0));
        }
        back_top = findViewById(R.id.back_top);
        back_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //修改已读
    private void updateStatus(SmsMessage smsMessage){
        smsMessage.setStatus(1);
        smsMessageDao.update(smsMessage);
    }


}
