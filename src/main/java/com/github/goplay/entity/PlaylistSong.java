package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class PlaylistSong {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer playlistId;

    private Integer songId;

    private Integer addedBy;

    @TableField(fill = FieldFill.INSERT)
    private Timestamp addedAt;

    private String addedUsername;

    private Integer isActive;

    public PlaylistSong(Integer id, Integer playlistId, Integer songId, Integer addedBy, String addedUsername) {
        this.id = id;
        this.playlistId = playlistId;
        this.songId = songId;
        this.addedBy = addedBy;
        this.addedUsername = addedUsername;
        this.isActive = 1;
    }

    public PlaylistSong() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getSongId() {
        return songId;
    }

    public Integer getAddedBy() {
        return addedBy;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public String getAddedUsername() {
        return addedUsername;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
    }

    public void setAddedUsername(String addedUsername) {
        this.addedUsername = addedUsername;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public void setAddedBy(Integer addedBy) {
        this.addedBy = addedBy;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }
}
