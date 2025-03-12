package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

public class SongInfo implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String songName;
    private String songArtist;
    private Integer songDuration;
    private String songAlbum;
    private Integer songSize;

    public SongInfo(Integer id, String songName, String songArtist, Integer songDuration) {
        this.id = id;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
    }

    public SongInfo(Integer id, String songName, String songArtist, Integer songDuration, String songAlbum, Integer songSize) {
        this.id = id;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.songAlbum = songAlbum;
        this.songSize = songSize;
    }

    public SongInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public Integer getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(Integer songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public Integer getSongSize() {
        return songSize;
    }

    public void setSongSize(Integer songSize) {
        this.songSize = songSize;
    }
}
