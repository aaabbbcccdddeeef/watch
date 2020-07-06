package com.wisdomin.studentcard.greendao;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class TemBean {

    @Id(autoincrement = true)
    private Long id;


    private String tem;

    private int status = 0;//默认0主动上报，1是定时上报 和 下发

    private long time;

    public TemBean(String tem, int status, long time) {
        this.tem = tem;
        this.status = status;
        this.time = time;
    }

    public TemBean() {
    }

    @Generated(hash = 1149551927)
    public TemBean(Long id, String tem, int status, long time) {
        this.id = id;
        this.tem = tem;
        this.status = status;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTem() {
        return tem;
    }

    public void setTem(String tem) {
        this.tem = tem;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
