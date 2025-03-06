package com.github.goplay.service;

import com.github.goplay.entity.SongInfo;
import com.github.goplay.mapper.SongInfoMapper;
import com.github.goplay.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.github.goplay.utils.FileUtils.fillEmptyNamesForSongInfo;

@Service
public class SongInfoService {


    private final SongInfoMapper songInfoMapper;

    public SongInfoService(SongInfoMapper songInfoMapper) {
        this.songInfoMapper = songInfoMapper;
    }

    public int addSongInfo(SongInfo songInfo) {
        return songInfoMapper.insert(songInfo);
    }

    public int addSongInfo(Integer songId, File audioFile) {
        SongInfo songInfo = new SongInfo(songId, FileUtils.getAudioName(audioFile),
                FileUtils.getAudioArtist(audioFile),
                FileUtils.getAudioDuration(audioFile),
                FileUtils.getAudioAlbum(audioFile),
                FileUtils.getAudioSize(audioFile));
        fillEmptyNamesForSongInfo(songInfo);
        return songInfoMapper.insert(songInfo);
    }

    public SongInfo getSongInfoById(Integer id) {
        return songInfoMapper.selectById(id);
    }
}
