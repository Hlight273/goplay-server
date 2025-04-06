package com.github.goplay.dto.newDTO;

import java.sql.Timestamp;
import java.util.List;

public class PostCommentDTO {
    private Integer id;
    private Integer parentId;
    private Integer postId;
    private Integer addedBy;
    private String addedByName;
    private String addedByAvatar;
    private Timestamp addedAt;
    private String contentText;
    private Integer isActive;

    private List<SongCommentDTO> replies; // 已加载的二级评论
    private Integer totalReplies; // 该评论下的总二级评论数量
    private Integer loadedRepliesCount; // 已加载的二级评论数量

    private String replyToName;//返回用 回复谁？
    private String replyToAvator;//返回用 回复谁？

    public PostCommentDTO(Integer id, Integer parentId, Integer postId, Integer addedBy, String addedByName, String addedByAvatar, Timestamp addedAt, String contentText, Integer isActive, List<SongCommentDTO> replies, Integer totalReplies, Integer loadedRepliesCount, String replyToName, String replyToAvator) {
        this.id = id;
        this.parentId = parentId;
        this.postId = postId;
        this.addedBy = addedBy;
        this.addedByName = addedByName;
        this.addedByAvatar = addedByAvatar;
        this.addedAt = addedAt;
        this.contentText = contentText;
        this.isActive = isActive;
        this.replies = replies;
        this.totalReplies = totalReplies;
        this.loadedRepliesCount = loadedRepliesCount;
        this.replyToName = replyToName;
        this.replyToAvator = replyToAvator;
    }

    public PostCommentDTO() {
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

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
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

    public String getReplyToName() {
        return replyToName;
    }

    public void setReplyToName(String replyToName) {
        this.replyToName = replyToName;
    }

    public String getReplyToAvator() {
        return replyToAvator;
    }

    public void setReplyToAvator(String replyToAvator) {
        this.replyToAvator = replyToAvator;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    public String getAddedByAvatar() {
        return addedByAvatar;
    }

    public void setAddedByAvatar(String addedByAvatar) {
        this.addedByAvatar = addedByAvatar;
    }
}
