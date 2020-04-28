package com.ctop.studentcard.bean;

import java.util.ArrayList;
import java.util.List;

public class IncomingCall {
    /**
     * deletePhone : [{"phone":""},{"phone":""}]
     * addPhone : [{"phone":"","time":[{"start":"0900","end":"2300"}]},{"phone":"","time":[{"start":"0900","end":"2300"}]}]
     * callLimit : 1
     * period : [{"week":"1"},{"week":"2"}]
     */

    private String callLimit;
    private List<DeletePhoneBean> deletePhone;
    private List<AddPhoneBean> addPhone;
    private List<PeriodBean> period;

    public String getCallLimit() {
        return callLimit;
    }

    public void setCallLimit(String callLimit) {
        this.callLimit = callLimit;
    }

    public List<DeletePhoneBean> getDeletePhone() {
        return deletePhone;
    }

    public void setDeletePhone(List<DeletePhoneBean> deletePhone) {
        this.deletePhone = deletePhone;
    }

    public List<AddPhoneBean> getAddPhone() {
        return addPhone;
    }

    public void setAddPhone(List<AddPhoneBean> addPhone) {
        this.addPhone = addPhone;
    }

    public List<PeriodBean> getPeriod() {
        return period;
    }

    public void setPeriod(List<PeriodBean> period) {
        this.period = period;
    }

    public static class DeletePhoneBean {
        /**
         * phone :
         */

        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class AddPhoneBean {
        /**
         * phone :
         * time : [{"start":"0900","end":"2300"}]
         */

        private String phone;
        private List<TimeBean> time;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public List<TimeBean> getTime() {
            return time;
        }

        public void setTime(List<TimeBean> time) {
            this.time = time;
        }

        public static class TimeBean {
            /**
             * start : 0900
             * end : 2300
             */

            private String start;
            private String end;

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }
        }
    }

    public static class PeriodBean {
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


    //删除号码@添加号码和时段@呼入限制@周期
    //13900000000!13900000001!13900000002!13900000003!
    // @13900000004=0600-2230+!13900000005=0300-0600+0900-1130+!
    // @2
    // @0+1+2+3+4+5+6
    /**
     * {"deletePhone":[{"phone":""},{"phone":""}],"addPhone":[{"phone":"","time":["start1":"0900","end1":"2300"]},{"phone":"","time":["start1":"0900","end1":"2300"]}],"callLimit":"1","period":[{"week":"1"},{"week":"2"}]}
     */

    public static IncomingCall parseJson(String json){
        IncomingCall incomingCall = new IncomingCall();
        List<DeletePhoneBean> deletePhones = new ArrayList<>();
        List<AddPhoneBean> addPhoneBeans = new ArrayList<>();
        List<PeriodBean> periodBeans = new ArrayList<>();

        PeriodBean periodBean = new PeriodBean();
        String[] datas = json.split("@");
        //deletePhone
        String deletePhone = datas[0];
        String[] phones = deletePhone.split("!");
        for (int i = 0;i<phones.length;i++){
            DeletePhoneBean deletePhoneBean = new DeletePhoneBean();
            deletePhoneBean.setPhone(phones[i]);
            deletePhones.add(deletePhoneBean);
        }
        incomingCall.setDeletePhone(deletePhones);
        //addPhone
        //13900000004=0600-2230+!13900000005=0300-0600+0900-1130+!
        String addPhone = datas[1];//13900000004=0600-2230+!13900000005=0300-0600+0900-1130+!
        String[] addphones = addPhone.split("!");//多个13900000005=0300-0600+0900-1130+
        for (int i = 0;i<addphones.length;i++){
            AddPhoneBean  addPhoneBean = new AddPhoneBean();
            String strings = addphones[i];//13900000005=0300-0600+0900-1130+
            String[] addPhone2 = strings.split("=");
            addPhoneBean.setPhone(addPhone2[0]);
            String[] addPhone3 = addPhone2[1].split("\\+");//多个0600-2230
            List<AddPhoneBean.TimeBean> times = new ArrayList<>();
            for (int j = 0;j<addPhone3.length;j++){
                String strings1 = addPhone3[j];
                String[] strings2 = strings1.split("-");
                AddPhoneBean.TimeBean timeBean = new AddPhoneBean.TimeBean();
                timeBean.setStart(strings2[0]);
                timeBean.setEnd(strings2[1]);
                times.add(timeBean);
            }
            addPhoneBean.setTime(times);
            addPhoneBeans.add(addPhoneBean);
        }
        incomingCall.setAddPhone(addPhoneBeans);
        //callLimit
        String callLimit = datas[2];
        incomingCall.setCallLimit(callLimit);
        //period    0+1+2+3+4+5+6
        String period = datas[3];
        List<PeriodBean> periods = new ArrayList<>();
        String[] periods1 = period.split("\\+");//多个0600-2230
        for(int i = 0;i<periods1.length;i++){
            PeriodBean periodBean1 =  new PeriodBean();
            periodBean1.setWeek(periods1[i]);
            periods.add(periodBean1);
        }
        incomingCall.setPeriod(periods);
        return incomingCall;

    }

}
