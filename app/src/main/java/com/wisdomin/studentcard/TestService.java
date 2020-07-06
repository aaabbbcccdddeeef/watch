package com.wisdomin.studentcard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TestService extends Service {

    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "onCreate");

    }

    public IBinder onBind(Intent intent) {
        Log.d("onBind", "onBind");
        return null;
    }
}   