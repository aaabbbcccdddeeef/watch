package com.ctop.studentcard.feature.setting.about;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.util.DeviceUtil;
import com.ctop.studentcard.util.QRCodeUtil;

public class AboutActivity extends BaseActivity {


    private ImageView qr_image;
    private TextView tv_imei;
    private TextView version_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.about_layout);

        initView();
    }

    private void initView() {
        qr_image = findViewById(R.id.qr_image);
        tv_imei = findViewById(R.id.tv_imei);
        version_num = findViewById(R.id.version_num);

        String imei = DeviceUtil.getPhoneIMEI(this);

        qr_image.setImageBitmap(QRCodeUtil.createQRCode(imei,150));
        tv_imei.setText("imei："+imei);
        version_num.setText("版本号："+DeviceUtil.getVersionName());
    }



}
