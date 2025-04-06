package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class PostComment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer parentId;
    private Integer postId;
    private Integer addedBy;
    @TableField(fill = FieldFill.INSERT)
    private java.sql.Timestamp addedAt;
    private String contentText;
    private Integer isActive;

    public PostComment(Integer id, Integer parentId, Integer postId, Integer addedBy, Timestamp addedAt, String contentText, Integer isActive) {
        this.id = id;
        this.parentId = parentId;
        this.postId = postId;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
        this.contentText = contentText;
        this.isActive = isActive;
    }

    public PostComment() {
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

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
