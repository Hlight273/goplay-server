package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.entity.Room;
import com.github.goplay.entity.RoomUser;
import com.github.goplay.entity.Song;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.RoomMapper;
import com.github.goplay.mapper.SongMapper;
import com.github.goplay.mapper.UserMapper;
import com.github.goplay.utils.FileUtils;
import com.github.goplay.utils.PrivilegeCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

@Service
public class SongService {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private SongInfoService songInfoService;

    private RoomSongService roomSongService;
    @Autowired
    public void setRoomSongService(RoomSongService roomSongService){
        this.roomSongService = roomSongService;
    }


    @Transactional
    public int addSong(MultipartFile file, Room room, Integer userId, String path, String fileName) {
        File f = new File(path);

        Song song = new Song(0,fileName,path,
                FileUtils.getAudioDuration(f),
                (int)file.getSize(),
                file.getContentType(),
                FileUtils.getAudioCoverPath(f),userId);
        int cntSong = songMapper.insert(song); //表song insert
        if (cntSong <= 0)
            return -1;

        int cntSongInfo = songInfoService.addSongInfo(song.getId(), f); //表song_info insert
        if (cntSongInfo <= 0)
            return -1;

        int cntRoomSong = roomSongService.addRoomSong(room.getId(), song.getId(), userId); //表room_song insert
        if (cntRoomSong <= 0)
            return -1;

        return 1;
    }

    public Song getSongById(Integer id) {
        return songMapper.selectById(id);
    }
}
