package com.ctop.studentcard.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 课堂模式
 */
public class ClassModel {

    /**
     * sosInFlag :
     * sosOutFlag :
     * items : [{"startTime":"","endTime":"","period":[{"week":"1"}],"isEffect":""}]
     */

    private String sosInFlag;
    private String sosOutFlag;
    private List<ItemsBean> items;



    public String getSosInFlag() {
        return sosInFlag;
    }

    public void setSosInFlag(String sosInFlag) {
        this.sosInFlag = sosInFlag;
    }

    public String getSosOutFlag() {
        return sosOutFlag;
    }

    public void setSosOutFlag(String sosOutFlag) {
        this.sosOutFlag = sosOutFlag;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }


    public static class ItemsBean {
        /**
         * startTime :
         * endTime :
         * period : [{"week":"1"}]
         * isEffect :
         */

        private String startTime;
        private String endTime;
        private String isEffect;
        private List<PeriodBean> period;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getIsEffect() {
            return isEffect;
        }

        public void setIsEffect(String isEffect) {
            this.isEffect = isEffect;
        }

        public List<PeriodBean> getPeriod() {
            return period;
        }

        public void setPeriod(List<PeriodBean> period) {
            this.period = period;
        }

        public static class PeriodBean {
            public PeriodBean() {
            }
            public PeriodBean(String week) {
                this.week = week;
            }

            /**
             * week : 1
             */


            private String week;

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }
        }
    }


    /**
     * {"sosInFlag":"","sosOutFlag":"","items":[{"startTime":"","endTime":"","period":[{"week":"1"}],"isEffect":""}]}
     *
     *   String sosInFlag = strings[0];
     *   String sosOutFlag = strings[0];
     *
     * 1@
     * 1@
     * 1=0900-1130!1+2+3!1@
     * 2=0900-1130!1+2+3!1@
     * 序号=开始时间-结束时间!周期!是否生效
     */



    public static ClassModel parseJson(String json){
        String[] strings = json.split("@");
        ClassModel classModel = new ClassModel();
        String sosInFlag = strings[0];
        String sosOutFlag = strings[1];

        classModel.setSosInFlag(sosInFlag);
        classModel.setSosOutFlag(sosOutFlag);
        List<ItemsBean> beanList = new ArrayList<>();
        if(!"0".equals(strings[2])){
            for(int i= 2 ;i<strings.length ; i++){
                ItemsBean itemsBean = new ItemsBean();
                String items = strings[i];
                String[] xuhao = items.split("=");
                String[] gantanhao = xuhao[1].split("!");
                //时间段
                String shijiduan = gantanhao[0];
                String[] shi = shijiduan.split("-");
                itemsBean.setStartTime(shi[0]);
                itemsBean.setEndTime(shi[1]);
                //周期
                String zhouqi = gantanhao[1];
                List<ItemsBean.PeriodBean> periodBeans = new ArrayList<>();
                String[] zhouqis = zhouqi.split("\\+");
                for(int f = 0;f<zhouqis.length;f++){
                    periodBeans.add(new ItemsBean.PeriodBean(zhouqis[f]));
                }
                itemsBean.setPeriod(periodBeans);
                //生效
                String shengxiao = gantanhao[2];
                itemsBean.setIsEffect(shengxiao);
                beanList.add(itemsBean);
            }
        }
        classModel.setItems(beanList);
        return classModel;
    }
}
