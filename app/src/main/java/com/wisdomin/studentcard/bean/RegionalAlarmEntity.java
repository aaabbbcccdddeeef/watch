package com.wisdomin.studentcard.bean;

/**
 * Created by noel on 2018/5/29.
 */

import java.io.Serializable;
import java.util.Date;

/**
 * @description 《告警区域设置》 实体
 * @author liuwenbin
 *
 */

public class RegionalAlarmEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; //ID
    private String iccid; //iccid
    private String area; //区域编号
    private Long operateType; //操作代码
    private Long reqType; //请求状态：1表示父亲卡
    private String shape; //形状:圆形=Round
    private Long elementNum; //元素个数
    private String element1; //元素值1
    private String element2; //元素值2
    private String element3; //元素值3
    private String element4; //元素值4
    private String element5; //元素值5
    private String element6; //元素值6
    private String element7; //元素值7
    private String element8; //元素值8
    private String duration; //时间段：当前区域的时间范围，最多设置两个时间段（开始时间-结束时间+开始时间-结束时间+），如：0800-1130+1330-1730+
    private String cycle; //周期：周一至周六用1-6表示，周日用0表示，以+号连接，如1+2+3+4+表示周一至周四
    private Date createTime; //区域创建时间
    private Long status; //1，区域生效

    public String getManageSerialNum()
    {
        return manageSerialNum;
    }

    public void setManageSerialNum(String manageSerialNum)
    {
        this.manageSerialNum = manageSerialNum;
    }

    private String manageSerialNum; //1，区域生效


    /**
     *默认空构造函数
     */
    public RegionalAlarmEntity() {
        super();
    }

    /**
     * @return id ID
     */
    public Long getId(){
        return this.id;
    }
    /**
     * @param id ID
     */
    public void setId(Long id){
        this.id = id;
    }
    /**
     * @return iccid iccid
     */
    public String getIccid(){
        return this.iccid;
    }
    /**
     * @param iccid iccid
     */
    public void setIccid(String iccid){
        this.iccid = iccid;
    }
    /**
     * @return area 区域编号
     */
    public String getArea(){
        return this.area;
    }
    /**
     * @param area 区域编号
     */
    public void setArea(String area){
        this.area = area;
    }
    /**
     * @return operateType 操作代码
     */
    public Long getOperateType(){
        return this.operateType;
    }
    /**
     * @param operateType 操作代码
     */
    public void setOperateType(Long operateType){
        this.operateType = operateType;
    }
    /**
     * @return reqType 请求状态：1表示父亲卡
     */
    public Long getReqType(){
        return this.reqType;
    }
    /**
     * @param reqType 请求状态：1表示父亲卡
     */
    public void setReqType(Long reqType){
        this.reqType = reqType;
    }
    /**
     * @return shape 形状:圆形=Round
     */
    public String getShape(){
        return this.shape;
    }
    /**
     * @param shape 形状:圆形=Round
     */
    public void setShape(String shape){
        this.shape = shape;
    }
    /**
     * @return elementNum 元素个数
     */
    public Long getElementNum(){
        return this.elementNum;
    }
    /**
     * @param elementNum 元素个数
     */
    public void setElementNum(Long elementNum){
        this.elementNum = elementNum;
    }
    /**
     * @return element1 元素值1
     */
    public String getElement1(){
        return this.element1;
    }
    /**
     * @param element1 元素值1
     */
    public void setElement1(String element1){
        this.element1 = element1;
    }
    /**
     * @return element2 元素值2
     */
    public String getElement2(){
        return this.element2;
    }
    /**
     * @param element2 元素值2
     */
    public void setElement2(String element2){
        this.element2 = element2;
    }
    /**
     * @return element3 元素值3
     */
    public String getElement3(){
        return this.element3;
    }
    /**
     * @param element3 元素值3
     */
    public void setElement3(String element3){
        this.element3 = element3;
    }
    /**
     * @return element4 元素值4
     */
    public String getElement4(){
        return this.element4;
    }
    /**
     * @param element4 元素值4
     */
    public void setElement4(String element4){
        this.element4 = element4;
    }
    /**
     * @return element5 元素值5
     */
    public String getElement5(){
        return this.element5;
    }
    /**
     * @param element5 元素值5
     */
    public void setElement5(String element5){
        this.element5 = element5;
    }
    /**
     * @return element6 元素值6
     */
    public String getElement6(){
        return this.element6;
    }
    /**
     * @param element6 元素值6
     */
    public void setElement6(String element6){
        this.element6 = element6;
    }
    /**
     * @return element7 元素值7
     */
    public String getElement7(){
        return this.element7;
    }
    /**
     * @param element7 元素值7
     */
    public void setElement7(String element7){
        this.element7 = element7;
    }
    /**
     * @return element8 元素值8
     */
    public String getElement8(){
        return this.element8;
    }
    /**
     * @param element8 元素值8
     */
    public void setElement8(String element8){
        this.element8 = element8;
    }
    /**
     * @return duration 时间段：当前区域的时间范围，最多设置两个时间段（开始时间-结束时间+开始时间-结束时间+），如：0800-1130+1330-1730+
     */
    public String getDuration(){
        return this.duration;
    }
    /**
     * @param duration 时间段：当前区域的时间范围，最多设置两个时间段（开始时间-结束时间+开始时间-结束时间+），如：0800-1130+1330-1730+
     */
    public void setDuration(String duration){
        this.duration = duration;
    }
    /**
     * @return cycle 周期：周一至周六用1-6表示，周日用0表示，以+号连接，如1+2+3+4+表示周一至周四
     */
    public String getCycle(){
        return this.cycle;
    }
    /**
     * @param cycle 周期：周一至周六用1-6表示，周日用0表示，以+号连接，如1+2+3+4+表示周一至周四
     */
    public void setCycle(String cycle){
        this.cycle = cycle;
    }
    /**
     * @return createTime 区域创建时间
     */
    public Date getCreateTime(){
        return this.createTime;
    }
    /**
     * @param createTime 区域创建时间
     */
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    /**
     * @return status 1，区域生效
     */
    public Long getStatus(){
        return this.status;
    }
    /**
     * @param status 1，区域生效
     */
    public void setStatus(Long status){
        this.status = status;
    }



    public static RegionalAlarmEntity parseJson(String data) {
        String[] strings = data.split("@");
        RegionalAlarmEntity regionalAlarmEntity = new RegionalAlarmEntity();
        regionalAlarmEntity.setOperateType(Long.valueOf(strings[0]));
        regionalAlarmEntity.setReqType(Long.valueOf(strings[1]));
        String[] strings2 = strings[2].split("\\*");
        regionalAlarmEntity.setShape(strings2[0]);
        regionalAlarmEntity.setElementNum(Long.valueOf(strings2[1]));
        long num = Long.valueOf(strings2[1]);
        if(num>=8){
            regionalAlarmEntity.setElement8(strings2[9]);
        }
        if(num>=7){
            regionalAlarmEntity.setElement7(strings2[8]);
        }
        if(num>=6){
            regionalAlarmEntity.setElement6(strings2[7]);
        }
        if(num>=5){
            regionalAlarmEntity.setElement5(strings2[6]);
        }
        if(num>=4){
            regionalAlarmEntity.setElement4(strings2[5]);
        }
        if(num>=3){
            regionalAlarmEntity.setElement3(strings2[4]);
        }
        if(num>=2){
            regionalAlarmEntity.setElement2(strings2[3]);
        }
        if(num>=1){
            regionalAlarmEntity.setElement1(strings2[2]);
        }
        //操作代码@请求状态@形状*元素个数*元素值 1*元素值 2*...*元素值 n@区域编号@时间段@周期
        //1@1@Round*2*（24.513972491956064#117.72402112636269）*（1000）@3@1130-1230+1330-1430+@1+2+3+4+
        regionalAlarmEntity.setArea(strings[3]);
        regionalAlarmEntity.setDuration(strings[4]);
        regionalAlarmEntity.setCycle(strings[5]);


        return regionalAlarmEntity;
    }
}
