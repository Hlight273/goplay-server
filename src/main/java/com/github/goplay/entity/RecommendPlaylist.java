package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

public class RecommendPlaylist implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer playlistId;
    private Integer isActive;

    public RecommendPlaylist(Integer id, Integer playlistId) {
        this.id = id;
        this.playlistId = playlistId;
        this.isActive = 1;
    }

    public RecommendPlaylist() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
