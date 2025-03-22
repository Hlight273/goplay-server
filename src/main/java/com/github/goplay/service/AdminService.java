package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.newDTO.SongDetailDTO;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.Song;
import com.github.goplay.entity.SongInfo;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.PlaylistMapper;
import com.github.goplay.mapper.SongInfoMapper;
import com.github.goplay.mapper.SongMapper;
import com.github.goplay.mapper.UserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {


    private final PlaylistMapper playlistMapper;
    private final SongMapper songMapper;
    private final UserMapper userMapper;
    private final SongInfoMapper songInfoMapper;
    private final PlaylistService playlistService;
    private final UserService userService;

    public AdminService(PlaylistMapper playlistMapper, SongMapper songMapper, UserMapper userMapper, SongInfoMapper songInfoMapper, SongService songService, PlaylistService playlistService, UserService userService) {
        this.playlistMapper = playlistMapper;
        this.songMapper = songMapper;
        this.userMapper = userMapper;
        this.songInfoMapper = songInfoMapper;
        this.playlistService = playlistService;
        this.userService = userService;
    }

//    public Page<Playlist> getAllPlaylists(int page, int size) {
//        return playlistMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<>());
//    }

    private Page<Playlist> searchPlaylists(String keyword, int page, int size) {
        LambdaQueryWrapper<Playlist> query = new LambdaQueryWrapper<>();
        query.like(Playlist::getTitle, keyword)
             .or().like(Playlist::getDescription, keyword);
        return playlistMapper.selectPage(new Page<>(page, size), query);
    }

    public Page<PlaylistInfo> searchPlaylistInfos(String keyword, int page, int size) {
        IPage<Playlist> playlistPage = searchPlaylists(keyword, page, size);

        List<PlaylistInfo> playlistInfos = playlistPage.getRecords().stream()
                .map(playlist -> playlistService.getPlaylistInfo_by_playlistId_ActiveAndNotActive(playlist.getId())).collect(Collectors.toList());

        return new Page<PlaylistInfo>(page, size, playlistPage.getTotal()).setRecords(playlistInfos);
    }

    public IPage<SongDetailDTO> searchSongDetails(String keyword, int page, int size) {
        IPage<SongInfo> songInfoPage = searchSongInfos(keyword, page, size);

        List<SongDetailDTO> songDetails = songInfoPage.getRecords().stream().map(songInfo -> {
            Song song = songMapper.selectById(songInfo.getId());
            return new SongDetailDTO(song, songInfo);
        }).collect(Collectors.toList());

        return new Page<SongDetailDTO>(page, size, songInfoPage.getTotal()).setRecords(songDetails);
    }


    private Page<SongInfo> searchSongInfos(String keyword, int page, int size) {
        LambdaQueryWrapper<SongInfo> query = new LambdaQueryWrapper<>();
        query.like(SongInfo::getSongName, keyword)
             .or().like(SongInfo::getSongArtist, keyword)
             .or().like(SongInfo::getSongAlbum, keyword);//搜索歌曲关键字：歌曲名、艺人名、专辑名
        return songInfoMapper.selectPage(new Page<>(page, size), query);
    }

    public Page<UserInfo> searchUsers(String keyword, int page, int size) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.like(User::getUsername, keyword)
             .or().like(User::getNickname, keyword);//用户搜索关键字：用户名、昵称
        IPage<User> userPage = userMapper.selectPage(new Page<>(page, size), query);
        List<UserInfo> userInfos = userPage.getRecords().stream()
                .map(user -> userService.getUserInfoById(user.getId())).collect(Collectors.toList());//user转userinfo
        return new Page<UserInfo>(page, size, userPage.getTotal()).setRecords(userInfos);
    }

    // 下架歌单
    @CacheEvict(value = "publicPlaylist", key = "#playlistId")
    public boolean deactivatePlaylist(Integer playlistId) {
        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist == null) return false;
        playlist.setIsActive(0);
        return playlistMapper.updateById(playlist) > 0;
    }

    // 上架歌单
    @CacheEvict(value = "publicPlaylist", key = "#playlistId")
    public boolean activatePlaylist(Integer playlistId) {
        Playlist playlist = playlistMapper.selectById(playlistId);
        if (playlist == null) return false;
        playlist.setIsActive(1);
        return playlistMapper.updateById(playlist) > 0;
    }

    // 下架歌曲
    public boolean deactivateSong(Integer songId) {
        Song song = songMapper.selectById(songId);
        if (song == null) return false;
        song.setIsActive(0);
        return songMapper.updateById(song) > 0;
    }

    // 上架歌曲
    public boolean activateSong(Integer songId) {
        Song song = songMapper.selectById(songId);
        if (song == null) return false;
        song.setIsActive(1);
        return songMapper.updateById(song) > 0;
    }

    // 封禁用户
    @CacheEvict(value = "userInfo", key = "#userId")
    public boolean deactivateUser(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return false;
        user.setIsActive(0);
        return userMapper.updateById(user) > 0;
    }

    // 解禁用户
    @CacheEvict(value = "userInfo", key = "#userId")
    public boolean activateUser(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return false;
        user.setIsActive(1);
        return userMapper.updateById(user) > 0;
    }

    // 更新歌单信息
    public boolean updatePlaylist(Integer playlistId, Playlist updatedPlaylist) {
        updatedPlaylist.setId(playlistId);
        return playlistMapper.updateById(updatedPlaylist) > 0;
    }

    // 更新歌曲信息
    public boolean updateSong(Integer songId, Song updatedSong) {
        updatedSong.setId(songId);
        return songMapper.updateById(updatedSong) > 0;
    }
}
