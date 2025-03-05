package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.PlaylistSong;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.PlaylistMapper;
import com.github.goplay.mapper.PlaylistSongMapper;
import com.github.goplay.mapper.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistSongMapper playlistSongMapper;
    @Autowired
    private SongMapper songMapper;
    @Autowired
    private RoomSongService roomSongService;
    @Autowired
    private PlaylistMapper playlistMapper;

    @Transactional
    public PlaylistInfo getPublicPlaylistInfo_by_playlistId(Integer playlistId){
        Playlist playlist = getPublicPlaylist_by_playlistId(playlistId);
        List<SongContent> songContentList = getPublicSongContentList_by_playlistId(playlistId);
        return new PlaylistInfo(playlist, songContentList);
    }

    private List<SongContent> getPublicSongContentList_by_playlistId(Integer playlistId){
        List<PlaylistSong> playlistSongs = playlistSongMapper.selectList(
                new QueryWrapper<PlaylistSong>()
                        .eq("playlist_id", playlistId)
                        .eq("is_active", 1)
        );
        if (playlistSongs.isEmpty())
            return Collections.emptyList();

       return roomSongService.convert_PlaylistSongList_to_SongContentList(playlistSongs);
    }

    public Playlist getPublicPlaylist_by_playlistId(Integer playlistId){
        Playlist playlist = playlistMapper.selectOne(
                new QueryWrapper<Playlist>()
                        .eq("id", playlistId)
                        .eq("is_active", 1)
                        .eq("is_public", 1)
        );
        return playlist;
    }

    public Playlist getPlaylist_by_playlistId(Integer playlistId){
        Playlist playlist = playlistMapper.selectOne(
                new QueryWrapper<Playlist>()
                        .eq("id", playlistId)
                        .eq("is_active", 1)
        );
        return playlist;
    }


}
