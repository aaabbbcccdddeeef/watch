package com.ctop.studentcard.bean;

public class RequestBody {

    private String productKey;//设备唯一编号
    private String cmd;//接口唯一标识
    private String messageType;//报文类型
    private String deviceTime;//设备时间
    private String dataLength;//data长度
    private String data;//请求的数据

    public RequestBody(String productKey, String cmd, String messageType, String deviceTime, String dataLength, String data) {
        this.productKey = productKey;
        this.cmd = cmd;
        this.messageType = messageType;
        this.deviceTime = deviceTime;
        this.dataLength = dataLength;
        this.data = data;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
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
        return productKey + "," + cmd + "," + messageType + "," + deviceTime + "," + dataLength + "," + data;
    }
}
