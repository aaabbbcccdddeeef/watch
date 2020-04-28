package com.ctop.studentcard.bean;

/**
 * 基站信息
 *
 * @author jiqinlin
 */
public class CellInfo {
    /**
     * 基站id，用来找到基站的位置
     */
    private int cellId;
    /**
     * 移动国家码，共3位，中国为460，即imsi前3位
     */
    private String mobileCountryCode = "460";
    /**
     * 移动网络码，共2位，在中国，移动的代码为00和02，联通的代码为01，电信的代码为03，即imsi第4~5位
     */
    private String mobileNetworkCode = "0";
    /**
     * 地区区域码
     */
    private int locationAreaCode;
    /**
     * 信号类型[选 gsm|cdma|wcdma]
     */
    private String radioType = "";
    //一下是cdma的属性
    private String mSystemId = "";
    private String mNetworkId = "";
    private String mBaseStationId = "";
    private String mBaseStationLongitude = "";
    private String mBaseStationLatitude = "";

    //信号强度
    private String mRssi = "";

    public CellInfo() {
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public String getMobileCountryCode() {
        return mobileCountryCode;
    }

    public void setMobileCountryCode(String mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }

    public String getMobileNetworkCode() {
        return mobileNetworkCode;
    }

    public void setMobileNetworkCode(String mobileNetworkCode) {
        this.mobileNetworkCode = mobileNetworkCode;
    }

    public int getLocationAreaCode() {
        return locationAreaCode;
    }

    public void setLocationAreaCode(int locationAreaCode) {
        this.locationAreaCode = locationAreaCode;
    }

    public String getRadioType() {
        return radioType;
    }

    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    public String getmSystemId() {
        return mSystemId;
    }

    public void setmSystemId(String mSystemId) {
        this.mSystemId = mSystemId;
    }

    public String getmNetworkId() {
        return mNetworkId;
    }

    public void setmNetworkId(String mNetworkId) {
        this.mNetworkId = mNetworkId;
    }

    public String getmBaseStationId() {
        return mBaseStationId;
    }

    public void setmBaseStationId(String mBaseStationId) {
        this.mBaseStationId = mBaseStationId;
    }

    public String getmBaseStationLongitude() {
        return mBaseStationLongitude;
    }

    public void setmBaseStationLongitude(String mBaseStationLongitude) {
        this.mBaseStationLongitude = mBaseStationLongitude;
    }

    public String getmBaseStationLatitude() {
        return mBaseStationLatitude;
    }

    public void setmBaseStationLatitude(String mBaseStationLatitude) {
        this.mBaseStationLatitude = mBaseStationLatitude;
    }

    public String getmRssi() {
        return mRssi;
    }

    public void setmRssi(String mRssi) {
        this.mRssi = mRssi;
    }

    @Override
    public String toString() {
        return mobileCountryCode+"!"+mobileNetworkCode+"!"+locationAreaCode+"!"+cellId+"!"+mRssi;
    }
}