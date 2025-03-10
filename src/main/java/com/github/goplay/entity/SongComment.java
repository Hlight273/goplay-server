package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class SongComment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer parentId;
    private Integer songId;
    private Integer addedBy;
    @TableField(fill = FieldFill.INSERT)
    private Timestamp addedAt;
    private String contentText;
    private Integer likeCount;
    private Integer isActive;

    public SongComment(Integer id, Integer parentId, Integer songId, Integer addedBy, String contentText, Integer likeCount) {
        this.id = id;
        this.parentId = parentId;
        this.songId = songId;
        this.addedBy = addedBy;
        this.contentText = contentText;
        this.likeCount = likeCount;
        this.isActive = 1;
    }

    public SongComment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
