package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final double updateWeight = 0.5;
    private final double favoriteWeight = 0.3;
    private final double playCountWeight = 0.2;

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

    /***
    从playlistId,构建其playlistInfo
     */
    public PlaylistInfo getPlaylistInfo_by_playlistId(Integer playlistId){
        Playlist playlist = getPlaylist_by_playlistId(playlistId);
        List<SongContent> songContentList = getSongContentList_by_playlistId(playlistId);
        return new PlaylistInfo(playlist, songContentList);
    }
    /***
     从playlistId,构建其playlistInfo(无论是不是active都查)
     */
    public PlaylistInfo getPlaylistInfo_by_playlistId_ActiveAndNotActive(Integer playlistId){
        Playlist playlist = getPlaylist_by_playlistId(playlistId, true);
        List<SongContent> songContentList = getSongContentList_by_playlistId(playlistId, true);
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
    @Cacheable(value = "playlistSong", key = "#playlistId")
    public List<SongContent> getSongContentList_by_playlistId(Integer playlistId, Boolean shouldGetUnActive){
        QueryWrapper<PlaylistSong> eq = new QueryWrapper<PlaylistSong>()
                .eq("playlist_id", playlistId);
        if(!shouldGetUnActive)
            eq.eq("is_active", 1);
        List<PlaylistSong> playlistSongs = playlistSongMapper.selectList( eq);
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
    public Playlist getPlaylist_by_playlistId(Integer playlistId, Boolean shouldGetUnActive){
        QueryWrapper<Playlist> eq = new QueryWrapper<Playlist>()
                .eq("id", playlistId);
        if(!shouldGetUnActive)
            eq.eq("is_active", 1);
        Playlist playlist = playlistMapper.selectOne(eq);
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
                        .eq("is_public", 1));
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

    public Page<PlaylistInfo> searchPlaylists(String keyword, int page, int size, boolean canSearchPrivate, int searcherId) {
        LambdaQueryWrapper<Playlist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                .like(Playlist::getTitle, keyword)
                .or()
                .like(Playlist::getDescription, keyword)
                .or()
                .apply("id IN (SELECT playlist_id FROM playlist_song WHERE song_id IN (SELECT id FROM song WHERE title LIKE {0}))", "%" + keyword + "%"));

        if (!canSearchPrivate) {//根据是否是负责人以上判定，是能查所有歌单，还是仅公开歌单(除非自己的)
            queryWrapper.eq(Playlist::getIsPublic, 1).or() .eq(Playlist::getUserId, searcherId);
        }

        queryWrapper.orderByDesc(Playlist::getUpdateAt); //暂时根据时间排序


        Page<Playlist> playlistPage = playlistMapper.selectPage(new Page<>(page, size), queryWrapper);
        List<PlaylistInfo> playlistInfoList = convertToPlaylistInfo(playlistPage.getRecords());

        Page<PlaylistInfo> resultPage = new Page<>(page, size, playlistPage.getTotal());
        resultPage.setRecords(playlistInfoList);
        return resultPage;
    }
    private List<PlaylistInfo> convertToPlaylistInfo(List<Playlist> playlists) {
        if (playlists.isEmpty()) {
            return List.of();
        }

        List<Integer> playlistIds = playlists.stream().map(Playlist::getId).collect(Collectors.toList());

        // **批量获取所有相关的歌曲内容**
        Map<Integer, List<SongContent>> playlistSongsMap = fetchSongContentsByPlaylistIds(playlistIds);

        return playlists.stream().map(playlist -> {
            PlaylistInfo info = new PlaylistInfo();
            info.setPlaylist(playlist);
            info.setSongContentList(playlistSongsMap.getOrDefault(playlist.getId(), List.of()));
            return info;
        }).collect(Collectors.toList());
    }
    private Map<Integer, List<SongContent>> fetchSongContentsByPlaylistIds(List<Integer> playlistIds) {
        if (playlistIds.isEmpty()) {
            return Map.of();
        }

        // 批量查询所有歌单的歌曲
        List<PlaylistSong> playlistSongs = playlistSongMapper.selectList(
                new LambdaQueryWrapper<PlaylistSong>()
                        .in(PlaylistSong::getPlaylistId, playlistIds)
                        .eq(PlaylistSong::getIsActive, true)
        );

        // 将 PlaylistSong 转换成 SongContent
        List<SongContent> songContents = roomSongService.convert_PlaylistSongList_to_SongContentList(playlistSongs);

        // 使用 Map 按 playlistId 进行分组
        Map<Integer, List<SongContent>> resultMap = new HashMap<>();

        // 将 songContents 按 playlistId 分组
        for (PlaylistSong playlistSong : playlistSongs) {
            // 获取对应的 songContent
            List<SongContent> correspondingSongs = songContents.stream()
                    .filter(songContent -> songContent.getSongInfo().getId().equals(playlistSong.getSongId()))
                    .collect(Collectors.toList());

            if (!correspondingSongs.isEmpty()) {
                resultMap.computeIfAbsent(playlistSong.getPlaylistId(), k -> new ArrayList<>())
                        .addAll(correspondingSongs);
            }
        }

        return resultMap;
    }









}
