package com.github.goplay.controller;

import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.newDTO.PlaylistFormDTO;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.PlaylistSong;
import com.github.goplay.service.PlaylistService;
import com.github.goplay.service.PlaylistSongService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import com.github.goplay.utils.UserUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistSongService playlistSongService;
    private final UserService userService;

    public PlaylistController(PlaylistService playlistService, UserService userService, PlaylistSongService playlistSongService) {
        this.playlistService = playlistService;
        this.playlistSongService = playlistSongService;
        this.userService = userService;
    }

    @GetMapping("/{playlistId}/info")
    public Result PlaylistInfo(@PathVariable Integer playlistId){
        PlaylistInfo playlistInfo = playlistService.getPublicPlaylistInfo_by_playlistId(playlistId);
        if(playlistInfo != null){
            return Result.ok()
                    .oData(playlistInfo)
                    .message("查询歌单成功");
        }else {
            return Result.empty()
                    .message("查询歌单为空！");
        }
    }


    @Transactional
    @PostMapping
    public Result addPlaylist(@RequestHeader("token") String token, @RequestBody PlaylistFormDTO playlistForm){
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        int playlistIndex =  playlistService.addPlaylist(new Playlist(requestUserId, playlistForm.getTitle(), playlistForm.getDescription(), playlistForm.getCoverUrl(), playlistForm.getIsPublic()));
        Playlist targetPlaylist = playlistService.getPlaylistById(playlistIndex);
        if(playlistIndex>0){
            return Result.ok().oData(targetPlaylist).message("新建用户歌单成功！");
        }else {
            return Result.error().message("新建用户歌单失败！");
        }
    }

    @Transactional
    @PutMapping("/{playlistId}")
    public Result updatePlaylist(@RequestHeader("token") String token,
                                 @PathVariable Integer playlistId,
                                 @RequestBody PlaylistFormDTO playlistForm) {
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        Playlist existingPlaylist = playlistService.getPlaylistById(playlistId);
        if (existingPlaylist == null) {
            return Result.error().message("歌单不存在！");
        }
        if (!UserUtils.hasPlaylistPermission_by_userId(existingPlaylist, requester)) {
            return Result.error().message("无权限修改该歌单！");
        }
        existingPlaylist.setTitle(playlistForm.getTitle());
        existingPlaylist.setDescription(playlistForm.getDescription());
        existingPlaylist.setCoverUrl(playlistForm.getCoverUrl());
        existingPlaylist.setIsPublic(playlistForm.getIsPublic());

        boolean updated = playlistService.updatePlaylist(existingPlaylist)>-1;

        if (updated) {
            return Result.ok().oData(existingPlaylist).message("歌单更新成功！");
        } else {
            return Result.error().message("歌单更新失败！");
        }
    }

    @Transactional
    @DeleteMapping("/{playlistId}")
    public Result removePlaylist(@RequestHeader("token") String token, @PathVariable Integer playlistId){
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        Playlist existingPlaylist = playlistService.getPlaylistById(playlistId);
        if (existingPlaylist == null) {
            return Result.error().message("歌单不存在！");
        }
        if (!UserUtils.hasPlaylistPermission_by_userId(existingPlaylist, requester)) {
            return Result.error().message("无权限删除该歌单！");
        }
        boolean success = playlistService.removePlaylist(playlistId)>-1;
        if(success){
            return Result.ok().oData(true).message("删除歌单成功！");
        }else {
            return Result.error().oData(false).message("删除歌单失败！");
        }
    }

    //去掉播放列表某个歌曲，但是不会删除歌曲内容
    @DeleteMapping("/{playlistId}/song/{songId}")
    public Result removeSongInPlaylist(@RequestHeader("token") String token, @PathVariable Integer playlistId, @PathVariable Integer songId){
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        Playlist existingPlaylist = playlistService.getPlaylistById(playlistId);
        if (existingPlaylist == null) {
            return Result.error().message("歌单不存在！");
        }
        if (!UserUtils.hasPlaylistPermission_by_userId(existingPlaylist, requester)) {
            return Result.error().message("无权限删除该歌单！");
        }
        int targetIndex = playlistSongService.removePlaylistSong(playlistId, songId);
        if(targetIndex>=0){
            return Result.ok().oData(true).message("去除记录成功！");
        }else {
            return Result.error().oData(false).message("去除记录失败！");
        }
    }
}
