package com.ctop.studentcard.util.crossBorder;

/**
 * Created by noel on 2018/6/25.
 */
public class AmapToGPS
{
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    public static void main(String[] args) {

        AmapToGPS amapToGPS = new AmapToGPS();
      // String hm = wg.delta(22.5305930000,113.9469680000);
       // 106.622678,26.6481255
       // String hm = wg.delta(26.6481255,106.622678);
        double[] hm = amapToGPS.gps84_To_Gcj02(26.838776,104.880981);
        System.out.println(hm[0]+","+hm[1]);
    }
//    public String amapToGPSPoint(String amapPoint){
//        String [] point= new String[2];
//        point=amapPoint.split(",");
//        String GPSPoint=this.delta(Double.valueOf(point[0]),Double.valueOf(point[1]));
//        return GPSPoint;
//    }
    //圆周率 GCJ_02_To_WGS_84
    double PI = 3.14159265358979324;
    /**
     * 方法描述:方法可以将高德地图SDK获取到的GPS经纬度转换为真实的经纬度，可以用于解决安卓系统使用高德SDK获取经纬度的转换问题。
     * @return 转换为真实GPS坐标后的经纬度
     * @throws <异常类型> {@inheritDoc} 异常描述
     */
//    public String delta(double lat,double lon) {
//        double a = 6378245.0;//克拉索夫斯基椭球参数长半轴a
//        double ee = 0.00669342162296594323;//克拉索夫斯基椭球参数第一偏心率平方
//        double dLat = this.transformLat(lon - 105.0, lat - 35.0);
//        double dLon = this.transformLon(lon - 105.0, lat - 35.0);
//        double radLat = lat / 180.0 * this.PI;
//        double magic = Math.sin(radLat);
//        magic = 1 - ee * magic * magic;
//        double sqrtMagic = Math.sqrt(magic);
//        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * this.PI);
//        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * this.PI);
//        StringBuffer stringBuffer=new StringBuffer();
//        stringBuffer.append(lat - dLat);
//        stringBuffer.append(Constant.DATA_GRAM_SPLIT_MARK);
//        stringBuffer.append(lon - dLon);
//        return stringBuffer.toString();
//    }
    //转换经度
    public double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * this.PI) + 20.0 * Math.sin(2.0 * x * this.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * this.PI) + 40.0 * Math.sin(x / 3.0 * this.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * this.PI) + 300.0 * Math.sin(x / 30.0 * this.PI)) * 2.0 / 3.0;
        return ret;
    }
    //转换纬度
    public double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * this.PI) + 20.0 * Math.sin(2.0 * x * this.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * this.PI) + 40.0 * Math.sin(y / 3.0 * this.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * this.PI) + 320 * Math.sin(y * this.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public double[] gps84_To_Gcj02(double lat, double lon)
    {
        if (outOfChina(lat, lon))
        {
            return new double[]{lat, lon};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat/180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
    double mgLon = lon + dLon;
        return new double[]{mgLat, mgLon};
}

        public static boolean outOfChina(double lat, double lon) {
            if (lon < 72.004 || lon > 137.8347)
            {
                return true;
            }
            if (lat < 0.8293 || lat > 55.8271)
            {
                return true;
            }
            return false;
        }
}
