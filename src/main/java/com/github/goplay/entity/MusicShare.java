package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@TableName("music_share")
public class MusicShare {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String curStatus; // "PENDING", "STORED", "DROPPED"
    private Integer songId;
    private String contentText;
    @Getter
    @Setter(AccessLevel.NONE)
    @TableField(fill = FieldFill.INSERT)
    private Timestamp addedAt;
    private Timestamp handledAt;
}
