package com.ctop.studentcard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.util.NetworkUtil;


/**
 * Created by user on 2016/10/27.
 */

public class ServerService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (NetworkUtil.isAvailable(this)) {
            BaseSDK.getInstance().init(getApplicationContext());
//        }
        return Service.START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        NettyServer.getInstance().shutDown();

    }


}
