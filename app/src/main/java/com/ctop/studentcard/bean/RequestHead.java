package com.ctop.studentcard.bean;

public class RequestHead {

    private String companyId;//平台给厂商的唯一标识
    private String productType;//设备类型
    private String versionCode;//开发版本号
    private String timestamp;//发起请求的时间
    private String netInTpye;//网络接入方式
    private String waterNumber;//请求流水号

    public RequestHead(String companyId, String productType, String versionCode, String timestamp, String netInTpye, String waterNumber) {
        this.companyId = companyId;
        this.productType = productType;
        this.versionCode = versionCode;
        this.timestamp = timestamp;
        this.netInTpye = netInTpye;
        this.waterNumber = waterNumber;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNetInTpye() {
        return netInTpye;
    }

    public void setNetInTpye(String netInTpye) {
        this.netInTpye = netInTpye;
    }

    public String getWaterNumber() {
        return waterNumber;
    }

    public void setWaterNumber(String waterNumber) {
        this.waterNumber = waterNumber;
    }

    @Override
    public String toString() {
        return companyId + "," + productType + "," + versionCode + "," + timestamp +
                "," + netInTpye + "," + waterNumber;
    }
}
