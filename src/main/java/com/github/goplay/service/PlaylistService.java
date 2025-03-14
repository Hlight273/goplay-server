package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.PlaylistSong;
import com.github.goplay.mapper.PlaylistMapper;
import com.github.goplay.mapper.PlaylistSongMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {


    private final PlaylistSongMapper playlistSongMapper;
    private final RoomSongService roomSongService;
    private final PlaylistMapper playlistMapper;

    public PlaylistService(PlaylistSongMapper playlistSongMapper, RoomSongService roomSongService, PlaylistMapper playlistMapper) {
        this.playlistSongMapper = playlistSongMapper;
        this.roomSongService = roomSongService;
        this.playlistMapper = playlistMapper;
    }


    public PlaylistInfo getPublicPlaylistInfo_by_playlistId(Integer playlistId){
        Playlist playlist = getPublicPlaylist_by_playlistId(playlistId);
        List<SongContent> songContentList = getSongContentList_by_playlistId(playlistId);
        return new PlaylistInfo(playlist, songContentList);
    }

    public PlaylistInfo getPlaylistInfo_by_playlistId(Integer playlistId){
        Playlist playlist = getPlaylist_by_playlistId(playlistId);
        List<SongContent> songContentList = getSongContentList_by_playlistId(playlistId);
        return new PlaylistInfo(playlist, songContentList);
    }

    @Cacheable(value = "playlistSong", key = "#playlistId")
    public List<SongContent> getSongContentList_by_playlistId(Integer playlistId){
        List<PlaylistSong> playlistSongs = playlistSongMapper.selectList(
                new QueryWrapper<PlaylistSong>()
                        .eq("playlist_id", playlistId)
                        .eq("is_active", 1)
        );
        if (playlistSongs.isEmpty())
            return Collections.emptyList();

       return roomSongService.convert_PlaylistSongList_to_SongContentList(playlistSongs);
    }

    @Cacheable(value = "publicPlaylist", key = "#playlistId", unless = "#result == null")
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

    //用户歌单
    public List<PlaylistInfo> get_PlaylistInfoList_ByOwnerId(Integer userId) {
        List<Playlist> playlists = playlistMapper.selectList(
                new QueryWrapper<Playlist>()
                        .eq("user_id", userId)
                        .eq("is_active", 1)
        );
        List<Integer> playlistIds = playlists.stream()
                .map(Playlist::getId)
                .collect(Collectors.toList());
        return get_PlaylistInfoList_ByPlaylistIds(playlistIds);
    }
    public List<PlaylistInfo> get_PublicPlaylistInfoList_ByOwnerId(Integer userId) {
        List<Playlist> playlists = playlistMapper.selectList(
                new QueryWrapper<Playlist>()
                        .eq("user_id", userId)
                        .eq("is_active", 1)
        );
        List<Integer> playlistIds = playlists.stream()
                .map(Playlist::getId)
                .collect(Collectors.toList());
        return get_PlaylistInfoList_ByPlaylistIds(playlistIds);
    }

    public List<PlaylistInfo> get_PlaylistInfoList_ByPlaylistIds(List<Integer> playlistIds) {
        List<PlaylistInfo> playlistInfos = new ArrayList<>();
        for(Integer playlistId : playlistIds){
            PlaylistInfo playlistInfo = getPlaylistInfo_by_playlistId(playlistId);
            if(playlistInfo!=null)
                playlistInfos.add(playlistInfo);
        }
        return playlistInfos;
    }

    @CacheEvict(value = "publicPlaylist", key = "#playlist.id")
    public int addPlaylist(Playlist playlist){
        if(playlistMapper.insert(playlist)>-1)
            return playlist.getId();
        return -1;
    }

    @CacheEvict(value = "publicPlaylist", key = "#playlist.id")
    public int updatePlaylist(Playlist playlist){
        if(playlistMapper.updateById(playlist)>-1)
            return playlist.getId();
        return -1;
    }

    public Playlist getPlaylistById(Integer playlistId){
        Playlist playlist = playlistMapper.selectById(playlistId);
        return playlist;
    }

    @CacheEvict(value = "publicPlaylist", key = "#playlistId")
    public int removePlaylist(Integer playlistId) {
        Playlist playlist = playlistMapper.selectById(playlistId);
        playlist.setIsActive(0);
        return playlistMapper.updateById(playlist);
    }




}
