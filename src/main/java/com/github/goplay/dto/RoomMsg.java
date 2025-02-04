package com.github.goplay.dto;

import java.sql.Timestamp;

public class RoomMsg {
    private UserInfo userInfo;
    private java.sql.Timestamp sendAt;
    private String msg;

    public RoomMsg() {
    }

    public RoomMsg(UserInfo userInfo, Timestamp sendAt, String msg) {
        this.userInfo = userInfo;
        this.sendAt = sendAt;
        this.msg = msg;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Timestamp getSendAt() {
        return sendAt;
    }

    public void setSendAt(Timestamp sendAt) {
        this.sendAt = sendAt;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
