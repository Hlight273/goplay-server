package com.github.goplay.dto;

import com.github.goplay.entity.SongInfo;

public class SongContent {

    private SongInfo songInfo;
    private String coverBase64;
    private String songUrl;

    public SongContent(SongInfo songInfo, String coverBase64, String songUrl) {
        this.songInfo = songInfo;
        this.coverBase64 = coverBase64;
        this.songUrl = songUrl;
    }

    public SongContent() {
    }

    public SongInfo getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        this.songInfo = songInfo;
    }

    public String getCoverBase64() {
        return coverBase64;
    }

    public void setCoverBase64(String coverBase64) {
        this.coverBase64 = coverBase64;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}
