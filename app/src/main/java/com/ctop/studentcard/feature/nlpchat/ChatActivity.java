package com.ctop.studentcard.feature.nlpchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.util.EncryptUtils;
import com.ctop.studentcard.util.JsonUtil;
import com.ctop.studentcard.util.LogUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.Response;

public class ChatActivity extends BaseActivity {

    int APP_ID = 2130836526;
    String APP_KEY = "7V51weZKPiOh4TII";


    EditText mEt;
    String nonce_str = getRandomString(10);
    String randomSession = getRandomString(10);
    TextView mTv;
    private String retStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        mEt = findViewById(R.id.mEt);
        mTv = findViewById(R.id.mTv);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Answer answer = JsonUtil.parseObject(retStr, Answer.class);

                LogUtil.e("an==="+answer.toString());

            }
        }
    };

    /**
     * 使用 Map按key进行排序
     *
     * @param map x
     * @return x
     */
    public Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    /**
     * 随机字符串
     *
     * @param length x
     * @return x
     */
    public String getRandomString(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public void sendMsg(View view) {
        final String question  = "你是谁";//= mEt.getText().toString().trim();
        mTv.append("ques : " + question + "\n");
//        mEt.setText("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long currTime = (System.currentTimeMillis() / 1000);

                    //  参数组合
                    Map<String, Object> params = new HashMap<>();

                    params.put("app_id", APP_ID);
                    params.put("time_stamp", currTime);
                    params.put("nonce_str", nonce_str);
                    params.put("session", randomSession);
                    params.put("question", question);
                    //  鉴权开始
                    //  1. 将<key, value>请求参数对按key进行字典升序排序，得到有序的参数对列表N
                    Map<String, Object> resultMap = sortMapByKey(params);
                    //  2.列表N中的参数对按URL键值对的格式拼接成字符串，得到字符串T（如：key1=value1&key2=value2），
                    //  URL键值拼接过程value部分需要URL编码，URL编码算法用大写字母，例如%E8，而不是小写%e8
                    //  3. 将应用密钥以app_key为键名，组成URL键值拼接到字符串T末尾，得到字符串S（如：key1=value1&key2=value2&app_key=密钥)
                    Set<String> keySet = resultMap.keySet();
                    StringBuilder sb = new StringBuilder();
                    for (String key : keySet) {
                        Object value = resultMap.get(key);
                        sb.append("&").append(key).append("=").append(URLEncoder.encode(value + "", "UTF-8"));
                    }
                    sb.deleteCharAt(0);

                    sb.append("&app_key=").append(APP_KEY);
                    LogUtil.e("RedWolf md5运算之前==="+sb.toString());
                    //  4. MD5运算
                    String md5Sign = EncryptUtils.encryptMD5ToString(sb.toString());
                    LogUtil.e("RedWolf md5Sign==="+md5Sign);
                    //  利用Http发送对话请求
                    //  拼接参数之前
                    LogUtil.e("RedWolf==="+ APP_ID);
                    LogUtil.e("RedWolf==="+ nonce_str);
                    LogUtil.e("RedWolf==="+  question);
                    LogUtil.e("RedWolf==="+ randomSession );
                    LogUtil.e("RedWolf==="+  currTime);
                    LogUtil.e("RedWolf==="+  APP_KEY);
                    LogUtil.e("RedWolf==="+  md5Sign);
                    RequestBean requestBean = new RequestBean();
                    requestBean.setApp_id(APP_ID);
                    requestBean.setNonce_str(nonce_str);
                    requestBean.setQuestion(question);
                    requestBean.setSession(randomSession);
                    requestBean.setSign(md5Sign);
                    requestBean.setTime_stamp(currTime);
                    String json = JsonUtil.toJSONString(requestBean);

                    HttpClient.getInstance(ChatActivity.this).post(HttpClient.TENCENT_URL, json, new HttpClient.MyCallback() {
                        @Override
                        public void success(Response response) throws IOException {
                            final String rtn = response.body().string();
                            //获取返回码
                            final String code = String.valueOf(response.code());
                            if (code.equals("200")) {
                                retStr = rtn;
                                handler.sendEmptyMessage(0);
                            }
                        }

                        @Override
                        public void failed(IOException e) {
                            e.printStackTrace();
                        }
                    });
//                    OkGo.<String>get(URL)
//                            .tag(this)
//                            .params("app_id", APP_ID)
//                            .params("nonce_str", nonce_str)
//                            .params("question", question)
//                            .params("session", randomSession)
//                            .params("time_stamp", currTime)
//                            .params("sign", md5Sign)
//                            .execute(new StringCallback() {
//                                @Override
//                                public void onSuccess(final Response<String> response) {
//                                    LogUtil.iTag("RedWolfR", response.body());
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Answer fuck = new Gson().fromJson(response.body(), Answer.class);
//                                            mTv.append("ans : " + fuck.getData().getAnswer());
//                                            mTv.append("session : " + fuck.getData().getSession() + "\n");
//                                        }
//                                    });
//
//                                }
//
//                                @Override
//                                public void onError(Response<String> response) {
//                                    super.onError(response);
//                                    LogUtil.e("RedWolfR"+response.body());
//                                }
//                            });
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();

    }

    public void clearAns(View view) {
        mTv.setText("");
    }


    class RequestBean{
        private int app_id;
        private long time_stamp;
        private String nonce_str;
        private String sign;
        private String session;
        private String question;

        public int getApp_id() {
            return app_id;
        }

        public void setApp_id(int app_id) {
            this.app_id = app_id;
        }

        public long getTime_stamp() {
            return time_stamp;
        }

        public void setTime_stamp(long time_stamp) {
            this.time_stamp = time_stamp;
        }

        public String getNonce_str() {
            return nonce_str;
        }

        public void setNonce_str(String nonce_str) {
            this.nonce_str = nonce_str;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }
    }

}
