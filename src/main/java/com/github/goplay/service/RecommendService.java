package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.RecommendPlaylist;
import com.github.goplay.entity.Song;
import com.github.goplay.mapper.PlaylistMapper;
import com.github.goplay.mapper.RecommendMapper;
import com.github.goplay.mapper.SongMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    private final RecommendMapper recommendMapper;
    private final PlaylistMapper playlistMapper;
    private final PlaylistService playlistService;
    private final SongMapper songMapper;
    private final SongService songService;

    public RecommendService(RecommendMapper recommendMapper, PlaylistMapper playlistMapper, PlaylistService playlistService, SongMapper songMapper, SongService songService) {
        this.recommendMapper = recommendMapper;
        this.playlistMapper = playlistMapper;
        this.playlistService = playlistService;
        this.songMapper = songMapper;
        this.songService = songService;
    }

    public List<PlaylistInfo> getRecommendPlaylists() {
        LambdaQueryWrapper<RecommendPlaylist> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RecommendPlaylist::getId)
                .eq(RecommendPlaylist::getIsActive, 1);
        List<Integer> playlistIds = recommendMapper.selectList(wrapper).stream().map(RecommendPlaylist::getPlaylistId).toList();
        return playlistService.get_PlaylistInfoList_ByPlaylistIds(playlistIds);
    }

    public boolean addRecommendPlaylist(Integer playlistId) {
        RecommendPlaylist recommendPlaylist = new RecommendPlaylist(0 , playlistId);
        return recommendMapper.insert(recommendPlaylist)>-1;
    }

    @CacheEvict(value = "recommendPlaylists_push", key = "#playlistId")
    public boolean removeRecommendPlaylist(Integer playlistId) {
        RecommendPlaylist recommendPlaylist = getRecommendPlaylist(playlistId);
        if(recommendPlaylist == null) {
            return false;
        }
        recommendPlaylist.setIsActive(0);
        return recommendMapper.updateById(recommendPlaylist)>-1;
    }

    public boolean isRecommended(Integer playlistId) {
        RecommendPlaylist recommendPlaylist = getRecommendPlaylist(playlistId);
        if(recommendPlaylist == null) {
            return false;
        }
        return recommendPlaylist.getIsActive() == 1;
    }

    @Cacheable(value = "recommendPlaylists_push", key = "#playlistId")
    public RecommendPlaylist getRecommendPlaylist(Integer playlistId) {
        LambdaQueryWrapper<RecommendPlaylist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendPlaylist::getPlaylistId, playlistId)
                .eq(RecommendPlaylist::getIsActive, 1);
        RecommendPlaylist recommendPlaylist = recommendMapper.selectOne(wrapper);
        return recommendPlaylist;
    }


    // 获取推荐歌单（假设为最新的10个公开且激活的歌单）
    //@Cacheable(value = "recommendPlaylists", key = "#limit")
    public List<PlaylistInfo> getSystemRecommendPlaylists(int limit) {
        Page<Playlist> page = new Page<>(1, limit);
        LambdaQueryWrapper<Playlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Playlist::getIsActive, 1)
                .eq(Playlist::getIsPublic, 1)
                .orderByDesc(Playlist::getAddedAt);

        playlistMapper.selectPage(page, wrapper);

        return playlistService.convertToPlaylistInfo(page.getRecords());
    }

    // 获取热门歌曲（假设根据播放量，前10条）
    //@Cacheable(value = "hotSongs", key = "#limit")
    public List<SongContent> getHotSongs(int limit) {
        Page<Song> page = new Page<>(1, limit);
        LambdaQueryWrapper<Song> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Song::getIsActive, 1)
                .orderByDesc(Song::getPlayCount);

        songMapper.selectPage(page, wrapper);

        return page.getRecords().stream()
                .map(songService::getSongContentBySong)
                .collect(Collectors.toList());
    }
}
