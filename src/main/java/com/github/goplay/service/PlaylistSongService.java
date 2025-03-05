package com.github.goplay.service;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.PlaylistSong;
import com.github.goplay.entity.RoomSong;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.PlaylistSongMapper;
import org.springframework.stereotype.Service;

@Service
public class PlaylistSongService {
    private final PlaylistSongMapper playlistSongMapper;
    private final UserService userService;

    public PlaylistSongService(PlaylistSongMapper playlistSongMapper, UserService userService) {
        this.playlistSongMapper = playlistSongMapper;
        this.userService = userService;
    }

    public int addPlaylistSong(Integer playlistId, Integer songId, Integer userId) {
        UserInfo userInfo = userService.getUserInfoById(userId);
        if(userInfo==null)
            return -1;
        PlaylistSong playlistSong = new PlaylistSong(0, playlistId, songId, userId, userInfo.getUsername());
        return playlistSongMapper.insert(playlistSong);
    }
}
