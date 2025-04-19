package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.goplay.dto.SongContent;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.VO.PostVO;
import com.github.goplay.dto.newDTO.PostCommentDTO;
import com.github.goplay.dto.newDTO.PostDTO;
import com.github.goplay.entity.*;
import com.github.goplay.mapper.*;
import com.github.goplay.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostService {


    private final PostMapper postMapper;
    private final PostImageMapper postImageMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostCommentMapper postCommentMapper;
    private final UserMapper userMapper;
    private final SongService songService;
    private final SongCommentMapper songCommentMapper;
    private final UserService userService;

    public PostService(PostMapper postMapper, PostImageMapper postImageMapper, PostLikeMapper postLikeMapper, PostCommentMapper postCommentMapper, UserMapper userMapper, SongService songService, SongCommentMapper songCommentMapper, UserService userService) {
        this.postMapper = postMapper;
        this.postImageMapper = postImageMapper;
        this.postLikeMapper = postLikeMapper;
        this.postCommentMapper = postCommentMapper;
        this.userMapper = userMapper;
        this.songService = songService;
        this.songCommentMapper = songCommentMapper;
        this.userService = userService;
    }

    /** 创建动态 */
    public boolean createPost(Integer userId, PostDTO dto) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(dto.getContentText());
        post.setSongId(dto.getSongId());
        post.setLinkUrl(dto.getLinkUrl());
        post.setIsActive(1);
        boolean inserted = postMapper.insert(post) > 0;
        if (inserted && dto.getImageUrls() != null) {
            for (String url : dto.getImageUrls()) {
                PostImage img = new PostImage();
                img.setPostId(post.getId());
                img.setImageUrl(url);
                img.setIsActive(1);
                postImageMapper.insert(img);
            }
        }
        return inserted;

    }

    /** 获取动态列表 */
    public IPage<PostVO> getPostList(Integer page, Integer pageSize) {
        Page<Post> postPage = new Page<>(page, pageSize);
        IPage<Post> pageResult = postMapper.selectPage(postPage,
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getIsActive, 1)
                        .orderByDesc(Post::getCreateTime));

        List<PostVO> postVOList = postResultToVOList(pageResult);
        return new Page<PostVO>(page, pageSize, pageResult.getTotal()).setRecords(postVOList);
    }

    /** 查询某用户的所有贴文（分页） */
    public IPage<PostVO> getPostsByUserId(Integer userId, Integer page, Integer pageSize) {
        Page<Post> postPage = new Page<>(page, pageSize);
        IPage<Post> pageResult = postMapper.selectPage(postPage,
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getIsActive, 1)
                        .eq(Post::getUserId, userId)
                        .orderByDesc(Post::getCreateTime));

        List<PostVO> postVOList = postResultToVOList(pageResult);
        System.out.println();
        return new Page<PostVO>(page, pageSize, pageResult.getTotal()).setRecords(postVOList);
    }

    /** 获取动态评论 */
    public IPage<PostCommentDTO> getCommentsByPost(Integer postId, Integer page, Integer pageSize) {
        Page<PostComment> commentPage = new Page<>(page, pageSize);
        IPage<PostComment> pageResult = postCommentMapper.selectPage(commentPage,
                new LambdaQueryWrapper<PostComment>()
                        .eq(PostComment::getPostId, postId)
                        .isNull(PostComment::getParentId)
                        .eq(PostComment::getIsActive, 1)
                        .orderByDesc(PostComment::getAddedAt));
        return getPostCommentDTOIPage(page, pageSize, pageResult);
    }

    /** 获取二级评论（回复） */
    public IPage<PostCommentDTO> getRepliesByCommentId(Integer commentId, Integer page, Integer pageSize) {
        Page<PostComment> commentPage = new Page<>(page, pageSize);
        IPage<PostComment> pageResult = postCommentMapper.selectPage(commentPage,
                new LambdaQueryWrapper<PostComment>()
                        .eq(PostComment::getParentId, commentId)
                        .eq(PostComment::getIsActive, 1)
                        .orderByAsc(PostComment::getAddedAt));

        return getPostCommentDTOIPage(page, pageSize, pageResult);
    }

    private IPage<PostCommentDTO> getPostCommentDTOIPage(Integer page, Integer pageSize, IPage<PostComment> pageResult) {
        List<PostCommentDTO> dtoList = pageResult.getRecords().stream().map(comment -> {
            PostCommentDTO dto = new PostCommentDTO();
            dto.setId(comment.getId());
            dto.setParentId(comment.getParentId());
            dto.setPostId(comment.getPostId());
            dto.setAddedBy(comment.getAddedBy());
            UserInfo userInfo = userService.getUserInfoById(comment.getAddedBy());//dto还需要用户名填充
            if(userInfo!=null){
                dto.setAddedByName(userInfo.getNickname());
                dto.setAddedByAvatar(UserUtils.getAvatar());
            }
            dto.setAddedAt(comment.getAddedAt());
            dto.setContentText(comment.getContentText());
            dto.setIsActive(comment.getIsActive());

            // 计算该评论的二级评论总数
            Integer totalReplies = Math.toIntExact(postCommentMapper.selectCount(
                    new LambdaQueryWrapper<PostComment>().eq(PostComment::getParentId, comment.getId())
            ));
            dto.setTotalReplies(totalReplies);
            dto.setLoadedRepliesCount(0); // 初始加载时未加载任何二级评论

            //找到父级被评论者信息
            PostComment parentComment = postCommentMapper.selectById(comment.getParentId());
            if(parentComment!=null){
                UserInfo parentUserInfo = userService.getUserInfoById(parentComment.getAddedBy());
                dto.setReplyToName(parentUserInfo.getNickname());
                dto.setReplyToAvator(UserUtils.getAvatar());
            }
            return dto;
        }).collect(Collectors.toList());

        Page<PostCommentDTO> result = new Page<>(page, pageSize, pageResult.getTotal());
        result.setRecords(dtoList);
        return result;
    }


    /** 添加评论 */
    public boolean addComment(PostCommentDTO dto) {
        PostComment comment = new PostComment();
        comment.setPostId(dto.getPostId());
        comment.setParentId(dto.getParentId());
        comment.setAddedBy(dto.getAddedBy());
        comment.setContentText(dto.getContentText());
        comment.setIsActive(1);
        return postCommentMapper.insert(comment) > 0;
    }

    /** 点赞或取消点赞 */
    public boolean toggleLike(Integer postId, Integer userId) {
        PostLike exist = postLikeMapper.selectOne(
                new LambdaQueryWrapper<PostLike>()
                        .eq(PostLike::getPostId, postId)
                        .eq(PostLike::getUserId, userId));
        if (exist != null) {
            postLikeMapper.deleteById(exist.getId());
            return false;
        } else {
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            postLikeMapper.insert(like);
            return true;
        }
    }



    private List<PostVO> postResultToVOList(IPage<Post> pageResult) {
        return pageResult.getRecords().stream()
                .map(this::convertToPostVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private PostVO convertToPostVO(Post post) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setAddedBy(post.getUserId());
        vo.setContentText(post.getContent());
        vo.setLinkUrl(post.getLinkUrl());
        vo.setAddedAt(post.getCreateTime());

        if (post.getSongId() != null) {
            Song song = songService.getSongById(post.getSongId());
            if (song == null) return null;
            SongContent songContent = songService.getSongContentBySong(song);
            if (song != null && songContent == null) return null;
            vo.setSongContent(songContent);
        }

        vo.setImageUrls(postImageMapper.selectList(
                        new LambdaQueryWrapper<PostImage>()
                                .eq(PostImage::getPostId, post.getId())
                                .eq(PostImage::getIsActive, 1))
                .stream().map(PostImage::getImageUrl).collect(Collectors.toList()));

        vo.setLikeCount(Math.toIntExact(postLikeMapper.selectCount(
                new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, post.getId()))));
        vo.setCommentCount(Math.toIntExact(postCommentMapper.selectCount(
                new LambdaQueryWrapper<PostComment>().eq(PostComment::getPostId, post.getId()))));
        vo.setLikedByCurrentUser(false); //默认不展示

        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            vo.setAddedByName(user.getNickname());
            vo.setAddedByAvatar(UserUtils.getAvatar());
        }

        return vo;
    }
}
