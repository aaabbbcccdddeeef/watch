package com.wisdomin.studentcard.feature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.wisdomin.studentcard.R;


public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(0x80000000,0x80000000);
//        getWindow().addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.second);


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() ==  KeyEvent.KEYCODE_HOME){
            startActivity(new Intent(SecondActivity.this, MainActivity.class));
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        System.out.println(" -->onKeyDown: keyCode: " + keyCode);
        if (KeyEvent.KEYCODE_HOME == keyCode) {
            System.out.println("HOME has been pressed yet ...");
        }
        return super.onKeyDown(keyCode, event);
    }


    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    @Override
    public void onAttachedToWindow() {
        //关键：在onAttachedToWindow中设置FLAG_HOMEKEY_DISPATCHED
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        super.onAttachedToWindow();
    }

}

