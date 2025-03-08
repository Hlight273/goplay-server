package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.RecommendPlaylist;
import com.github.goplay.mapper.PlaylistMapper;
import com.github.goplay.mapper.RecommendPlaylistMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendPlaylistService {

    private final RecommendPlaylistMapper recommendPlaylistMapper;
    private final PlaylistMapper playlistMapper;
    private final PlaylistService playlistService;

    public RecommendPlaylistService(RecommendPlaylistMapper recommendPlaylistMapper, PlaylistMapper playlistMapper, PlaylistService playlistService) {
        this.recommendPlaylistMapper = recommendPlaylistMapper;
        this.playlistMapper = playlistMapper;
        this.playlistService = playlistService;
    }

    public List<PlaylistInfo> getRecommendPlaylists() {
        LambdaQueryWrapper<RecommendPlaylist> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RecommendPlaylist::getId)
                .eq(RecommendPlaylist::getIsActive, 1);
        List<Integer> playlistIds = recommendPlaylistMapper.selectList(wrapper).stream().map(RecommendPlaylist::getPlaylistId).toList();
        return playlistService.get_PlaylistInfoList_ByPlaylistIds(playlistIds);
    }

    public boolean addRecommendPlaylist(Integer playlistId) {
        RecommendPlaylist recommendPlaylist = new RecommendPlaylist(0 , playlistId);
        return recommendPlaylistMapper.insert(recommendPlaylist)>-1;
    }

    public boolean removeRecommendPlaylist(Integer playlistId) {
        RecommendPlaylist recommendPlaylist = getRecommendPlaylist(playlistId);
        if(recommendPlaylist == null) {
            return false;
        }
        recommendPlaylist.setIsActive(0);
        return recommendPlaylistMapper.updateById(recommendPlaylist)>-1;
    }

    public boolean isRecommended(Integer playlistId) {
        RecommendPlaylist recommendPlaylist = getRecommendPlaylist(playlistId);
        if(recommendPlaylist == null) {
            return false;
        }
        return recommendPlaylist.getIsActive() == 1;
    }

    private RecommendPlaylist getRecommendPlaylist(Integer playlistId) {
        LambdaQueryWrapper<RecommendPlaylist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendPlaylist::getPlaylistId, playlistId)
                .eq(RecommendPlaylist::getIsActive, 1);
        RecommendPlaylist recommendPlaylist = recommendPlaylistMapper.selectOne(wrapper);
        return recommendPlaylist;
    }
}
