package com.github.goplay.dto;

public class PlayerData {
    private Integer index;
    private String url;
    private Integer curTime;
    private boolean paused;

    public PlayerData(Integer index, String url, Integer curTime, boolean paused) {
        this.index = index;
        this.url = url;
        this.curTime = curTime;
        this.paused = paused;
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

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCurTime() {
        return curTime;
    }

    public void setCurTime(Integer curTime) {
        this.curTime = curTime;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
