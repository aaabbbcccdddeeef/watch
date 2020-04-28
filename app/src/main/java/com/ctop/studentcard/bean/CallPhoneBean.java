package com.ctop.studentcard.bean;

public class CallPhoneBean {
    private String number;
    private String time;
    private int state;
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {//1.呼入2.呼出3.未接
        String rets = "";
        if (state == 1) {
            rets = "已接";
        } else if (state == 2) {
            rets = "呼出";
        } else {
            rets = "未接";
        }
        return rets;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "CallPhoneBean{" +
                "number='" + number + '\'' +
                ", time='" + time + '\'' +
                ", state=" + state +
                '}';
    }
}
