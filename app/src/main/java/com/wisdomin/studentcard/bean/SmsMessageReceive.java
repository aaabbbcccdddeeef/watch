package com.wisdomin.studentcard.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SmsMessageReceive implements Parcelable {

    private String smsType;
    private String uuid;
    private String speackOrShow;
    private String speackTime;
    private String flash;
    private String shake;//
    private String ring;//
    private String message;

    public String getSmsType() {
        String smsTypeStr = "";//0 紧急消息 1 通知 2 作业  3 考勤  4 消费  5 普通消息
        if(smsType.equals("0")){
            smsTypeStr="紧急通知";
        }else if(smsType.equals("1")){
            smsTypeStr="通知";
        }else if(smsType.equals("2")){
            smsTypeStr="作业";
        }else if(smsType.equals("3")){
            smsTypeStr="考勤";
        }else if(smsType.equals("4")){
            smsTypeStr="消费";
        }else if(smsType.equals("5")){
            smsTypeStr="普通消息";
        }
        return smsTypeStr;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSpeackOrShow() {
        return speackOrShow;
    }

    public void setSpeackOrShow(String speackOrShow) {
        this.speackOrShow = speackOrShow;
    }

    public String getSpeackTime() {
        return speackTime;
    }

    public void setSpeackTime(String speackTime) {
        this.speackTime = speackTime;
    }

    public String getFlash() {
        return flash;
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }

    public String getShake() {
        return shake;
    }

    public void setShake(String shake) {
        this.shake = shake;
    }

    public String getRing() {
        return ring;
    }

    public void setRing(String ring) {
        this.ring = ring;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static SmsMessageReceive parseJson(String data) {
        String[] strings = data.split("@");
        SmsMessageReceive receive = new SmsMessageReceive();
        receive.setSmsType(strings[0]);
        receive.setUuid(strings[1]);
        receive.setSpeackOrShow(strings[2]);
        receive.setSpeackTime(strings[3]);
        receive.setFlash(strings[4]);
        receive.setShake(strings[5]);
        receive.setRing(strings[6]);
        receive.setMessage(strings[7]);
        return receive;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
