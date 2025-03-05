package com.github.goplay.controller;

import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.entity.Playlist;
import com.github.goplay.service.PlaylistService;
import com.github.goplay.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
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

//    //查
//    @GetMapping("/{playlistId}/songs")
//    public Result SongContentListFromPlaylist(@PathVariable Integer playlistId){
//        List<SongContent> SongContentList = playlistService.getPublicSongContentList_by_playlistId(playlistId);
//        if(SongContentList != null){
//            return Result.ok()
//                    .oData(SongContentList)
//                    .message("查询成功");
//        }else {
//            return Result.empty()
//                    .message("查询为空！");
//        }
//    }
//
//    @GetMapping("/{playlistId}")
//    public Result Playlist(@PathVariable Integer playlistId){
//        Playlist playlist = playlistService.getPublicPlaylist_by_playlistId(playlistId);
//        if(playlist != null){
//            return Result.ok()
//                    .oData(playlist)
//                    .message("查询成功");
//        }else {
//            return Result.empty()
//                    .message("查询为空！");
//        }
//    }


}
