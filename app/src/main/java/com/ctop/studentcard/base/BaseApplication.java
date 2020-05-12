package com.ctop.studentcard.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import androidx.multidex.MultiDex;

import com.ctop.studentcard.R;
import com.ctop.studentcard.api.Iback;
import com.ctop.studentcard.api.InstructionCallBack;
import com.ctop.studentcard.bean.SmsMessageReceive;
import com.ctop.studentcard.broadcast.BatteryBroadcastReceiver;
import com.ctop.studentcard.broadcast.BroadcastConstant;
import com.ctop.studentcard.broadcast.CallActiveReceiver;
import com.ctop.studentcard.feature.MainActivity;
import com.ctop.studentcard.greendao.DaoManager;
import com.ctop.studentcard.netty.NettyClient;
import com.ctop.studentcard.util.GSMCellLocation;
import com.ctop.studentcard.util.GpsUtil;
import com.ctop.studentcard.util.LogUtil;
import com.ctop.studentcard.util.PreferencesUtils;
import com.ctop.studentcard.util.PropertiesUtil;
import com.ctop.studentcard.broadcast.BroadcastReceiverInCall;
import com.ctop.studentcard.broadcast.TimeTickReceiver;
import com.ctop.studentcard.service.ServerService;
import com.ctop.studentcard.observer.CallLogObserver;
import com.ctop.studentcard.observer.CallTimeObserver;
import com.ctop.studentcard.util.ai.KdxfSpeechSynthesizerUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;


public class BaseApplication extends Application implements InstructionCallBack {

    private static Context mContext;

    private BatteryBroadcastReceiver batteryBroadcastReceiver;
    private SmsSelfReceiver smsSelfReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        // 初始化MultiDex
        MultiDex.install(this);
        Intent intent = new Intent(this, ServerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        startService(intent);
        LogUtil.e("BaseApplication:thread:name:" + Thread.currentThread().getName());

        //注册接收下发的监听
//        if (NetworkUtil.isAvailable(mContext)) {
            BaseSDK.getInstance().setBaseContext(getApplicationContext());
            BaseSDK.getInstance().setOnInstructionListener(this);
//        }
        //监听通话记录
        setCallListner();
        setCallTimeListner();
        initBrocastReciver();
        //是否要初始化Preferences，第一次初始化，以后不初始化
        boolean initProperties = PreferencesUtils.getInstance(mContext).getBoolean("initProperties", false);
        if (!initProperties) {
            PropertiesUtil.getInstance().initProperties(mContext);
            PreferencesUtils.getInstance(mContext).setBoolean("initProperties", true);
        }
        initGreenDao();
        GSMCellLocation.getSignalStrengths(mContext);//手机网络信号强度
        GpsUtil.getInstence(mContext);//手机gps信号强度
        initMsc();
    }
    private void initMsc() {
        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误

        StringBuffer param = new StringBuffer();
        param.append("appid="+getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(this, param.toString());
    }
    private void initGreenDao() {
        DaoManager mManager = DaoManager.getInstance();
        mManager.init(this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {//SUPERPASS
                PreferencesUtils.getInstance(mContext).setBoolean("IOTSUPERPASS", true);
                new CountDownTimer(30 * 60 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        //组装报文
                        PreferencesUtils.getInstance(mContext).setBoolean("IOTSUPERPASS", false);
                    }
                }.start();
            } else if (msg.what == 1) {//WAKEUP
                BaseSDK.getInstance().connect();
            } else if (msg.what == 2) {//setSERVER
                String param = (String) msg.obj;
                //第一位：固定SETSEVER
                //第二位：1第三位参数为 IP地址  0 第三位参数为域名
                //第三位：ip地址或者域名
                //第四位：端口
                String[] strings = param.split(",");
                if (strings[1].equals("1")) {
                    PropertiesUtil.getInstance().setProperties(mContext, "host", strings[2]);
                    PropertiesUtil.getInstance().setProperties(mContext, "tcp_port", strings[3]);
                }else {
                    PropertiesUtil.getInstance().setProperties(mContext, "host", strings[2]);
                    PropertiesUtil.getInstance().setProperties(mContext, "tcp_port", "0");
                }
                //重启tcp
                NettyClient.getInstance(mContext).stopTcp();
                NettyClient.getInstance(mContext).connect();

            } else if (msg.what == 3) {//RESTART

            } else if (msg.what == 4) {//STATUS
                //Server：online/offline
                //Battery：10%
                //Singal Level：strong/normal/weak 50dBm~0dBm   90dBm~-60dBm  >90dBm
                //GPS：Sleep/on
                //GPRS:connection/Disconnection
                String Server = BaseSDK.getInstance().getConnectStatus() ? "online" : "offline";
                String Battery = MainActivity.getSystemBattery(mContext) + "";
                String SingalLevel = "strong";
                if (GSMCellLocation.lastStrength < -90) {
                    SingalLevel = "weak";
                } else if (GSMCellLocation.lastStrength > -50) {
                    SingalLevel = "strong";
                } else {
                    SingalLevel = "normal";
                }
                String GPS = "";
                LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    GPS = "Sleep";
                } else {
                    GPS = "on";
                }
                String data = Server + "," + Battery + "%," + SingalLevel + "," + GPS+","+GpsUtil.signalCont;
                String number = (String) msg.obj;
                BaseSDK.getInstance().sendSMS(mContext,number,data);
            } else if (msg.what == 5) {//语音播报并屏幕显示

                SmsMessageReceive smsMessageReceive = (SmsMessageReceive) msg.obj;
//                TextToSpeechUtils.getInstance(mContext,"通知，" + smsMessageReceive.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
                KdxfSpeechSynthesizerUtil.getInstance(mContext,"通知，" + smsMessageReceive.getMessage());
                //发送广播 屏幕显示
                sendScreenBrocast(smsMessageReceive.getUuid(), smsMessageReceive.getSmsType());
            }
        }
    };


    //发送广播 屏幕显示
    private void sendScreenBrocast(String uuid, String smsType) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.SHOW_SCREEN_SMS);
        intent.putExtra("uuid", uuid);
        intent.putExtra("smsType", smsType);

        mContext.sendBroadcast(intent);// 发送
        LogUtil.e("sendScreenBrocast ok");
    }

    private void initBrocastReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        TimeTickReceiver timeTickReceiver = new TimeTickReceiver();
        mContext.registerReceiver(timeTickReceiver, filter);

        BroadcastReceiverInCall broadcastReceiverInCall = new BroadcastReceiverInCall();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        intentFilter.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(broadcastReceiverInCall, intentFilter);

        CallActiveReceiver callActiveReceiver = new CallActiveReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.ctop.studentcard.call.active");
        intentFilter1.setPriority(Integer.MAX_VALUE);
        mContext.registerReceiver(callActiveReceiver, intentFilter1);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        mContext.registerReceiver(batteryBroadcastReceiver, filter1);

        //收到短信后，发送广播，这里接收
        IntentFilter filterSms = new IntentFilter();
        filterSms.addAction(BroadcastConstant.SMS_STATE);
        smsSelfReceiver = new SmsSelfReceiver();
        mContext.registerReceiver(smsSelfReceiver, filterSms);

    }

    private void setCallListner() {
        Uri uri = Uri.parse("content://call_log/calls");
        final int handlerWhat = 0;
        getContentResolver().registerContentObserver(uri, true, new CallLogObserver(mContext, handlerWhat, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == handlerWhat) {
                    LogUtil.e("handleMessage");
                    String data = (String) msg.obj;
                    BaseSDK.getInstance().sendCallRecord(data);
                }
            }
        }));
    }

    //设置通话时长限制
    private void setCallTimeListner() {
        Uri uri = Uri.parse("content://call_log/calls");
        final int handlerWhat = 1;
        getContentResolver().registerContentObserver(uri, true, new CallTimeObserver(mContext, handlerWhat, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == handlerWhat) {
                    LogUtil.e("handleMessage");
                    int durationLong = (int) msg.obj;
                    PreferencesUtils.getInstance(mContext).setInt("callTimeLongAlready", durationLong);
                }
            }
        }));
    }

    public static Context getAppContext() {
        return mContext;
    }

    /**
     * 根据下发的指令，响应server收到指令，然后再去，操作设备，
     *
     * @param downData 收到的下发的指令
     * @param iback    给server响应的接口对象
     */
    @Override
    public void operateTerminal(String downData, Iback iback) {
        LogUtil.d("server下发的指令：" + downData);
        //本地处理完数据以后，给server的响应
        //调用iback接口的returnToServer(String ret)方法，向server发送响应数据
        iback.returnToServer("resp+aaa");
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public static PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info == null) {
            info = new PackageInfo();
        }
        return info;
    }


    class SmsSelfReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e("onReceive: action: " + action);
            if (action.equals(BroadcastConstant.SMS_STATE)) {//Action
                int what = intent.getExtras().getInt("what");
                LogUtil.e("what===" + what);
                if (what == 0) {
                    mHandler.sendEmptyMessage(0);
                } else if (what == 1) {
                    mHandler.sendEmptyMessage(1);
                } else if (what == 2) {//SETSERVER
                    String param = intent.getExtras().getString("param");
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.obj = param;
                    mHandler.sendMessage(msg);
                } else if (what == 3) {
                    mHandler.sendEmptyMessage(3);
                } else if (what == 4) {
                    String number = intent.getExtras().getString("number");
                    Message msg = Message.obtain();
                    msg.what = 4;
                    msg.obj = number;
                    mHandler.sendMessage(msg);
                } else if (what == 5) {

                    String uuid = intent.getExtras().getString("uuid");
                    String message = intent.getExtras().getString("message");
                    String smsType = intent.getExtras().getString("smsType");

                    SmsMessageReceive smsMessageReceive = new SmsMessageReceive();
                    smsMessageReceive.setUuid(uuid);
                    smsMessageReceive.setMessage(message);
                    smsMessageReceive.setSmsType(smsType);

                    Message msg = Message.obtain();
                    msg.what = 5;
                    msg.obj = smsMessageReceive;

                    mHandler.sendMessage(msg);
                }
            }
        }
    }

}

