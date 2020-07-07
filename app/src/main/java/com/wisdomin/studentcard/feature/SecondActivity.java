package com.wisdomin.studentcard.feature;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wisdomin.studentcard.R;


public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(0x80000000,0x80000000);
//        getWindow().addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.second);

        WebView mWebView = findViewById(R.id.webView);
        mWebView.loadUrl("https://www.baidu.com");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });

        //不使用缓存:
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

    }




}

