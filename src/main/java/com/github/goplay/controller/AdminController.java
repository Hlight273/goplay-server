package com.github.goplay.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.newDTO.SongDetailDTO;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.Song;
import com.github.goplay.entity.User;
import com.github.goplay.service.AdminService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import com.github.goplay.utils.UserLevel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在AdminInterceptor中会拦截非管理员请求
 */
@RestController
@RequestMapping("/admin")
public class AdminController {


    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    /**
     * 搜索歌单（分页）
     */
    @GetMapping("/playlistInfos/search")
    public Result searchPlaylists(@RequestParam(required = false) String keyword,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize) {
        IPage<PlaylistInfo> playlistInfos = adminService.searchPlaylistInfos(keyword, page, pageSize);
        if(playlistInfos.getTotal()>0){
            return Result.ok().data("playlistInfos", playlistInfos.getRecords()).data("total", playlistInfos.getTotal());
        }
        return Result.empty();
    }

    /**
     * 搜索歌曲（分页）
     */
    @GetMapping("/songs/search")
    public Result searchSongs(@RequestParam(required = false) String keyword,
                              @RequestParam Integer page,
                              @RequestParam Integer pageSize) {
        IPage<SongDetailDTO> songDetails = adminService.searchSongDetails(keyword, page, pageSize);
        return Result.ok().data("songs", songDetails.getRecords()).data("total", songDetails.getTotal());
    }

    /**
     * 搜索用户（分页）
     */
    @GetMapping("/users/search")
    public Result searchUsers(@RequestParam(required = false) String keyword,
                              @RequestParam Integer page,
                              @RequestParam Integer pageSize) {
        IPage<UserInfo> users = adminService.searchUsers(keyword, page, pageSize);
        return Result.ok().data("users", users.getRecords()).data("total", users.getTotal());
    }

    /**
     * 下架歌单
     */
    @PostMapping("/playlist/{playlistId}/deactivate")
    public Result deactivatePlaylist(@PathVariable Integer playlistId) {
        if (!adminService.deactivatePlaylist(playlistId)) {
            return Result.error().message("歌单下架失败！");
        }
        return Result.ok().message("歌单下架成功！");
    }

    /**
     * 上架歌单
     */
    @PostMapping("/playlist/{playlistId}/activate")
    public Result activatePlaylist(@PathVariable Integer playlistId) {
        if (!adminService.activatePlaylist(playlistId)) {
            return Result.error().message("歌单上架失败！");
        }
        return Result.ok().message("歌单上架成功！");
    }

    /**
     * 下架歌曲
     */
    @PostMapping("/song/{songId}/deactivate")
    public Result deactivateSong(@PathVariable Integer songId) {
        if (!adminService.deactivateSong(songId)) {
            return Result.error().message("歌曲下架失败！");
        }
        return Result.ok().message("歌曲下架成功！");
    }

    /**
     * 上架歌曲
     */
    @PostMapping("/song/{songId}/activate")
    public Result activateSong(@PathVariable Integer songId) {
        if (!adminService.activateSong(songId)) {
            return Result.error().message("歌曲上架失败！");
        }
        return Result.ok().message("歌曲上架成功！");
    }

    /**
     * 下架歌曲
     */
    @PostMapping("/user/{userId}/deactivate")
    public Result deactivateUser(@PathVariable Integer userId) {
        UserInfo userInfo = userService.getUserInfoById(userId);
        if(userInfo.getLevel()> UserLevel.NORMAL){
            return Result.error().message("用户权限高于普通用户，封禁失败！");
        }
        if (!adminService.deactivateUser(userId)) {
            return Result.error().message("用户封禁失败！");
        }
        return Result.ok().message("用户封禁成功！");
    }

    /**
     * 上架歌曲
     */
    @PostMapping("/user/{userId}/activate")
    public Result activateUser(@PathVariable Integer userId) {
        if (!adminService.activateUser(userId)) {
            return Result.error().message("用户解禁失败！");
        }
        return Result.ok().message("用户解禁成功！");
    }

    /**
     * 更新歌单信息
     */
    @PutMapping("/playlist/{playlistId}")
    public Result updatePlaylist(@PathVariable Integer playlistId,
                                 @RequestBody Playlist updatedPlaylist) {
        if (!adminService.updatePlaylist(playlistId, updatedPlaylist)) {
            return Result.error().message("歌单更新失败！");
        }
        return Result.ok().message("歌单更新成功！");
    }

    /**
     * 更新歌曲信息
     */
    @PutMapping("/song/{songId}/info")
    public Result updateSong(@RequestHeader("token") String token,
                             @PathVariable Integer songId,
                             @RequestBody Song updatedSong) {
        if (!adminService.updateSong(songId, updatedSong)) {
            return Result.error().message("歌曲更新失败！");
        }
        return Result.ok().message("歌曲更新成功！");
    }
}

