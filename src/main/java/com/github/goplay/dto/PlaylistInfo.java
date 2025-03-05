package com.github.goplay.dto;
import java.util.List;

import com.github.goplay.entity.Playlist;

public class PlaylistInfo {
    private Playlist playlist;
    private List<SongContent> songContentList;

    public PlaylistInfo(Playlist playlist, List<SongContent> songContentList) {
        this.playlist = playlist;
        this.songContentList = songContentList;
    }

    public PlaylistInfo() {
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public List<SongContent> getSongContentList() {
        return songContentList;
    }

    public void setSongContentList(List<SongContent> songContentList) {
        this.songContentList = songContentList;
    }
}
