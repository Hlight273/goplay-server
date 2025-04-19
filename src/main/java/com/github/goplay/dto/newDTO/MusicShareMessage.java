package com.github.goplay.dto.newDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class MusicShareMessage implements Serializable {
    private Integer shareId;
    private Integer senderId;
    private Integer receiverId;
    private Integer songId;
    private String curStatus;
    private String contentText;
    private String senderName;
    private String senderAvatar;
    private String shareTime;
}
