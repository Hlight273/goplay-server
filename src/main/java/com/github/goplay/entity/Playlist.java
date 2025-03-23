package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.sql.Timestamp;

import static com.github.goplay.utils.CommonUtils.formatCurTime;

public class Playlist implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String title;

    private String description;

    private String coverUrl;

    @TableField(fill = FieldFill.INSERT)
    private Timestamp addedAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateAt;

    private Integer isActive;

    private Integer isPublic;

    public Playlist(Integer id, Integer userId, String title, String description, String coverUrl, Integer isActive, Integer isPublic) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.coverUrl = coverUrl;
        this.isActive = isActive;
        this.isPublic = isPublic;
    }

    public Playlist(Integer userId, String title, String description, String coverUrl,Integer isPublic) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.coverUrl = coverUrl;
        this.isPublic = isPublic;
    }

    public static Playlist newPlaylistByRoom(Integer userId, String nickName){
        String title = nickName+"的房间歌单";
        String description = "于"+formatCurTime()+"创建的的房间歌单";
        Playlist playlist = new Playlist(userId, title, description, "", 1);
        playlist.isActive = 1;
        return playlist;
    }

    public Playlist() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }
}