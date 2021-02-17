package com.nameless.monitor_restart.entity;

public class RunningJob {
    private String jid;
    private String name;
    private String state;

    public String getJid() {
        return jid;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }
}
