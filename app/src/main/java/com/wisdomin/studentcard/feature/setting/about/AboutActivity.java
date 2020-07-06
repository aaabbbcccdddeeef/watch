package com.wisdomin.studentcard.feature.setting.about;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.base.BaseActivity;
import com.wisdomin.studentcard.util.DeviceUtil;
import com.wisdomin.studentcard.util.QRCodeUtil;

public class AboutActivity extends BaseActivity {


    private ImageView qr_image;
    private ImageView back_top;
    private TextView tv_imei;
    private TextView version_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        initView();
    }

    private void initView() {
        back_top = findViewById(R.id.back_top);
        qr_image = findViewById(R.id.qr_image);
        tv_imei = findViewById(R.id.tv_imei);
        version_num = findViewById(R.id.version_num);

        String imei = DeviceUtil.getPhoneIMEI(this);

        qr_image.setImageBitmap(QRCodeUtil.createQRCode(imei,150));
        tv_imei.setText("imei："+imei);
        version_num.setText("版本号："+DeviceUtil.getVersionName());
        back_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}
