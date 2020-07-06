package com.wisdomin.studentcard.feature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.bean.ClassModel;
import com.wisdomin.studentcard.bean.PhoneNumber;
import com.wisdomin.studentcard.broadcast.BroadcastConstant;
import com.wisdomin.studentcard.feature.menu.MenuIndexActivity;
import com.wisdomin.studentcard.feature.nlpchat.AIUIActivity;
import com.wisdomin.studentcard.feature.setting.wallpaper.WallpaperActivity;
import com.wisdomin.studentcard.util.AppConst;
import com.wisdomin.studentcard.util.DeviceUtil;
import com.wisdomin.studentcard.util.GSMCellLocation;
import com.wisdomin.studentcard.util.JsonUtil;
import com.wisdomin.studentcard.util.KeyUtil;
import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;
import com.wisdomin.studentcard.util.TimeUtils;
import com.wisdomin.studentcard.util.ToastUtils;
import com.wisdomin.studentcard.util.ai.KdxfSpeechSynthesizerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    KeyUtil keyUtil;


    private Context mContext;
    private TextView buttery;
    private ImageView signalStrength;
    private ImageView new_phone;
    private ImageView new_sms;
    private TextView signalStrengthText;
    private LinearLayout main_ll;
    float x1 = 0.0F;
    float x2 = 0.0F;
    float y1 = 0.0F;
    float y2 = 0.0F;
    private TextView  tv_time, tv_date, tv_week;

    private SimpleDateFormat format1 = new SimpleDateFormat("E", Locale.CHINA);

    private SimpleDateFormat format2 = new SimpleDateFormat("HH:mm", Locale.CHINA);

    private SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);


    private void handleTime() {
        Date date = new Date();
        this.tv_time.setText(this.format2.format(date));
        this.tv_date.setText(this.format3.format(date));
        this.tv_week.setText(this.format1.format(date));
        this.mHandler.sendEmptyMessageDelayed(5, 1000L);
    }

    String toastStr = "禁止呼出";
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
                if (TextUtils.isEmpty(phontNu)) {
//                    SpeechSynthesizerUtil.getInstance(mContext).startSynthesizer("请设置亲情号码");
                    KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                    return;
                } else {
                    if(needRejectCall()){
                        Toast.makeText(mContext,toastStr,Toast.LENGTH_LONG).show();
                    }else {
                        PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                        List<PhoneNumber.EachPhoneNumber> eachPhoneNumbers = phoneNumber.getItems();
                        for (PhoneNumber.EachPhoneNumber eachPhoneNumber : eachPhoneNumbers) {
                            if (eachPhoneNumber.getPhoneType().equals("1")) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                                intent.setData(data);
                                startActivity(intent);
                            }
                        }
                    }
                }
            } else if (msg.what == 2) {
                String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
                if (TextUtils.isEmpty(phontNu)) {
                    KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                    return;
                } else {
                    if(needRejectCall()){
                        Toast.makeText(mContext,"课堂时间内无法拨打电话",Toast.LENGTH_LONG).show();
                    }else {
                        PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                        List<PhoneNumber.EachPhoneNumber> eachPhoneNumbers = phoneNumber.getItems();
                        for (PhoneNumber.EachPhoneNumber eachPhoneNumber : eachPhoneNumbers) {
                            if (eachPhoneNumber.getPhoneType().equals("2")) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                                intent.setData(data);
                                startActivity(intent);
                            }
                        }
                    }
                }
            } else if (msg.what == 3) {
                String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
                if (TextUtils.isEmpty(phontNu)) {
                    KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置亲情号码");
                    return;
                } else {
                    if(needRejectCall()){
                        Toast.makeText(mContext,"课堂时间内无法拨打电话",Toast.LENGTH_LONG).show();
                    }else {
                        PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                        List<PhoneNumber.EachPhoneNumber> eachPhoneNumbers = phoneNumber.getItems();
                        for (PhoneNumber.EachPhoneNumber eachPhoneNumber : eachPhoneNumbers) {
                            if (eachPhoneNumber.getPhoneType().equals("3")) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                Uri data = Uri.parse("tel:" + eachPhoneNumber.getPhoneNumber());
                                intent.setData(data);
                                startActivity(intent);
                            }
                        }
                    }
                }
            } else if (msg.what == 4) {
                BaseSDK.getInstance().send_report_sos();
                String phontNu = PreferencesUtils.getInstance(mContext).getString("phoneNumber", "");
                if (TextUtils.isEmpty(phontNu)) {
                    KdxfSpeechSynthesizerUtil.getInstance(mContext,"请设置S O S号码");
                    return;
                } else {
                    PhoneNumber phoneNumber = JsonUtil.parseObject(phontNu, PhoneNumber.class);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:" + phoneNumber.getSosNumber());
                    intent.setData(data);
                    startActivity(intent);
                    //上报设备模式
                    LogUtil.e("上报设备模式8");
                    BaseSDK.getInstance().send_device_status("3");
                    //设置30分钟的实时模式
                    PreferencesUtils.getInstance(mContext).setString("locationModeOld", PreferencesUtils.getInstance(mContext).getString("locationMode",  AppConst.MODEL_BALANCE));
                    PreferencesUtils.getInstance(mContext).setString("locationMode", AppConst.MODEL_REAL_TIME);
                    BaseSDK.getInstance().setPeriod(3 * 60);
                    //计算结束时间 realTime
                    long endTime = System.currentTimeMillis() + 30 * 60 * 1000;
                    PreferencesUtils.getInstance(mContext).setLong("realTimeModeEnd", endTime);
                }
            } else if (msg.what == 5) {
                handleTime();
            } else if (msg.what == 6) {//有未接
                new_phone.setVisibility(View.VISIBLE);
            } else if (msg.what == 7) {//没有未接
                new_phone.setVisibility(View.GONE);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        mContext = this;
        initView();

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        initData();

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        }
    }

    private void initData() {
        PreferencesUtils.getInstance(mContext).setString("locationMode",  AppConst.MODEL_BALANCE);//初始化：定位模式：平衡模式
        PreferencesUtils.getInstance(mContext).setLong("locationModeStart", System.currentTimeMillis());//初始化：定位模式：开始时间
    }


    private void initView() {
        main_ll = findViewById(R.id.main_ll);

        buttery = findViewById(R.id.buttery);
        signalStrength = findViewById(R.id.signalStrength);
        new_phone = findViewById(R.id.new_phone);
        new_sms = findViewById(R.id.new_sms);
        signalStrengthText = findViewById(R.id.signalStrengthText);
        buttery.setText(getSystemBattery(mContext) + "%");

        tv_time = findViewById(R.id.tv_time);
        tv_date = findViewById(R.id.tv_date);
        tv_week = findViewById(R.id.tv_week);


//        showIndex();
    }

    /**
     * 实时获取电量
     */
    public static int getSystemBattery(Context context) {
        int level = 0;
        Intent batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        level = batteryInfoIntent.getIntExtra("level", 0);
        int batterySum = batteryInfoIntent.getIntExtra("scale", 100);
        int percentBattery = 100 * level / batterySum;

        return percentBattery;
    }
//
//    private void showIndex() {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.container, new Index1Fragment());
//        fragmentTransaction.commit();
//    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            this.x1 = motionEvent.getX();
            this.y1 = motionEvent.getY();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

            this.x2 = motionEvent.getX();
            this.y2 = motionEvent.getY();
            int i = ViewConfiguration.get(this).getScaledTouchSlop();
            LogUtil.i("getScaledTouchSlop==="+i);
            float f3 = (i * 10);
            LogUtil.e("x1===" + x1);
            LogUtil.e("x2===" + x2);
            LogUtil.e("f3===" + f3);
            if (this.x1 - this.x2 > f3) {
                if(inclassmode()){
                    ToastUtils.getInstance(mContext).showShortToast("禁用时段");
//                    Toast.makeText(mContext,"禁用时段",Toast.LENGTH_LONG).show();
                    return true;
                }else {
                    showFeatureMenu();
                    return true;
                }
            }
            if (this.y1 - this.y2 > f3) {
                startActivity(new Intent(MainActivity.this, CallNumberActivity.class));
                overridePendingTransition(R.anim.down_to_up_in, R.anim.down_to_up_exit);
                return true;
            }
            if (this.x2 - this.x1 > f3) {
                if(inclassmode()){
                    ToastUtils.getInstance(mContext).showShortToast("禁用时段");
//                    Toast.makeText(mContext,"禁用时段",Toast.LENGTH_LONG).show();
                    return true;
                }else {
                    startActivity(new Intent(MainActivity.this, AIUIActivity.class));
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    return true;
                }
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void showFeatureMenu() {
//        if (isClassMode()) {
//            Toast.makeText((Context)this, "课堂模式中", Toast.LENGTH_LONG).show();
//            return true;
//        }
        startActivity(new Intent((Context) this, MenuIndexActivity.class));
        overridePendingTransition( R.anim.main_in,R.anim.aiui_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(5);
        registerReceiver(this);
        int wall_selected_index = PreferencesUtils.getInstance(mContext).getInt("wall_selected_index", 0);
        main_ll.setBackground(getResources().getDrawable(WallpaperActivity.WALL_PAPER_ITEM_ICON[wall_selected_index]));
        //未接电话
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                CallsUtil.getContentCallLogNoRecieve(mContext, mHandler);
//            }
//        }).start();
        //未读短信
//        SmsMessageDao smsMessageDao = DaoManager.getInstance().getDaoSession().getSmsMessageDao();
//        List<SmsMessage> smsMessageList = smsMessageDao.queryBuilder().where(SmsMessageDao.Properties.Status.eq(0)).orderDesc(SmsMessageDao.Properties.Time).list();
//        if (smsMessageList.size() > 0) {
//            new_sms.setVisibility(View.VISIBLE);
//        } else {
//            new_sms.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this);
        this.mHandler.removeMessages(5);
    }

    //
    private ButtueryReceiver buttueryReceiver = null;
    private StrengthReceiver strengthReceiver = null;
    private Reject_call_out_receiver reject_call_out_receiver;

    private void registerReceiver(Context context) {
        LogUtil.i("registerButtueryReceiver");
        buttueryReceiver = new ButtueryReceiver();
        final IntentFilter homeFilter = new IntentFilter(BroadcastConstant.BUTTERY_STATE);
        context.registerReceiver(buttueryReceiver, homeFilter);

        LogUtil.i("registerStrengthReceiver");
        strengthReceiver = new StrengthReceiver();
        final IntentFilter strengthFilter = new IntentFilter(BroadcastConstant.LASTSTRENGTH_STATE);
        context.registerReceiver(strengthReceiver, strengthFilter);

        buttery.setText(DeviceUtil.getBattery());

    }

    private void unregisterReceiver(Context context) {
        LogUtil.i("unregisterButtueryReceiver");
        if (null != buttueryReceiver) {
            context.unregisterReceiver(buttueryReceiver);
        }
        LogUtil.i("unregisterStrengthReceiver");
        if (null != strengthReceiver) {
            context.unregisterReceiver(strengthReceiver);
        }
        if (null != reject_call_out_receiver) {
            context.unregisterReceiver(reject_call_out_receiver);
        }
    }

    @Override
    public void onInit(int status) {

    }

    class ButtueryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e("onReceive: buttery==" + action);
            if (action.equals(BroadcastConstant.BUTTERY_STATE)) {//Action
                buttery.setText(intent.getExtras().getString("state"));
            }
        }
    }


    class StrengthReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            LogUtil.e("onReceive: signalStrength: ===" + intent.getExtras().getInt("signalStrength"));
            if (action.equals(BroadcastConstant.LASTSTRENGTH_STATE)) {//Action
                String state = GSMCellLocation.getNetType(mContext);
                if (state.equals("1")) {
                    signalStrengthText.setText("中国移动");
                } else if (state.equals("2")) {
                    signalStrengthText.setText("中国联通");
                } else if (state.equals("3")) {
                    signalStrengthText.setText("中国电信");
                }else {
                    signalStrengthText.setText("未插卡");
                    signalStrength.setWillNotDraw(true);
                    return;
                }
                signalStrength.setWillNotDraw(false);
                //>=-90是ok的
                int signalStrenghtint = intent.getExtras().getInt("signalStrength");
                LogUtil.e( "onReceive: signalStrenghtint: " + signalStrenghtint);
                if (signalStrenghtint != 0) {
                    if (signalStrenghtint <= -110) {
                        signalStrength.setImageResource(R.drawable.weak);
                    } else if (signalStrenghtint > -90) {
                        signalStrength.setImageResource(R.drawable.strong);
                    } else {
                        signalStrength.setImageResource(R.drawable.normal);
                    }
                }

            }
        }
    }

    // 监听KeyEventKey.KEYCODE_ENTER键（右下角确定键）,当此键按下的时候，隐藏输入法软键盘，编写相关逻辑。
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        //物理键确定，并且是按下的行为（避免两次响应）
        if (event.getKeyCode() == 74 || event.getKeyCode() == 4 || event.getKeyCode() == 23 || event.getKeyCode() == 7) {
            keyUtil = new KeyUtil(MainActivity.this, mHandler);
            keyUtil.dispatchKeyEvent(event);
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (keyUtil != null) {
            keyUtil.removeLongPressCallback();
        }
    }

    class Reject_call_out_receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e( "onReceive: action: " + action);
            if (action.equals(BroadcastConstant.REJECT_CALL_OUT)) {//Action
                String msg = intent.getExtras().getString("msg");
                Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean needRejectCall(){
        if(inclassmode()){
            return true;
        }
        //通话时长
        int callTimeLongAlready = PreferencesUtils.getInstance(mContext).getInt("callTimeLongAlready", 0);
        String callSetting = PreferencesUtils.getInstance(mContext).getString("callSetting", "");
        int callTimeLong = PreferencesUtils.getInstance(mContext).getInt("callTimeLong", -1);
        if ("1".equals(callSetting)){
            if (-1 != callTimeLong) {
                if (callTimeLongAlready >= callTimeLong) {
                    toastStr = "您本月的通话时长已用完";
                    return true;
                }
            }
        }
        return false;
    }

    private boolean inclassmode(){
        //课堂模式
        String classModelString = PreferencesUtils.getInstance(mContext).getString("classModel", "");
        ClassModel classModel = JsonUtil.parseObject(classModelString, ClassModel.class);
        if (classModel != null) {
            if (classModel.getItems().size() > 0) {
                List<ClassModel.ItemsBean> itemsBeanList = classModel.getItems();
                for (int i = 0; i < itemsBeanList.size(); i++) {
                    if ("0".equals(itemsBeanList.get(i).getIsEffect())) continue;//不生效
                    List<ClassModel.ItemsBean.PeriodBean> periodBeans = itemsBeanList.get(i).getPeriod();
                    for (int j = 0; j < periodBeans.size(); j++) {
                        if (TimeUtils.getWeekInCome().equals(periodBeans.get(j).getWeek())) {
                            int awaitstartInt = Integer.parseInt(itemsBeanList.get(i).getStartTime());
                            int awaitendInt = Integer.parseInt(itemsBeanList.get(i).getEndTime());
                            String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                            int timeNowInt = Integer.parseInt(timeNow);
                            if (awaitstartInt < awaitendInt) {//同一天
                                if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {
                                    toastStr = "您正处于课堂模式时间，呼出已受限";
                                    return true;
                                }
                            } else {//不同天
                                if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                                    toastStr = "您正处于课堂模式时间，呼出已受限";
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
