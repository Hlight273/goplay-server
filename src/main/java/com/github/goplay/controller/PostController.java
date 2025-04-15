package com.github.goplay.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.goplay.dto.VO.PostVO;
import com.github.goplay.dto.newDTO.PostCommentDTO;
import com.github.goplay.dto.newDTO.PostDTO;
import com.github.goplay.service.PostService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/village/post")
public class PostController {


    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** 发布动态 */
    @PostMapping
    public Result createPost(@RequestHeader("token") String token, @RequestBody PostDTO postDTO) {
        Integer userId = JwtUtils.getUserIdFromToken(token);
        boolean success = postService.createPost(userId, postDTO);
        return success ? Result.ok().message("发布成功！") : Result.error().message("发布失败！");
    }

    /** 获取所有动态（分页） */
    @GetMapping
    public Result getPostList(@RequestParam Integer page, @RequestParam Integer pageSize) {
        IPage<PostVO> postPage = postService.getPostList(page, pageSize);
        return Result.ok().data("posts", postPage.getRecords()).data("total", postPage.getTotal());
    }

    /** 获取某用户的动态（分页） */
    @GetMapping("{userId}")
    public Result getPostListByUserId(@PathVariable Integer userId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        IPage<PostVO> postPage = postService.getPostsByUserId(userId, page, pageSize);
        return Result.ok().data("posts", postPage.getRecords()).data("total", postPage.getTotal());
    }

    /** 获取某个动态的评论（分页） */
    @GetMapping("/comment/{postId}")
    public Result getPostComments(
            @PathVariable Integer postId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        IPage<PostCommentDTO> commentPage = postService.getCommentsByPost(postId, page, pageSize);
        return Result.ok().data("comments", commentPage.getRecords()).data("total", commentPage.getTotal());
    }

    /** 获取某条评论的回复（分页） */
    @GetMapping("/comment/replies/{commentId}")
    public Result getCommentReplies(
            @PathVariable Integer commentId,
            @RequestParam Integer page,
            @RequestParam Integer pageSize) {
        IPage<PostCommentDTO> replies = postService.getRepliesByCommentId(commentId, page, pageSize);
        return Result.ok().data("replies", replies.getRecords()).data("total", replies.getTotal());
    }


    /** 提交评论或回复 */
    @PostMapping("/comment")
    public Result addPostComment(@RequestHeader("token") String token, @RequestBody PostCommentDTO comment) {
        Integer userId = JwtUtils.getUserIdFromToken(token);
        comment.setAddedBy(userId);
        boolean success = postService.addComment(comment);
        return success ? Result.ok().message("评论成功！") : Result.error().message("评论失败！");
    }

    /** 点赞/取消点赞 */
    @PostMapping("/like/{postId}")
    public Result toggleLike(@RequestHeader("token") String token, @PathVariable Integer postId) {
        Integer userId = JwtUtils.getUserIdFromToken(token);
        boolean success = postService.toggleLike(postId, userId);
        return Result.ok().message(success ? "操作成功！" : "操作失败！");
    }
}

