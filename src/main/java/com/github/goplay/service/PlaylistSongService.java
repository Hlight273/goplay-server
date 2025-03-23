package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.*;
import com.github.goplay.mapper.PlaylistSongMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PlaylistSongService {
    private final PlaylistSongMapper playlistSongMapper;
    private final UserService userService;

    public PlaylistSongService(PlaylistSongMapper playlistSongMapper, UserService userService) {
        this.playlistSongMapper = playlistSongMapper;
        this.userService = userService;
    }


    @CacheEvict(value = "playlistSong", key = "#playlistId")
    public int addPlaylistSong(Integer playlistId, Integer songId, Integer userId) {
        UserInfo userInfo = userService.getUserInfoById(userId);
        if(userInfo==null)
            return -1;
        PlaylistSong playlistSong = new PlaylistSong(0, playlistId, songId, userId, userInfo.getUsername());
        return playlistSongMapper.insert(playlistSong);
    }

    @CacheEvict(value = "playlistSong", key = "#playlistId")
    ///给pls表记录is_active设为false，返回其id或-1
    public int removePlaylistSong(Integer playlistId, Integer songId) {
        LambdaQueryWrapper<PlaylistSong> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlaylistSong::getPlaylistId, playlistId)
                .eq(PlaylistSong::getSongId, songId)
                .eq(PlaylistSong::getIsActive, 1);
        PlaylistSong playlistSong = playlistSongMapper.selectOne(wrapper);
        if(playlistSong==null)
            return -1;
        playlistSong.setIsActive(0);
        boolean success = playlistSongMapper.updateById(playlistSong)>-1;
        if(success)
            return playlistSong.getId();
        else
            return -1;
    }

    public boolean isExistPlaylistSong(Integer playlistId, Integer songId) {
        LambdaQueryWrapper<PlaylistSong> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlaylistSong::getPlaylistId, playlistId)
                .eq(PlaylistSong::getSongId, songId);
        return playlistSongMapper.selectCount(wrapper)>0;
    }


}
