package com.ctop.studentcard.bean;

import java.util.ArrayList;
import java.util.List;

public class ClickBean {


    private List<ItemsBean> items;

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }


    public static class ItemsBean {
        /**
         * time :
         * period : [{"week":""}]
         * isEffect :
         */

        private String time;
        private String isEffect;
        private List<PeriodBean> period;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
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
            public PeriodBean(String week) {
                this.week = week;
            }

            /**
             * week :
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
     *  //1=0900!1+2+3!0@2=1000!1+2+3!1
     *
     *  {"items":[{"time":"","period":[{"week":""}],"isEffect":""}]}
     *
     */
    public static ClickBean parseJson(String data){
        ClickBean clickBean = new ClickBean();
        List<ItemsBean> beanList = new ArrayList<>();
        String[] strings = data.split("@");//1=0900!1+2+3!0
        for(int i = 0;i<strings.length;i++){
            ItemsBean itemsBean = new ItemsBean();
            String[] bean = strings[i].split("=");//1=0900!1+2+3!0
            String strings1 = bean[1];//0900!1+2+3!0
            String[] three = strings1.split("!");
            itemsBean.setTime(three[0]);
            itemsBean.setIsEffect(three[2]);
            List<ItemsBean.PeriodBean> periodList = new ArrayList<>();
            String[] pers = three[1].split("\\+");
            for(int j = 0;j<pers.length;j++){
                ItemsBean.PeriodBean periodBean = new ItemsBean.PeriodBean(pers[j]);
                periodList.add(periodBean);
            }
            beanList.add(itemsBean);
        }
        clickBean.setItems(beanList);
        return clickBean;
    }

}
