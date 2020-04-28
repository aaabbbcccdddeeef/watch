package com.ctop.studentcard.bean;


public class RequestContent {

    private String imei;//imei
    private String iccid;//SIM 卡卡号
    private String waterNumber;//请求流水号
    private String cmd;//接口唯一标示
    private String messageType;//报文类型
    private String deviceTime;//设备时间
    private String dataLength;//data长度
    private String data;//请求的数据

    public RequestContent(String imei, String iccid, String waterNumber, String cmd, String messageType, String deviceTime, String dataLength, String data) {
        this.imei = imei;
        this.iccid = iccid;
        this.waterNumber = waterNumber;
        this.cmd = cmd;
        this.messageType = messageType;
        this.deviceTime = deviceTime;
        this.dataLength = dataLength;
        this.data = data;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getWaterNumber() {
        return waterNumber;
    }

    public void setWaterNumber(String waterNumber) {
        this.waterNumber = waterNumber;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return  imei+","+iccid+","+waterNumber+","+cmd+","+messageType+","+deviceTime+","+dataLength+","+data;
    }
}
