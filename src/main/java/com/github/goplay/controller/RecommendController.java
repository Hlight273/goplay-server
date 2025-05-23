package com.github.goplay.controller;

import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.Playlist;
import com.github.goplay.service.*;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import com.github.goplay.utils.Data.UserLevel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final UserService userService;
    private final PlaylistService playlistService;
    private final SongService songService;
    private final RecommendJobService recommendJobService;

    public RecommendController(RecommendService recommendService, UserService userService, PlaylistService playlistService, SongService songService, RecommendJobService recommendJobService) {
        this.recommendService = recommendService;
        this.userService = userService;
        this.playlistService = playlistService;
        this.songService = songService;
        this.recommendJobService = recommendJobService;
    }

    @PostMapping("/playlist/{playlistId}")
    public Result AddRecommend(@RequestHeader("token") String token, @PathVariable Integer playlistId) {
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        if (requester.getLevel() < UserLevel.MANAGER) {
            return Result.error().message("推荐歌单权限不足！");
        }
        Optional<Playlist> playlistOpt = Optional.ofNullable(playlistService.getPlaylistById(playlistId));
        if (playlistOpt.isEmpty()) {
            return Result.error().message("歌单不存在！");
        }
        if (recommendService.isRecommended(playlistId)) {
            return Result.error().message("该歌单已经推送过了！");
        }
        return recommendService.addRecommendPlaylist(playlistId)
                ? Result.ok().message("推送到主页成功！")
                : Result.error().message("推送失败！");
    }

    @DeleteMapping("/playlist/{playlistId}")
    public Result RemoveRecommend(@RequestHeader("token") String token, @PathVariable Integer playlistId) {
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        Playlist existingPlaylist = playlistService.getPlaylistById(playlistId);
        if (existingPlaylist == null) {
            return Result.error().message("歌单不存在！");
        }
        if (!(requester.getLevel() >= UserLevel.MANAGER)) {
            return Result.error().message("删除推荐歌单权限不足！");
        }
        if(recommendService.removeRecommendPlaylist(playlistId)){
            return Result.ok().message("下架推送成功！");
        }else{
            return Result.error().message("下架失败！");
        }
    }

    //这个是(站长)负责人推荐
    @GetMapping("/playlist/all")
    public Result GetRecommendPlaylists() {
        List<PlaylistInfo> playlistInfos = recommendService.getRecommendPlaylists();
        if(!(playlistInfos==null||playlistInfos.isEmpty())){
            return Result.ok().oData(playlistInfos).message("推荐歌单获取成功！");
        }else{
            return Result.empty().message("推荐歌单为空！");
        }
    }

    //自动推荐歌单
    @GetMapping("/playlist/auto/all")
    public Result getSystemRecommendPlaylists() {
        List<PlaylistInfo> recommended = recommendJobService.getCachedSysRecommendPlaylists(10); // 固定推荐前10个
        if (recommended.isEmpty()) {
            return Result.empty().message("推荐歌单为空！");
        }
        return Result.ok().oData(recommended).message("推荐歌单获取成功！");
    }

    //推荐热门歌曲（假设根据播放量，前10条）
    @GetMapping("/hot-songs")
    public Result getHotSongs() {
        List<SongContent> hotSongs = recommendJobService.getCachedHotSongs(10); // 获取前10条热门歌曲
        if (hotSongs.isEmpty()) {
            return Result.empty().message("暂无热门歌曲推荐！");
        }
        return Result.ok().oData(hotSongs).message("热门歌曲推荐获取成功！");
    }
}
