package com.wisdomin.studentcard.util;

import android.content.Context;

import com.wisdomin.studentcard.bean.RequestContent;
import com.wisdomin.studentcard.util.encryption.SecurityAES;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;

/**
 * 组装数据的工具类
 */
public class PackDataUtil {

    private static AtomicInteger counter = new AtomicInteger(1);

    /**
     * 封装请求的数据
     *
     * @param context
     * @param data
     * @return
     */
    public static String packRequestStr(Context context, String waterNumber, String cmd, String messageType, String data) {
        String aes_key = PropertiesUtil.getInstance().getAes_key(context);
        String aes_vector = PropertiesUtil.getInstance().getAes_vector(context);

        //组装报文requestContent
        RequestContent requestContent = new RequestContent(DeviceUtil.getPhoneIMEI(context), DeviceUtil.getSimSerialNumber(context), waterNumber, cmd,
                messageType, systemTime(), data.length() + "", data);
        String contentRaw = requestContent.toString();
        LogUtil.d("===contentRaw===" + contentRaw);

        String aes = SecurityAES.getInstance().encryptT("[" + contentRaw + "]", aes_key, aes_vector);
        aes = aes.replace("\r","").replace("\n","");
        String content = aes + PropertiesUtil.getInstance().getFactory(context)+"\r\n";
        LogUtil.d("===reqString===" + content);
        LogUtil.writeInFile(context, "===content===" + systemTime() + "===" + content + "\r\n");
        return content;
    }

    public static RequestContent parseResponse(Context context, String returnStr) {
        RequestContent requestContent;
        try{
            String aes_key = PropertiesUtil.getInstance().getAes_key(context);
            String aes_vector = PropertiesUtil.getInstance().getAes_vector(context);
//            String base64 = Base64Custom.decodeToString(returnStr);
//            LogUtil.e("receive base64: " + base64);
            String body = SecurityAES.getInstance().decryptT(returnStr, aes_key, aes_vector);
            LogUtil.e("receive body: " + body);
            String[] stringBody = body.split(",");
            if(stringBody[3].equals(AppConst.LOCATION_INFO_GET)){
                String six = stringBody[6].replace("]", "");
                requestContent = new RequestContent(stringBody[0], stringBody[1], stringBody[2], stringBody[3], stringBody[4], stringBody[5], six,"0");
            }else {
                requestContent = new RequestContent(stringBody[0], stringBody[1], stringBody[2], stringBody[3], stringBody[4], stringBody[5], stringBody[6], stringBody[7]);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return requestContent;
    }

    public static String convertByteBufToString(ByteBuf buf) {
        String str;
        if (buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

    /**
     * 创建流水号
     *
     * @return
     */
    public static String createWaterNumber() {
        String waterNumber = "";
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sf.format(date);
        waterNumber = dateStr + getCount();
        return waterNumber;
    }

    /**
     * 设备时间
     *
     * @return
     */
    public static String systemTime() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sf.format(date);
        return dateStr;
    }

    public static synchronized String getCount() {
        // 线程安全,以原子方式将当前值加1，注意：这里返回的是自增前的值。
        if (counter.intValue() > 9999) {
            counter = new AtomicInteger(1);
        }
        int length = 4;
        String ret = counter.getAndIncrement() + "";
        for (int i = 0; ret.length() < length; i++) {
            ret = "0" + ret;
        }
        LogUtil.d("AtomicInteger:" + ret);
        return ret;
    }
}
