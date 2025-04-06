package com.github.goplay.dto.newDTO;

import java.util.List;

public class PostDTO {
    private String contentText;
    private Integer songId;
    private String linkUrl;
    private List<String> imageUrls; // 对应多张图片

    public PostDTO() {
    }

    public PostDTO(String contentText, Integer songId, String linkUrl, List<String> imageUrls) {
        this.contentText = contentText;
        this.songId = songId;
        this.linkUrl = linkUrl;
        this.imageUrls = imageUrls;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public Integer getSongId() {
        return songId;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
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
}
