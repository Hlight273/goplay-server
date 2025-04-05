package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.dto.newDTO.SongDetailDTO;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.RecommendPlaylist;
import com.github.goplay.entity.Song;
import com.github.goplay.mapper.PlaylistMapper;
import com.github.goplay.mapper.RecommendMapper;
import com.github.goplay.mapper.SongMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    private RecommendPlaylist getRecommendPlaylist(Integer playlistId) {
        LambdaQueryWrapper<RecommendPlaylist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendPlaylist::getPlaylistId, playlistId)
                .eq(RecommendPlaylist::getIsActive, 1);
        RecommendPlaylist recommendPlaylist = recommendMapper.selectOne(wrapper);
        return recommendPlaylist;
    }


    // 获取推荐歌单（假设为最新的10个公开且激活的歌单）
    public List<PlaylistInfo> getSystemRecommendPlaylists(int limit) {
        LambdaQueryWrapper<Playlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Playlist::getIsActive, 1) // 确保是激活的
                .eq(Playlist::getIsPublic, 1) // 确保是公开的
                .orderByDesc(Playlist::getAddedAt) // 按添加时间降序排列
                .last("LIMIT " + limit); // 限制结果数量

        List<Playlist> playlists = playlistMapper.selectList(wrapper);

        // 将 Playlist 转换为 PlaylistInfo
        return playlistService.convertToPlaylistInfo(playlists);
    }

    // 获取热门歌曲（假设根据播放量，前10条）
    public List<SongContent> getHotSongs(int limit) {
        LambdaQueryWrapper<Song> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Song::getIsActive, 1) // 确保是激活的歌曲
                .orderByDesc(Song::getPlayCount) // 按播放量降序排列
                .last("LIMIT " + limit); // 限制结果数量

        List<Song> songs = songMapper.selectList(wrapper);

        return songs.stream().map(song -> {
            SongContent dto = songService.getSongContentBySong(song);
            return dto;
        }).toList();
    }


}
