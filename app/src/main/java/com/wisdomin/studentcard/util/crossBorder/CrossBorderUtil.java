package com.wisdomin.studentcard.util.crossBorder;


//import java.awt.geom.Point2D;

/**
 * Created by noel on 2018/12/5.
 */
public class CrossBorderUtil
{
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     */
    public static double getDistance(double lat1, double lng1, double lat2,
            double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.abs(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2))));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 1000);
        return s;
    }

    /**
     * 判断一个点是否在圆形区域内
     */
    public static boolean isInCircle(double lng1, double lat1, double lng2, double lat2, String radius) {
        double distance = getDistance(lat1, lng1, lat2, lng2);
        double r = Double.parseDouble(radius);
        if (distance > r) {
            return false;
        } else {
            return true;
        }
    }
//    /**
//     * 判断是否在多边形区域内
//     * @return
//     */
//    public static boolean isInPolygon(double pointLon, double pointLat, double[] lon,
//            double[] lat) {
//        Point2D.Double point = new Point2D.Double(pointLon, pointLat);
//        List<Point2D.Double> pointList = new ArrayList<Point2D.Double>();
//        double polygonPoint_x = 0.0, polygonPoint_y = 0.0;
//        for (int i = 0; i < lon.length; i++) {
//            polygonPoint_x = lon[i];
//            polygonPoint_y = lat[i];
//            Point2D.Double polygonPoint = new Point2D.Double(polygonPoint_x, polygonPoint_y);
//            pointList.add(polygonPoint);
//        }
//        return check(point, pointList);
//    }
//
//    /**
//     * 一个点是否在多边形内
//     */
//    private static boolean check(Point2D.Double point, List<Point2D.Double> polygon) {
//        java.awt.geom.GeneralPath peneralPath = new java.awt.geom.GeneralPath();
//
//        Point2D.Double first = polygon.get(0);
//        peneralPath.moveTo(first.x, first.y);
//        polygon.remove(0);
//        for (Point2D.Double d : polygon) {
//            peneralPath.lineTo(d.x, d.y);
//        }
//        peneralPath.lineTo(first.x, first.y);
//        peneralPath.closePath();
//        return peneralPath.contains(point);
//    }

    /**
     * 判断是否在多边形区域内
     * @return
     */
    public static boolean isPtInPoly (double ALon , double ALat , PointEntity[] ps) {
        int iSum, iCount, iIndex;
        double dLon1 = 0, dLon2 = 0, dLat1 = 0, dLat2 = 0, dLon;
        if (ps.length < 3) {
            return false;
        }
        iSum = 0;
        iCount = ps.length;
        for (iIndex = 0; iIndex<iCount;iIndex++) {
            if (iIndex == iCount - 1) {
                dLon1 = ps[iIndex].getX();
                dLat1 = ps[iIndex].getY();
                dLon2 = ps[0].getX();
                dLat2 = ps[0].getY();
            } else {
                dLon1 = ps[iIndex].getX();
                dLat1 = ps[iIndex].getY();
                dLon2 = ps[iIndex + 1].getX();
                dLat2 = ps[iIndex + 1].getY();
            }
            // 以下语句判断A点是否在边的两端点的水平平行线之间，在则可能有交点，开始判断交点是否在左射线上
            if (((ALat >= dLat1) && (ALat < dLat2)) || ((ALat >= dLat2) && (ALat < dLat1))) {
                if (Math.abs(dLat1 - dLat2) > 0) {
                    //得到 A点向左射线与边的交点的x坐标：
                    dLon = dLon1 - ((dLon1 - dLon2) * (dLat1 - ALat) ) / (dLat1 - dLat2);
                    // 如果交点在A点左侧（说明是做射线与 边的交点），则射线与边的全部交点数加一：
                    if (dLon < ALon) {
                        iSum++;
                    }
                }
            }
        }
        if ((iSum % 2) != 0) {
            return true;
        }
        return false;
    }


}
