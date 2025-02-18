package com.github.goplay.dto;

public class PlayerData {
    private Integer index;
    private String url;
    private Double curTime;
    private boolean paused;
    private Integer srcUserId;
    private boolean isExternal;

    public PlayerData(Integer index, String url, Double curTime, boolean paused, Integer srcUserId, boolean isExternal) {
        this.index = index;
        this.url = url;
        this.curTime = curTime;
        this.paused = paused;
        this.srcUserId = srcUserId;
        this.isExternal = isExternal;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public PlayerData() {
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getUrl() {
        return url;
    }

    public Integer getSrcUserId() {
        return srcUserId;
    }

    public void setSrcUserId(Integer srcUserId) {
        this.srcUserId = srcUserId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getCurTime() {
        return curTime;
    }

    public void setCurTime(Double curTime) {
        this.curTime = curTime;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
