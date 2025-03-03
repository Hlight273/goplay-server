package com.github.goplay.dto;

import java.sql.Timestamp;

public class VipInfo {
    private Integer userId;
    private Integer level;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer days;

    public VipInfo(Integer userId, Integer level, Timestamp startTime, Timestamp endTime, Integer days) {
        this.userId = userId;
        this.level = level;
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
    }

    public VipInfo() {
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
