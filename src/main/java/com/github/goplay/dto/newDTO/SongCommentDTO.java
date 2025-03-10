package com.github.goplay.dto.newDTO;

import java.sql.Timestamp;
import java.util.List;

public class SongCommentDTO {
    private Integer id;
    private Integer parentId; // null 表示是一级评论
    private Integer songId;
    private Integer addedBy;
    private String addedByName;
    private Timestamp addedAt;
    private String contentText;
    private Integer likeCount;
    private Integer isActive;

    private List<SongCommentDTO> replies; // 已加载的二级评论
    private Integer totalReplies; // 该评论下的总二级评论数量
    private Integer loadedRepliesCount; // 已加载的二级评论数量

    public SongCommentDTO(Integer id, Integer parentId, Integer songId, Integer addedBy, String addedByName, Timestamp addedAt, String contentText, Integer likeCount, Integer isActive, List<SongCommentDTO> replies, Integer totalReplies, Integer loadedRepliesCount) {
        this.id = id;
        this.parentId = parentId;
        this.songId = songId;
        this.addedBy = addedBy;
        this.addedByName = addedByName;
        this.addedAt = addedAt;
        this.contentText = contentText;
        this.likeCount = likeCount;
        this.isActive = isActive;
        this.replies = replies;
        this.totalReplies = totalReplies;
        this.loadedRepliesCount = loadedRepliesCount;
    }

    public SongCommentDTO() {
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

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
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

    public List<SongCommentDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<SongCommentDTO> replies) {
        this.replies = replies;
    }

    public Integer getTotalReplies() {
        return totalReplies;
    }

    public void setTotalReplies(Integer totalReplies) {
        this.totalReplies = totalReplies;
    }

    public Integer getLoadedRepliesCount() {
        return loadedRepliesCount;
    }

    public void setLoadedRepliesCount(Integer loadedRepliesCount) {
        this.loadedRepliesCount = loadedRepliesCount;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }
}
