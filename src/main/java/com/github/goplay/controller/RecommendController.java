package com.github.goplay.controller;

import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.UserId;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.RecommendPlaylist;
import com.github.goplay.entity.Room;
import com.github.goplay.service.PlaylistService;
import com.github.goplay.service.RecommendPlaylistService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import com.github.goplay.utils.UserLevel;
import com.github.goplay.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendPlaylistService recommendPlaylistService;
    private final UserService userService;
    private final PlaylistService playlistService;

    public RecommendController(RecommendPlaylistService recommendPlaylistService, UserService userService, PlaylistService playlistService) {
        this.recommendPlaylistService = recommendPlaylistService;
        this.userService = userService;
        this.playlistService = playlistService;
    }

    @PostMapping("/playlist/{playlistId}")
    public Result AddRecommend(@RequestHeader("token") String token, @PathVariable Integer playlistId) {
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        Playlist existingPlaylist = playlistService.getPlaylistById(playlistId);
        if (existingPlaylist == null) {
            return Result.error().message("歌单不存在！");
        }
        if (!(requester.getLevel() >= UserLevel.MANAGER)) {
            return Result.error().message("推荐歌单权限不足！");
        }
        if(recommendPlaylistService.isRecommended(playlistId)){
            return Result.error().message("该歌单已经推送过了！");
        }
        if(recommendPlaylistService.addRecommendPlaylist(playlistId)){
            return Result.ok().message("推送到主页成功！");
        }else{
            return Result.error().message("推送失败！");
        }
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
        if(recommendPlaylistService.removeRecommendPlaylist(playlistId)){
            return Result.ok().message("下架推送成功！");
        }else{
            return Result.error().message("下架失败！");
        }
    }

    @GetMapping("/playlist/all")
    public Result GetRecommendPlaylists() {
        List<PlaylistInfo> playlistInfos = recommendPlaylistService.getRecommendPlaylists();
        if(!(playlistInfos==null||playlistInfos.isEmpty())){
            return Result.ok().oData(playlistInfos).message("推荐歌单获取成功！");
        }else{
            return Result.empty().message("推荐歌单为空！");
        }
    }
}
