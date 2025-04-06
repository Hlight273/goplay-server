package com.github.goplay.dto.VO;

import com.github.goplay.dto.SongContent;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class PostVO {
    private Integer id;
    private String contentText;
    private SongContent songContent;
    private String linkUrl;
    private List<String> imageUrls;
    private Integer addedBy;
    private String addedByName;
    private String addedByAvatar;
    private LocalDateTime addedAt;
    private Integer likeCount;
    private Boolean likedByCurrentUser;
    private Integer commentCount;

    public PostVO() {
    }

    public PostVO(Integer id, String contentText, SongContent songContent, String linkUrl, List<String> imageUrls, Integer addedBy, String addedByName, String addedByAvatar, LocalDateTime addedAt, Integer likeCount, Boolean likedByCurrentUser, Integer commentCount) {
        this.id = id;
        this.contentText = contentText;
        this.songContent = songContent;
        this.linkUrl = linkUrl;
        this.imageUrls = imageUrls;
        this.addedBy = addedBy;
        this.addedByName = addedByName;
        this.addedByAvatar = addedByAvatar;
        this.addedAt = addedAt;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
        this.commentCount = commentCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public SongContent getSongContent() {
        return songContent;
    }

    public void setSongContent(SongContent songContent) {
        this.songContent = songContent;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Integer getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(Integer addedBy) {
        this.addedBy = addedBy;
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

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(Boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}

