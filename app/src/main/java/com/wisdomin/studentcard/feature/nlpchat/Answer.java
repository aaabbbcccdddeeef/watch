package com.wisdomin.studentcard.feature.nlpchat;

/**
 * {
 * 2019-01-04 12:22:48.576 7124-7124/calldead.redwolf.qqrebot I/RedWolfR: │   "ret": 0,
 * 2019-01-04 12:22:48.576 7124-7124/calldead.redwolf.qqrebot I/RedWolfR: │   "msg": "ok",
 * 2019-01-04 12:22:48.576 7124-7124/calldead.redwolf.qqrebot I/RedWolfR: │   "data": {
 * 2019-01-04 12:22:48.576 7124-7124/calldead.redwolf.qqrebot I/RedWolfR: │       "session": "5D3UYKEUQ4",
 * 2019-01-04 12:22:48.576 7124-7124/calldead.redwolf.qqrebot I/RedWolfR: │       "answer": "到底我是机器人还是你是机器人"
 * 2019-01-04 12:22:48.576 7124-7124/calldead.redwolf.qqrebot I/RedWolfR: │   }
 * 2019-01-04 12:22:48.576 7124-7124/calldead.redwolf.qqrebot I/RedWolfR: │ }
 */
public class Answer {
    private int ret;
    private String msg;
    private DataF data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataF getData() {
        return data;
    }

    public void setData(DataF data) {
        this.data = data;
    }

    public class DataF {
        private String session;
        private String answer;

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }


    @Override
    public String toString() {
        return "Answer{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
