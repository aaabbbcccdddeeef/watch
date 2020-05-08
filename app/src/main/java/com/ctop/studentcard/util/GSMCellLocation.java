package com.ctop.studentcard.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.ctop.studentcard.bean.CellInfo;
import com.ctop.studentcard.broadcast.BroadcastConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 基站信息
 */
public class GSMCellLocation {

    public static int lastStrength = 0;
    //单个基站的信号强度
    public static void getSignalStrengths(final Context ctx){
       final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new  PhoneStateListener(){

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                String signalInfo = signalStrength.toString();
                String[] params = signalInfo.split(" ");
                if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                    //4G网络 最佳范围 >-90dBm 越大越好
                    int Itedbm = Integer.parseInt(params[9]);
                    lastStrength = Itedbm;
                }else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
                        tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
                    //3G网络最佳范围  >-90dBm  越大越好  ps:中国移动3G获取不到  返回的无效dbm值是正数（85dbm）
                    //在这个范围的已经确定是3G，但不同运营商的3G有不同的获取方法，故在此需做判断 判断运营商与网络类型的工具类在最下方
                    String yys = getNetType(ctx);//获取当前运营商
                    if (yys == "1") {
                    } else if (yys == "2") {
                        int cdmaDbm = signalStrength.getCdmaDbm();
                        lastStrength = cdmaDbm;
                    } else if (yys == "3") {
                        int evdoDbm = signalStrength.getEvdoDbm();
                        lastStrength = evdoDbm;
                    }
                } else {
                    //2G网络最佳范围>-90dBm 越大越好
                    int asu = signalStrength.getGsmSignalStrength();
                    int dbm = -113 + 2 * asu;
                    lastStrength = dbm;
                }

//                lastStrength = signalStrength.getGsmSignalStrength();
                sendStrength(ctx);
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    //发送广播 接通
    public static  void sendStrength(Context context) {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.LASTSTRENGTH_STATE);
        intent.putExtra("signalStrength", lastStrength);
        context.sendBroadcast(intent);// 发送
    }

    public static String getNetType(Context ctx) {
        String ret = "1";
        ArrayList<CellInfo> cellInfos = new ArrayList<CellInfo>();
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //网络制式
        int type = tm.getNetworkType();
        @SuppressLint("MissingPermission")
        String imsi = tm.getSubscriberId();
//        LogUtil.d("imsi: " + imsi);
        //为了区分移动、联通还是电信，推荐使用imsi来判断(万不得己的情况下用getNetworkType()判断，比如imsi为空时)
        if (imsi != null && !"".equals(imsi)) {
//            LogUtil.d("imsi");
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                // 中国移动
                ret = "1";
            } else if (imsi.startsWith("46001")) {
                // 中国联通
                ret = "2";
            } else if (imsi.startsWith("46003")) {
                // 中国电信
                ret = "3";
            }
        } else {
            LogUtil.e("type");
            // 在中国，联通的3G为UMTS或HSDPA，电信的3G为EVDO
            // 在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA
            // String OperatorName = tm.getNetworkOperatorName();
            //中国电信
            if (type == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || type == TelephonyManager.NETWORK_TYPE_CDMA
                    || type == TelephonyManager.NETWORK_TYPE_1xRTT) {
                ret = "3";
            }
            //移动(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_GPRS) {
                ret = "1";
            }
            //联通(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_GPRS
                    || type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_UMTS
                    || type == TelephonyManager.NETWORK_TYPE_HSDPA) {
                ret = "2";
            }
        }
        return ret;
    }


    public static String getServiceState(Context ctx) {
        String ret = "1";
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //网络制式
        int type = tm.getNetworkType();
        LogUtil.e("type");
        // 在中国，联通的3G为UMTS或HSDPA，电信的3G为EVDO
        // 在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA
        // String OperatorName = tm.getNetworkOperatorName();
        //中国电信
        if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
            ret = "EVDO_A";
        } else if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
            ret = "EVDO_0";
        } else if (type == TelephonyManager.NETWORK_TYPE_CDMA) {
            ret = "CDMA";
        } else if (type == TelephonyManager.NETWORK_TYPE_1xRTT) {
            ret = "1xRTT";
        }
        //移动(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
        else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
            ret = "EDGE";
        } else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
            ret = "GPRS";
        } else if (type == TelephonyManager.NETWORK_TYPE_LTE) {
            ret = "LTE";
        }
        //联通(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
        else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
            ret = "GPRS";
        } else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
            ret = "EDGE";
        } else if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
            ret = "UMTS";
        } else if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
            ret = "HSDPA";
        }
        return ret;
    }


    public static String getbts(Context ctx) {
        String state = getNetType(ctx);
        String ret = "";
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        ret = mobile(tm);
        return ret;
    }


    public static String getnearbts(Context ctx) {
        String ret = "";
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        ArrayList<CellInfo> cellInfos = new ArrayList<>();
        mobile(cellInfos, tm);
        for (int i = 0; i < cellInfos.size(); i++) {
            ret += cellInfos.get(i).toString() + "|";
        }
        if(ret.length()>0){
            return ret.substring(0, ret.length() - 1);
        }else {
            return "";
        }

    }

    public static ArrayList<CellInfo> getCellInfos(Context ctx) {
        ArrayList<CellInfo> cellInfos = new ArrayList<CellInfo>();
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //网络制式
        int type = tm.getNetworkType();
        /**
         * 获取SIM卡的IMSI码
         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         */
        @SuppressLint("MissingPermission")
        String imsi = tm.getSubscriberId();
//        LogUtil.d("imsi: " + imsi);
        //为了区分移动、联通还是电信，推荐使用imsi来判断(万不得己的情况下用getNetworkType()判断，比如imsi为空时)
        if (imsi != null && !"".equals(imsi)) {
            LogUtil.d("imsi");
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
                // 中国移动
                mobile(cellInfos, tm);
            } else if (imsi.startsWith("46001")) {
                // 中国联通
                union(cellInfos, tm);
            } else if (imsi.startsWith("46003")) {
                // 中国电信
                cdma(cellInfos, tm);
            }
        } else {
            LogUtil.e("type");
            // 在中国，联通的3G为UMTS或HSDPA，电信的3G为EVDO
            // 在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA
            // String OperatorName = tm.getNetworkOperatorName();
            //中国电信
            if (type == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || type == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || type == TelephonyManager.NETWORK_TYPE_CDMA
                    || type == TelephonyManager.NETWORK_TYPE_1xRTT) {
                cdma(cellInfos, tm);
            }
            //移动(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_GPRS) {
                mobile(cellInfos, tm);
            }
            //联通(EDGE（2.75G）是GPRS（2.5G）的升级版，速度比GPRS要快。目前移动基本在国内升级普及EDGE，联通则在大城市部署EDGE。)
            else if (type == TelephonyManager.NETWORK_TYPE_GPRS
                    || type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_UMTS
                    || type == TelephonyManager.NETWORK_TYPE_HSDPA) {
                union(cellInfos, tm);
            }
        }
        return cellInfos;
    }

    /**
     * 电信
     *
     * @param cellInfos
     * @param tm
     */
    private static void cdma(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
        @SuppressLint("MissingPermission")
        CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        @SuppressLint("MissingPermission") List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(list.get(i).getCid());
            cell.setLocationAreaCode(location.getNetworkId());
            cell.setMobileNetworkCode(String.valueOf(location.getSystemId()));
            cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            cell.setRadioType("cdma");
            cell.setmRssi(list.get(i).getRssi() + "");
            cellInfos.add(cell);
        }
    }

    /**
     * 电信
     *
     * @param tm
     */
    private static String cdma(TelephonyManager tm) {
        @SuppressLint("MissingPermission")
        CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        info.setmSystemId(String.valueOf(location.getSystemId()));
        info.setmNetworkId(location.getNetworkId() + "");
        info.setmBaseStationId(location.getBaseStationId() + "");
        info.setmBaseStationLongitude(location.getBaseStationLongitude() + "");
        info.setmBaseStationLatitude(location.getBaseStationLatitude() + "");
        info.setRadioType("cdma");
        info.setmRssi(lastStrength+"");
        return info.toString();
    }

    /**
     * 移动
     *
     * @param cellInfos
     * @param tm
     */
    private static void mobile(ArrayList<CellInfo> cellInfos,
                               TelephonyManager tm) {
        @SuppressLint("MissingPermission") GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        @SuppressLint("MissingPermission") List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(list.get(i).getCid());
            cell.setLocationAreaCode(location.getLac());
            if(tm.getNetworkOperator().length()>0) {
                cell.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
                cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            }
            cell.setRadioType("gsm");
            cell.setmRssi(list.get(i).getRssi() + "");
            cellInfos.add(cell);
        }
    }

    /**
     * 移动
     *
     * @param tm
     */
    private static String mobile(TelephonyManager tm) {
        @SuppressLint("MissingPermission") GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        info.setCellId(location.getCid());
        info.setLocationAreaCode(location.getLac());
        if(tm.getNetworkOperator().length()>0){
            info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
            info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        }
        info.setRadioType("gsm");
        info.setmRssi(lastStrength+"");
        return info.toString();
    }

    /**
     * 联通
     *
     * @param cellInfos
     * @param tm
     */
    private static void union(ArrayList<CellInfo> cellInfos, TelephonyManager tm) {
        @SuppressLint("MissingPermission") GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        //前面获取到的都是单个基站的信息，接下来再获取周围邻近基站信息以辅助通过基站定位的精准性
        // 获得邻近基站信息
        @SuppressLint("MissingPermission") List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CellInfo cell = new CellInfo();
            cell.setCellId(list.get(i).getCid());
            cell.setLocationAreaCode(location.getLac());
            if(tm.getNetworkOperator().length()>0){
                cell.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
                cell.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
            }
            cell.setRadioType("gsm");
            cell.setmRssi(list.get(i).getRssi() + "");
            cellInfos.add(cell);
        }
    }

    /**
     * 联通
     *
     * @param tm
     */
    private static String union(TelephonyManager tm) {
        @SuppressLint("MissingPermission") GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        CellInfo info = new CellInfo();
        //经过测试，获取联通数据以下两行必须去掉，否则会出现错误，错误类型为JSON Parsing Error
        //info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
        //info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        info.setCellId(location.getCid());
        info.setLocationAreaCode(location.getLac());
        if(tm.getNetworkOperator().length()>0){
            info.setMobileNetworkCode(tm.getNetworkOperator().substring(3, 5));
            info.setMobileCountryCode(tm.getNetworkOperator().substring(0, 3));
        }
        info.setRadioType("gsm");
        info.setmRssi(lastStrength+"");
        return info.toString();
    }


}
