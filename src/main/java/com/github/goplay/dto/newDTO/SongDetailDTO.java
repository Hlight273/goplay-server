package com.github.goplay.dto.newDTO;

import com.github.goplay.entity.Song;
import com.github.goplay.entity.SongInfo;

public class SongDetailDTO {
    private Song song;
    private SongInfo songInfo;

    public SongDetailDTO(Song song, SongInfo songInfo) {
        this.song = song;
        this.songInfo = songInfo;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public SongInfo getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        this.songInfo = songInfo;
    }
}
