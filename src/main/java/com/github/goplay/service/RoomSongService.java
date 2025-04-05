package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.goplay.dto.SongContent;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.PlaylistSong;
import com.github.goplay.entity.RoomSong;
import com.github.goplay.entity.Song;
import com.github.goplay.entity.SongInfo;
import com.github.goplay.mapper.RoomSongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.goplay.utils.FileUtils.getAudioFileNameByPath;
import static com.github.goplay.utils.FileUtils.getImgStrToBase64;

@Service
public class RoomSongService {



    private SongService songService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomSongMapper roomSongMapper;

    @Lazy
    @Autowired
    public void setSongService(SongService songService) {
        this.songService = songService;
    }

    @Autowired
    private SongInfoService songInfoService ;

    public int addRoomSong(RoomSong roomSong) {
        return roomSongMapper.insert(roomSong);
    }

    public int addRoomSong(Integer roomId, Integer songId, Integer userId) {
        UserInfo userInfo = userService.getUserInfoById(userId);
        if(userInfo==null)
            return -1;
        RoomSong roomSong = new RoomSong(0, roomId, songId, userId, userInfo.getUsername());
        return roomSongMapper.insert(roomSong);
    }

    @Cacheable(value = "roomSongs", key = "#roomId")
    @Transactional
    public List<SongContent> getSongContentListInRoom(Integer roomId){
        List<RoomSong> target_RoomSongs = roomSongMapper.selectList(
                new QueryWrapper<RoomSong>()
                    .eq("room_id", roomId)
                    .eq("is_active", true)//用room_id查roomSong表的记录
        );
        if (target_RoomSongs.isEmpty())
            return Collections.emptyList();

        return convert_RoomSongList_to_SongContentList(target_RoomSongs);
    }

    @CacheEvict(value = "roomSongs", key = "#roomId")
    @Transactional
    public boolean removeSongInRoom(Integer roomId, Integer songId) {
        RoomSong target_RoomSong = roomSongMapper.selectOne(
                new QueryWrapper<RoomSong>()
                        .eq("room_id", roomId)
                        .eq("song_id", songId)
                        .eq("is_active", true)//用room_id查roomSong表的记录
        );
        if(target_RoomSong==null)
            return false;
        int i = roomSongMapper.update(
                null,
                new UpdateWrapper<RoomSong>()
                        .eq("room_id", roomId)
                        .eq("song_id", songId)
                        .eq("is_active", true)
                        .set("is_active", false)
        );
        return i > 0;
    }

    public List<SongContent> convert_RoomSongList_to_SongContentList(List<RoomSong> roomSongList) {
        List<SongContent> songContentList = new ArrayList<>();
        for (RoomSong rs : roomSongList) {
            Integer songId = rs.getSongId();
            Song song = songService.getSongById(songId);
            if(song.getIsActive()==0)//下架歌曲直接跳过
                continue;
            SongContent songContent = songService.getSongContentBySong(song);
            if(songContent!=null)
                songContentList.add(songContent);
        }
        return songContentList;
    }


    public List<SongContent> convert_PlaylistSongList_to_SongContentList(List<PlaylistSong> plSongList) {
        List<SongContent> songContentList = new ArrayList<>();
        for (PlaylistSong pls : plSongList) {
            Integer songId = pls.getSongId();
            Song song = songService.getSongById(songId);
            if(song.getIsActive()==0)//下架歌曲直接跳过
                continue;
            SongContent songContent = songService.getSongContentBySong(song);
            if(songContent!=null)
                songContentList.add(songContent);
        }
        return songContentList;
    }
}
