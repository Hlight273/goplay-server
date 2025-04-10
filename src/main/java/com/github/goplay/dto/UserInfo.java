package com.github.goplay.dto;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private Integer id;
    private String username;
    private String avatarUrl;
    private Integer room_id;
    private Integer isOnline;
    private Integer privilege;
    private Integer level;
    private String nickname;
    private Integer hPoints;
    private Integer isActive;

    public UserInfo(Integer id, String username, String avatarUrl, Integer level, String nickname) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.level = level;
        this.nickname = nickname;
    }

    public UserInfo(Integer id, String username, String avatarUrl, Integer room_id, Integer privilege, String nickname) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.room_id = room_id;
        this.privilege = privilege;
        this.nickname = nickname;
    }

    public UserInfo(Integer id, String username, String avatarUrl, Integer room_id, Integer isOnline, Integer privilege, Integer level, String nickname, Integer hPoints, Integer isActive) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.room_id = room_id;
        this.isOnline = isOnline;
        this.privilege = privilege;
        this.level = level;
        this.nickname = nickname;
        this.hPoints = hPoints;
        this.isActive = isActive;
    }

    public UserInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getRoom_id() {
        return room_id;
    }

    public void setRoom_id(Integer room_id) {
        this.room_id = room_id;
    }

    public Integer getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer gethPoints() {
        return hPoints;
    }

    public void sethPoints(Integer hPoints) {
        this.hPoints = hPoints;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }
}
