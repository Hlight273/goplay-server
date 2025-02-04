package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.goplay.utils.CommonUtils;

import java.sql.Timestamp;

public class RoomSong {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer roomId;
    private Integer songId;
    private Integer addedBy;
    private java.sql.Timestamp addedAt;
    private String addedUsername;
    private Integer isActive;

    public RoomSong(Integer id, Integer roomId, Integer songId, Integer addedBy, String addedUsername) {
        this.id = id;
        this.roomId = roomId;
        this.songId = songId;
        this.addedBy = addedBy;
        this.addedAt = CommonUtils.curTime();
        this.addedUsername = addedUsername;
        this.isActive = 1;
    }

    public RoomSong(Integer id, Integer roomId, Integer songId, Integer addedBy, Timestamp addedAt, String addedUsername, Integer isActive) {
        this.id = id;
        this.roomId = roomId;
        this.songId = songId;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.addedUsername = addedUsername;
        this.isActive = isActive;
    }

    public RoomSong() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getSongId() {
        return songId;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
    }

    public Integer getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(Integer addedBy) {
        this.addedBy = addedBy;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    public String getAddedUsername() {
        return addedUsername;
    }

    public void setAddedUsername(String addedUsername) {
        this.addedUsername = addedUsername;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
