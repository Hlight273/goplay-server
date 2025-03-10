package com.github.goplay.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.goplay.dto.newDTO.SongCommentDTO;
import com.github.goplay.service.SongCommentService;
import com.github.goplay.service.SongService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class SongCommentController {


    private final SongCommentService songCommentService;
    private final SongService songService;

    public SongCommentController(SongCommentService songCommentService, SongService songService) {
        this.songCommentService = songCommentService;
        this.songService = songService;
    }

    /** 获取歌曲的一级评论（分页） */
    @GetMapping("/song/{songId}")
    public Result getCommentsBySong(
            @PathVariable Integer songId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {

        IPage<SongCommentDTO> commentsPage = songCommentService.getCommentsBySong(songId, page, pageSize);
        return Result.ok().data("comments", commentsPage.getRecords()).data("total", commentsPage.getTotal());
    }

    /** 获取某个评论的二级评论（分页） */
    @GetMapping("/replies/{commentId}")
    public Result getRepliesByComment(
            @PathVariable Integer commentId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {

        IPage<SongCommentDTO> repliesPage = songCommentService.getRepliesByComment(commentId, page, pageSize);
        return Result.ok().data("replies", repliesPage.getRecords()).data("total", repliesPage.getTotal());
    }

    /** 提交新评论（支持一级评论和二级评论） */
    @PostMapping
    public Result addComment(@RequestHeader("token") String token, @RequestBody SongCommentDTO comment) {
        Integer userId = JwtUtils.getUserIdFromToken(token);
        comment.setAddedBy(userId);
        if(songService.getSongById(comment.getSongId()) == null) {
            return Result.error().message("歌曲不存在！");
        }
        boolean success = songCommentService.addComment(comment);
        return success ? Result.ok().message("评论成功！") : Result.error().message("评论失败！");
    }
}
