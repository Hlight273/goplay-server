package com.github.goplay.dto;

public class UserInfo {
    private Integer id;
    private String username;
    private String avatarUrl;
    private Integer room_id;
    private Integer privilege;

    public UserInfo(Integer id, String username, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public UserInfo(Integer id, String username, String avatarUrl, Integer room_id, Integer privilege) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.room_id = room_id;
        this.privilege = privilege;
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
}
