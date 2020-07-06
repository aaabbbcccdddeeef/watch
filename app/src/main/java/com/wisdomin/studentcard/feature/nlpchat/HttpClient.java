package com.wisdomin.studentcard.feature.nlpchat;

import android.content.Context;

import com.wisdomin.studentcard.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * - @author:y4ngyy
 */

public class HttpClient {

    public static String TENCENT_URL = "https://api.ai.qq.com/fcgi-bin/nlp/nlp_textchat";

    private OkHttpClient client;
    private static HttpClient mClient;
    private Context context;

    private HttpClient(Context c) {
        context = c;
        client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public static HttpClient getInstance(Context c) {
        if (mClient == null) {
            mClient = new HttpClient(c);
        }
        return mClient;
    }


    // GET方法
    public void get(String url, HashMap<String, String> param, final MyCallback callback) {

        // 拼接请求参数
        if (!param.isEmpty()) {
            StringBuffer buffer = new StringBuffer(url);
            buffer.append('?');
            for (Map.Entry<String, String> entry : param.entrySet()) {
                buffer.append(entry.getKey());
                buffer.append('=');
                buffer.append(entry.getValue());
                buffer.append('&');
            }
            buffer.deleteCharAt(buffer.length() - 1);
            url = buffer.toString();
        }
        Request.Builder builder = new Request.Builder().url(url);
        builder.method("GET", null);
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    public void get(String url, MyCallback callback) {
        get(url, new HashMap<String, String>(), callback);
    }

    // POST 方法
    public void post(String url, String jsonStr, final MyCallback callback) {
        LogUtil.e("http:url==="+url);
        LogUtil.showLog("http:request:",jsonStr);
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("data", jsonStr);
        FormBody.Builder formBody = new FormBody.Builder();
        if (!hashMap.isEmpty()) {
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody form = formBody.build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.post(form)
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    //上传文件
    public void uploadFile(String url,File mFile, final MyCallback callback) {
        // 获取要上传的文件
//        mFile = new File("/storage/sdcard0/Pictures/text.jpg");

       /* if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File directory = Environment.getExternalStorageDirectory();
            mFile = new File(directory, "text.jpg");
        }*/
        OkHttpClient client = new OkHttpClient.Builder().build();
        // 设置文件以及文件上传类型封装
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), mFile);
        // 文件上传的请求体封装
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", "abc")
                .addFormDataPart("file", mFile.getName(), requestBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.failed(e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.success(response);
            }
        });
    }

    public interface MyCallback {
        void success(Response res) throws IOException;

        void failed(IOException e);
    }
}