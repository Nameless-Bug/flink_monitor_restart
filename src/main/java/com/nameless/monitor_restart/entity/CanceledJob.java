package com.nameless.monitor_restart.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class CanceledJob {

    private String jid;
    private String name;
    private String state;
    private String endtime;

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @JSONField(name = "end-time")
    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
