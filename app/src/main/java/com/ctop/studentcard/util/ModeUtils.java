package com.ctop.studentcard.util;

import android.content.Context;

import com.ctop.studentcard.bean.ClassModel;

import java.util.List;

public class ModeUtils {



    public static boolean inclassmode(Context mContext){
        //课堂模式
        String classModelString = PreferencesUtils.getInstance(mContext).getString("classModel", "");
        ClassModel classModel = JsonUtil.parseObject(classModelString, ClassModel.class);
        if (classModel != null) {
            if (classModel.getItems().size() > 0) {
                List<ClassModel.ItemsBean> itemsBeanList = classModel.getItems();
                for (int i = 0; i < itemsBeanList.size(); i++) {
                    if ("0".equals(itemsBeanList.get(i).getIsEffect())) continue;//不生效
                    List<ClassModel.ItemsBean.PeriodBean> periodBeans = itemsBeanList.get(i).getPeriod();
                    for (int j = 0; j < periodBeans.size(); j++) {
                        if (TimeUtils.getWeekInCome().equals(periodBeans.get(j).getWeek())) {
                            int awaitstartInt = Integer.parseInt(itemsBeanList.get(i).getStartTime());
                            int awaitendInt = Integer.parseInt(itemsBeanList.get(i).getEndTime());
                            String timeNow = TimeUtils.getNowTimeString(TimeUtils.format4);
                            int timeNowInt = Integer.parseInt(timeNow);
                            if (awaitstartInt < awaitendInt) {//同一天
                                if (timeNowInt > awaitstartInt && timeNowInt < awaitendInt) {
                                    return true;
                                }
                            } else {//不同天
                                if (timeNowInt > awaitstartInt || timeNowInt < awaitendInt) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
