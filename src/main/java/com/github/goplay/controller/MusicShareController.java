package com.github.goplay.controller;

import com.github.goplay.dto.newDTO.MusicShareMessage;
import com.github.goplay.service.MusicShareService;
import com.github.goplay.service.SongService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class MusicShareController {

    private final MusicShareService service;
    private final SongService songService;
    private final UserService userService;

    @PostMapping("/send")
    public Result sendShare(@RequestHeader("token") String token,
                                  @RequestBody MusicShareMessage musicShareMessage) {
        Integer senderId = JwtUtils.getUserIdFromToken(token);
        if(!songService.isSongExist(musicShareMessage.getSongId()))
            return Result.error().message("歌曲不存在！");
        if(!userService.isUserExist(musicShareMessage.getReceiverId()))
            return Result.error().message("用户不存在！");
        service.sendShare(senderId, musicShareMessage);
        return Result.ok().message("success");
    }

    @PostMapping("/handle")
    public Result handleDecision(@RequestParam("shareId") Integer shareId, @RequestParam("store") boolean store) {
        service.handleUserDecision(shareId, store);
        return Result.ok().message("success");
    }

    @GetMapping("/my")
    public Result myShares(@RequestHeader("token") String token) {//给前端发用户所有的非DROPPED的Shares，至于PENDING还是STORED让前端自己表现
        Integer requesterId = JwtUtils.getUserIdFromToken(token);
        return Result.ok().message("success").oData(service.getMyActiveMessages(requesterId));
    }
}
