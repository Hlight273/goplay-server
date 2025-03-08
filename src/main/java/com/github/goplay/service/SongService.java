package com.github.goplay.service;

import com.github.goplay.dto.SongContent;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.Room;
import com.github.goplay.entity.Song;
import com.github.goplay.entity.SongInfo;
import com.github.goplay.mapper.PlaylistSongMapper;
import com.github.goplay.mapper.SongMapper;
import com.github.goplay.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.github.goplay.utils.FileUtils.getAudioFileNameByPath;
import static com.github.goplay.utils.FileUtils.getImgStrToBase64;

@Service
public class SongService {


    private final RoomSongService roomSongService;
    private final PlaylistSongService playlistSongService;
    private final SongMapper songMapper;
    private final SongInfoService songInfoService;

    public SongService(RoomSongService roomSongService, PlaylistSongService playlistSongService, SongMapper songMapper, SongInfoService songInfoService) {
        this.roomSongService = roomSongService;
        this.playlistSongService = playlistSongService;
        this.songMapper = songMapper;
        this.songInfoService = songInfoService;
    }

    //增
    @Transactional
    public SongContent addSong4Room(MultipartFile file, Room room, Integer userId, String path, String fileName) {
        Song song = insertSongByFileInfo(file,userId,path,fileName);
        if(song==null)
            return null;

        int cntRoomSong = roomSongService.addRoomSong(room.getId(), song.getId(), userId); //表room_song insert
        if (cntRoomSong <= 0)
            return null;

        return getSongContentBySong(song);
    }

    @Transactional
    public SongContent addSong4Playlist(MultipartFile file, Playlist playlist, Integer userId, String path, String fileName) {
        Song song = insertSongByFileInfo(file,userId,path,fileName);
        if(song==null)
            return null;

        int cntPlaylistSong = playlistSongService.addPlaylistSong(playlist.getId(), song.getId(), userId); //表playlist_song insert
        if (cntPlaylistSong <= 0)
            return null;
        return getSongContentBySong(song);
    }

    //查
    public Song getSongById(Integer id) {
        return songMapper.selectById(id);
    }

    public SongContent getSongContentBySong(Song song) {
        SongInfo songInfo = songInfoService.getSongInfoById(song.getId());
        return new SongContent(songInfo, getImgStrToBase64(song.getFileCoverPath()), getAudioFileNameByPath(song.getFilePath()));
    }

    //删
    public int removeSongById(Integer id) {
        Song song = songMapper.selectById(id);
        song.setIsActive(0);
        return songMapper.updateById(song);
    }


    /// 返回song
    private Song insertSongByFileInfo(MultipartFile file,Integer userId, String path, String fileName){
        File f = new File(path);

        Song song = new Song(0,fileName,path,
                FileUtils.getAudioDuration(f),
                (int)file.getSize(),
                file.getContentType(),
                FileUtils.getAudioCoverPath(f),userId);
        int cntSong = songMapper.insert(song); //表song insert
        if (cntSong <= 0)
            return null;

        int cntSongInfo = songInfoService.addSongInfo(song.getId(), f, FileUtils.getFileName(file)); //表song_info insert
        if (cntSongInfo <= 0)
            return null;
        return song;
    }
}
