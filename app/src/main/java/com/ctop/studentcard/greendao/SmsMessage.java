package com.ctop.studentcard.greendao;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SmsMessage {

    @Id(autoincrement = true)
    private Long id;

    private String uuid;

    private String date;

    private String message;

    private int status = 0;//默认0是未读，1是已读

    private long time;



    public SmsMessage(String date, String message, int status, long time, String uuid) {
        this.date = date;
        this.message = message;
        this.status = status;
        this.time = time;
        this.uuid = uuid;
    }

    public SmsMessage() {
    }

    @Generated(hash = 1376746162)
    public SmsMessage(Long id, String uuid, String date, String message, int status,
            long time) {
        this.id = id;
        this.uuid = uuid;
        this.date = date;
        this.message = message;
        this.status = status;
        this.time = time;
    }

    public String getDate() { return this.date; }

    public Long getId() { return this.id; }

    public String getMessage() { return this.message; }

    public int getStatus() { return this.status; }

    public long getTime() { return this.time; }

    public String getUuid() { return this.uuid; }

    public void setDate(String paramString) { this.date = paramString; }

    public void setMessage(String paramString) { this.message = paramString; }

    public void setStatus(int paramInt) { this.status = paramInt; }

    public void setTime(long paramLong) { this.time = paramLong; }

    public void setUuid(String paramString) { this.uuid = paramString; }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SmsMessage{id=");
        stringBuilder.append(this.id);
        stringBuilder.append(", message='");
        stringBuilder.append(this.message);
        stringBuilder.append('\'');
        stringBuilder.append(", status=");
        stringBuilder.append(this.status);
        stringBuilder.append(", date='");
        stringBuilder.append(this.date);
        stringBuilder.append('\'');
        stringBuilder.append(", time=");
        stringBuilder.append(this.time);
        stringBuilder.append(", uuid='");
        stringBuilder.append(this.uuid);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public void setId(Long id) {
        this.id = id;
    }
}
