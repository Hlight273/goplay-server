package com.github.goplay.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.newDTO.PlaylistFormDTO;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.PlaylistSong;
import com.github.goplay.service.PlaylistService;
import com.github.goplay.service.PlaylistSongService;
import com.github.goplay.service.SongService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import com.github.goplay.utils.UserLevel;
import com.github.goplay.utils.UserUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistSongService playlistSongService;
    private final UserService userService;
    private final SongService songService;

    public PlaylistController(PlaylistService playlistService, UserService userService, PlaylistSongService playlistSongService, SongService songService) {
        this.playlistService = playlistService;
        this.playlistSongService = playlistSongService;
        this.userService = userService;
        this.songService = songService;
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
        if(playlistForm.getTitle().isEmpty())
            return Result.error().message("歌单标题不能为空！");
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
        Result preCheck = preCheckModifyPlaylist(token, playlistId);
        if(preCheck!=null)
            return preCheck;
        if(playlistForm.getTitle().isEmpty())
            return Result.error().message("歌单标题不能为空！");
        Playlist existingPlaylist = playlistService.getPlaylistById(playlistId);
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
        Result preCheck = preCheckModifyPlaylist(token, playlistId);
        if(preCheck!=null)
            return preCheck;
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
        Result preCheck = preCheckModifyPlaylist(token, playlistId);
        if(preCheck!=null)
            return preCheck;
        int targetIndex = playlistSongService.removePlaylistSong(playlistId, songId);
        if(targetIndex>=0){
            return Result.ok().oData(true).message("歌单中删除歌曲成功！");
        }else {
            return Result.error().oData(false).message("删除失败！");
        }
    }

    @Transactional
    @PostMapping("/{playlistId}/song/{songId}")
    public Result addSongInPlaylist(@RequestHeader("token") String token, @PathVariable Integer playlistId, @PathVariable Integer songId){
        Result preCheck = preCheckModifyPlaylist(token, playlistId);
        if(preCheck!=null)
            return preCheck;
        if(songService.getSongById(songId)==null){
            return Result.error().message("歌曲不存在！");
        }
        if(playlistSongService.isExistPlaylistSong(playlistId, songId)){
            return Result.error().message("歌曲已存在该歌单！");
        }
        int targetIndex = playlistSongService.addPlaylistSong(playlistId, songId, JwtUtils.getUserIdFromToken(token));
        if(targetIndex>=0){
            return Result.ok().oData(true).message("添加成功！");
        }else {
            return Result.error().oData(false).message("去除记录失败！");
        }
    }

    @GetMapping("/search")
    public Result searchPlaylists(@RequestHeader("token") String token,
                                  @RequestParam String keyword,
                                  @RequestParam int page,
                                  @RequestParam int size) {
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        boolean canSearchPrivate = requester.getLevel()>= UserLevel.MANAGER;
        Page<PlaylistInfo> resultPage = playlistService.searchPlaylists(keyword, page, size, canSearchPrivate, requestUserId);
        return resultPage.getRecords().isEmpty()
                ? Result.empty().message("未找到相关歌单！")
                : Result.ok().oData(resultPage.getRecords()).message("搜索成功！").data("total", resultPage.getTotal());
    }


    ///预检查 用户是否有权限增删改该歌单，以及歌单是否存在
    private Result preCheckModifyPlaylist(String token, Integer playlistId){
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requestUserId);
        Playlist existingPlaylist = playlistService.getPlaylistById(playlistId);
        if (existingPlaylist == null) {
            return Result.error().message("歌单不存在！");
        }
        if (!UserUtils.hasPlaylistPermission_by_userId(existingPlaylist, requester)) {
            return Result.error().message("无权限修改该歌单！");
        }
        return null;
    }
}
