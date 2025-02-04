package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.dto.SongContent;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.RoomSong;
import com.github.goplay.entity.Song;
import com.github.goplay.entity.SongInfo;
import com.github.goplay.mapper.RoomSongMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RoomSongMapper roomSongMapper;
    @Autowired
    private UserService userService;

    private SongService songService;
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

    @Transactional
    public List<SongContent> getSongContentListInRoom(Integer roomId){
        List<RoomSong> target_RoomSongs = roomSongMapper.selectList(
                new QueryWrapper<RoomSong>()
                    .eq("room_id", roomId)
                    .eq("is_active", true)//用room_id查roomSong表的记录
        );
        if (target_RoomSongs.isEmpty())
            return Collections.emptyList();

        List<SongContent> songContentList = new ArrayList<>();
        for (RoomSong rs : target_RoomSongs) {
            Integer songId = rs.getSongId();
            Song song = songService.getSongById(songId);
            SongInfo songInfo = songInfoService.getSongInfoById(songId);
            SongContent songContent = new SongContent(songInfo, getImgStrToBase64(song.getFileCoverPath()), getAudioFileNameByPath(song.getFilePath()));
            songContentList.add(songContent);
        }
        return songContentList;
    }
}
