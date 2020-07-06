package com.wisdomin.studentcard.bean;

/**
 * Created by noel on 2018/5/29.
 */

import android.content.Context;

import com.wisdomin.studentcard.util.LogUtil;
import com.wisdomin.studentcard.util.NetworkUtil;
import com.wisdomin.studentcard.util.PreferencesUtils;
import com.wisdomin.studentcard.util.TimeUtils;
import com.wisdomin.studentcard.util.crossBorder.CrossBorderUtil;
import com.wisdomin.studentcard.util.crossBorder.PointEntity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description
 * @author liuwenbin
 *
 */

public class RegionalReturn implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 父母卡类型：1表示父亲卡 2表示母亲卡（暂默认父
     * 亲卡）
     * 位置信息：遵循NEMA报文格式GPS上报位置信息，参
     * 见附录A；
     * 进入某区域或离开某区域：1或者0(1:在区域内 0：
     * 在区域外)
     */

    private String cardType;
    private String gpsString;
    private String inOrOut;
    private String area;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getGpsString() {
        return gpsString;
    }

    public void setGpsString(String gpsString) {
        this.gpsString = gpsString;
    }

    public String getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }




    public static RegionalReturn isCrossBorder(Context mContext , double latitude, double longitude, String lastLatitude, String lastLongitude) {
        RegionalReturn regionalReturn = null;
        Set regionalAlarmEntityList = PreferencesUtils.getInstance(mContext).getStringSet("regionalAlarm", new HashSet<String>());
        List<RegionalAlarmEntity> list_1 = new ArrayList<>(regionalAlarmEntityList);
        for (RegionalAlarmEntity regionalAlarmEntity:list_1)
        {
            Calendar calendar;
            calendar = Calendar.getInstance();
            String week;
            week = calendar.get(Calendar.DAY_OF_WEEK)-1+ "";
            if (!regionalAlarmEntity.getCycle().contains(week)){
                continue;
            }else
            {
                String[] durationTime = regionalAlarmEntity.getDuration().split("\\+");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHMM");
                int nowTime = Integer.parseInt(simpleDateFormat.format(new Date()));
                boolean inDruation = false;
                for (String s : durationTime)
                {
                    int beginTime = Integer.parseInt(s.split("-")[0]);
                    int endTime = Integer.parseInt(s.split("-")[1]);
                    if (nowTime > beginTime && nowTime < endTime)
                    {
                        inDruation = true;
                    }
                }
                if (!inDruation)
                {
                    continue;
                }
            }
            if ("".equals(longitude)||0==longitude){
                LogUtil.e("当前点为空跳出围栏判断");
                break;
            }
            if ("Round".equals(regionalAlarmEntity.getShape())){
                String[] coordinate = regionalAlarmEntity.getElement1().split(",");
                //当前点
                LogUtil.e("圆心"+Double.valueOf(coordinate[0])+","+Double.valueOf(coordinate[1])
                        +"-"+regionalAlarmEntity.getElement2());
                LogUtil.e("当前点"+Double.valueOf(longitude)+","+
                        Double.valueOf(latitude));
                LogUtil.e("历史点"+Double.valueOf(lastLongitude)+","+
                        Double.valueOf(lastLatitude));
                boolean isInCircleNow= CrossBorderUtil.isInCircle(Double.valueOf(longitude),
                        Double.valueOf(latitude),Double.valueOf(coordinate[1]),Double.valueOf(coordinate[0])
                        ,regionalAlarmEntity.getElement2());
                boolean isInCircleLast= CrossBorderUtil.isInCircle(Double.valueOf(lastLongitude),
                        Double.valueOf(lastLatitude),Double.valueOf(coordinate[1]),Double.valueOf(coordinate[0])
                        ,regionalAlarmEntity.getElement2());
                LogUtil.e(isInCircleNow+" "+isInCircleLast);
                if (isInCircleNow&&!isInCircleLast){
                    //进入当前围栏报警
                    //1:在区域内  0：在区域外'
                    LogUtil.e("进入圆形围栏报警，围栏号"+regionalAlarmEntity.getArea());
                    regionalReturn = new RegionalReturn();
                    regionalReturn.setCardType(regionalAlarmEntity.getReqType()+"");
                    regionalReturn.setGpsString(NetworkUtil.createGps(latitude,longitude, TimeUtils.getNowTimeString(TimeUtils.format6)));
                    regionalReturn.setInOrOut(0+"");
                    regionalReturn.setArea(regionalAlarmEntity.getArea());
                }else if(!isInCircleNow&&isInCircleLast){
                    LogUtil.e("离开圆形围栏报警，围栏号"+regionalAlarmEntity.getArea());
                    regionalReturn = new RegionalReturn();
                    regionalReturn.setCardType(regionalAlarmEntity.getReqType()+"");
                    regionalReturn.setGpsString(NetworkUtil.createGps(latitude,longitude,TimeUtils.getNowTimeString(TimeUtils.format6)));
                    regionalReturn.setInOrOut(1+"");
                    regionalReturn.setArea(regionalAlarmEntity.getArea());
                }
            }
            if ("Polygon".equals(regionalAlarmEntity.getShape())){
                int elementNum=regionalAlarmEntity.getElementNum().intValue();
                List<String> point=new ArrayList<String>();
                point.add(regionalAlarmEntity.getElement1());
                point.add(regionalAlarmEntity.getElement2());
                point.add(regionalAlarmEntity.getElement3());
                point.add(regionalAlarmEntity.getElement4());
                point.add(regionalAlarmEntity.getElement5());
                point.add(regionalAlarmEntity.getElement6());
                point.add(regionalAlarmEntity.getElement7());
                point.add(regionalAlarmEntity.getElement8());
                PointEntity[] ps = new PointEntity[elementNum];
                for (int i = 0; i <elementNum; i++)
                {
                    String[] element= point.get(i).split(",");
                    LogUtil.e("原始坐标"+element[1]+","+element[0]);
                    PointEntity pointEntity=new PointEntity(Double.valueOf(element[1]),Double.valueOf(element[0]));
                    ps[i]=pointEntity;
                }
                LogUtil.e("当前点"+Double.valueOf(longitude)+","+
                        Double.valueOf(latitude));
                LogUtil.e("历史点"+Double.valueOf(lastLongitude)+","+
                        Double.valueOf(lastLatitude));
                boolean isInPolygonNow= CrossBorderUtil.isPtInPoly(Double.valueOf(longitude),Double.valueOf(latitude),ps);
                boolean isInPolygonLast= CrossBorderUtil.isPtInPoly(Double.valueOf(lastLongitude), Double.valueOf(lastLatitude),ps);
                LogUtil.e(isInPolygonNow+" "+isInPolygonLast);
                if (isInPolygonNow&&!isInPolygonLast){
                    //进入当前围栏报警
                    //1:在区域内  0：在区域外'
                    LogUtil.e("进入多边形围栏报警，围栏号"+regionalAlarmEntity.getArea());
                    regionalReturn = new RegionalReturn();
                    regionalReturn.setCardType(regionalAlarmEntity.getReqType()+"");
                    regionalReturn.setGpsString(NetworkUtil.createGps(latitude,longitude,TimeUtils.getNowTimeString(TimeUtils.format6)));
                    regionalReturn.setInOrOut(1+"");
                    regionalReturn.setArea(regionalAlarmEntity.getArea());
                }else if(!isInPolygonNow&&isInPolygonLast){
                    LogUtil.e("离开多边形围栏报警，围栏号"+regionalAlarmEntity.getArea());
                    regionalReturn = new RegionalReturn();
                    regionalReturn.setCardType(regionalAlarmEntity.getReqType()+"");
                    regionalReturn.setGpsString(NetworkUtil.createGps(latitude,longitude,TimeUtils.getNowTimeString(TimeUtils.format6)));
                    regionalReturn.setInOrOut(1+"");
                    regionalReturn.setArea(regionalAlarmEntity.getArea());
                }
            }
        }
        return regionalReturn;
    }
}
