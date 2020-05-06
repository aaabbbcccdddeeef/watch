package com.ctop.studentcard.util;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static final String format1 = "yyyy-MM-dd HH:mm:ss";
    public static final String format2 = "HH:mm:ss";
    public static final String format3 = "HH:mm";
    public static final String format4 = "HHmm";
    public static final String format5 = "HHmmss";
    public static final String format6 = "yyyyMMddHHmmss";

    /**
     * 设备时间
     *
     * @return
     */
    public static String systemTime() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sf.format(date);
        return dateStr;
    }

    /**
     * 结束时间
     *
     * @return
     */
    public static String getEndTime(long startTime, long duration) {
        long endLong = startTime + duration;
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(endLong));
        return date;
    }

    /**
     * 获取当前时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss</p>
     *
     * @return 时间字符串
     */
    public static String getNowTimeString(String format) {
        return millis2String(System.currentTimeMillis(), format);
    }

    /**
     * 将时间戳转为时间字符串
     * <p>格式为pattern</p>
     *
     * @param millis  毫秒时间戳
     * @param pattern 时间格式
     * @return 时间字符串
     */
    public static String millis2String(long millis, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date(millis));
    }


//    public static String getWeek() {
//        String week = "";
//        Date today = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(today);
//        int weekday = c.get(Calendar.DAY_OF_WEEK);
//        if (weekday == 1) {
//            week = "1";
//        } else if (weekday == 2) {
//            week = "2";
//        } else if (weekday == 3) {
//            week = "3";
//        } else if (weekday == 4) {
//            week = "4";
//        } else if (weekday == 5) {
//            week = "5";
//        } else if (weekday == 6) {
//            week = "6";
//        } else if (weekday == 7) {
//            week = "0";
//        }
//        return week;
//    }

    public static String getWeekInCome() {
        String week = "";
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (weekday == 1) {
            week = "0";
        } else if (weekday == 2) {
            week = "1";
        } else if (weekday == 3) {
            week = "2";
        } else if (weekday == 4) {
            week = "3";
        } else if (weekday == 5) {
            week = "4";
        } else if (weekday == 6) {
            week = "5";
        } else if (weekday == 7) {
            week = "6";
        }
        return week;
    }

    public static final long ONE_MINUTE_MILLIONS = 60 * 1000;
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;

    /**
     * 获取短时间格式
     *
     * @return
     */
    public static String getShortTime(long millis) {
        Date date = new Date(millis);
        Date curDate = new Date();

        String str = "";
        long durTime = curDate.getTime() - date.getTime();

        int dayStatus = calculateDayStatus(date, new Date());

        if (durTime <= 10 * ONE_MINUTE_MILLIONS) {
            str = "刚刚";
        } else if (durTime < ONE_HOUR_MILLIONS) {
            str = durTime / ONE_MINUTE_MILLIONS + "分钟前";
        } else if (dayStatus == 0) {
            str = durTime / ONE_HOUR_MILLIONS + "小时前";
        } else if (dayStatus == -1) {
            str = "昨天" + DateFormat.format("HH:mm", date);
        } else if (isSameYear(date, curDate) && dayStatus < -1) {
            str = DateFormat.format("MM-dd", date).toString();
        } else {
            str = DateFormat.format("yyyy-MM-dd", date).toString();
        }
        return str;

    }


    /**
     * 判断是否是同一年
     *
     * @param targetTime
     * @param compareTime
     * @return
     */
    public static boolean isSameYear(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }


    /**
     * 判断是否处于今天还是昨天，0表示今天，-1表示昨天，小于-1则是昨天以前
     *
     * @param targetTime
     * @param compareTime
     * @return
     */
    public static int calculateDayStatus(Date targetTime, Date compareTime) {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }

    //将字符串  转为  时间戳
    public static long changeChaToLong(String timestr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(timestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //日期转时间戳（毫秒）
        long time = date.getTime();
        System.out.print("Format To times:" + time);
        return time;
    }


    public static String changeChaToLongToString(String timestr) {
        return getShortTime(changeChaToLong(timestr));
    }

}
