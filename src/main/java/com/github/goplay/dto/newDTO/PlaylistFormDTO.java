package com.github.goplay.dto.newDTO;

public class PlaylistFormDTO {
    private String title;
    private String description;
    private String coverUrl;
    private Integer isPublic;

    public PlaylistFormDTO(String title, String description, String coverUrl, Integer isPublic) {
        this.title = title;
        this.description = description;
        this.coverUrl = coverUrl;
        this.isPublic = isPublic;
    }

    public PlaylistFormDTO() {
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

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }
}
