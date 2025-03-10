package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.goplay.dto.newDTO.SongCommentDTO;
import com.github.goplay.entity.SongComment;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.SongCommentMapper;
import com.github.goplay.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongCommentService {


    private final SongCommentMapper songCommentMapper;
    private final UserMapper userMapper;

    public SongCommentService(SongCommentMapper songCommentMapper, UserMapper userMapper) {
        this.songCommentMapper = songCommentMapper;
        this.userMapper = userMapper;
    }

    /**
     * 获取歌曲的一级评论（分页）
     */
    public IPage<SongCommentDTO> getCommentsBySong(Integer songId, Integer page, Integer pageSize) {
        Page<SongComment> commentPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SongComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SongComment::getSongId, songId)
                .isNull(SongComment::getParentId) // 只查一级评论
                .eq(SongComment::getIsActive, true)
                .orderByDesc(SongComment::getAddedAt);

        IPage<SongComment> commentIPage = songCommentMapper.selectPage(commentPage, queryWrapper);

        List<SongCommentDTO> commentDTOList = commentIPage.getRecords().stream().map(comment -> {
            SongCommentDTO dto = convertToDTO(comment);
            // 额外需要插入用户名
            User user = userMapper.selectById(comment.getAddedBy());
            if (user != null) {
                dto.setAddedByName(user.getUsername());
            }
            return dto;
        }).collect(Collectors.toList());

        return new Page<SongCommentDTO>(page, pageSize, commentIPage.getTotal()).setRecords(commentDTOList);
    }

    /**
     * 获取某个评论的二级评论（分页）
     */
    public IPage<SongCommentDTO> getRepliesByComment(Integer commentId, Integer page, Integer pageSize) {
        Page<SongComment> repliesPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<SongComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SongComment::getParentId, commentId)
                .eq(SongComment::getIsActive, true)
                .orderByAsc(SongComment::getAddedAt);

        IPage<SongComment> replyIPage = songCommentMapper.selectPage(repliesPage, queryWrapper);

        List<SongCommentDTO> replyDTOList = replyIPage.getRecords().stream().map(comment -> {
            SongCommentDTO dto = convertToDTO(comment);
            // 额外需要插入用户名
            User user = userMapper.selectById(comment.getAddedBy());
            if (user != null) {
                dto.setAddedByName(user.getUsername());
            }
            return dto;
        }).collect(Collectors.toList());

        return new Page<SongCommentDTO>(page, pageSize, replyIPage.getTotal()).setRecords(replyDTOList);
    }

    /**
     * 提交新评论（支持一级和二级评论）
     */
    public boolean addComment(SongCommentDTO commentDTO) {
        SongComment comment = new SongComment();
        BeanUtils.copyProperties(commentDTO, comment);
        return songCommentMapper.insert(comment) > 0;
    }

    /**
     * DTO 转换方法
     */
    private SongCommentDTO convertToDTO(SongComment comment) {
        SongCommentDTO dto = new SongCommentDTO();
        BeanUtils.copyProperties(comment, dto);

        // 计算该评论的二级评论总数
        Integer totalReplies = Math.toIntExact(songCommentMapper.selectCount(
                new LambdaQueryWrapper<SongComment>().eq(SongComment::getParentId, comment.getId())
        ));
        dto.setTotalReplies(totalReplies);
        dto.setLoadedRepliesCount(0); // 初始加载时未加载任何二级评论

        return dto;
    }
}
