package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.goplay.constant.MusicShareStatus;
import com.github.goplay.dto.newDTO.MusicShareMessage;
import com.github.goplay.entity.MusicShare;
import com.github.goplay.event.EventType;
import com.github.goplay.event.ShareMusicEvent;
import com.github.goplay.mapper.MusicShareMapper;
import com.github.goplay.messageQueue.ShareMessageSender;
import com.github.goplay.utils.CommonUtils;
import com.github.goplay.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicShareService {


    private final ApplicationEventPublisher eventPublisher;
    private final MusicShareMapper mapper;
    private final ShareMessageSender sender;
    private final UserService userService;

    public void sendShare(Integer senderId, MusicShareMessage musicShareMessage) {
        MusicShare entity = new MusicShare();
        entity.setSenderId(senderId);
        entity.setReceiverId(musicShareMessage.getReceiverId());
        entity.setSongId(musicShareMessage.getSongId());
        entity.setContentText(musicShareMessage.getContentText());
        entity.setCurStatus(MusicShareStatus.PENDING);
        mapper.insert(entity);

        MusicShareMessage message = new MusicShareMessage();
        message.setShareId(entity.getId());
        message.setSenderId(senderId);
        message.setReceiverId(musicShareMessage.getReceiverId());
        message.setSongId(musicShareMessage.getSongId());
        message.setSenderAvatar(UserUtils.getAvatar());
        message.setSenderName(userService.getUserInfoById(musicShareMessage.getReceiverId()).getNickname());
        message.setShareTime(LocalDateTime.now().toString());
        message.setContentText(musicShareMessage.getContentText());
        sender.sendShareMessage(message);
    }

    public void handleIncomingMessage(MusicShareMessage message) {
        eventPublisher.publishEvent(new ShareMusicEvent(this, message, EventType.MUSIC_SHARE));//转发给在线相应用户
    }

    public void handleUserDecision(Integer shareId, boolean store) {
        MusicShare entity = mapper.selectById(shareId);
        if (entity == null) return;

        entity.setCurStatus(store ? MusicShareStatus.STORED : MusicShareStatus.DROPPED);
        entity.setHandledAt(CommonUtils.curTime());
        mapper.updateById(entity);
    }

    public List<MusicShareMessage> getMyActiveMessages(Integer userId) {
        List<MusicShare> shares = mapper.selectList(new LambdaQueryWrapper<MusicShare>()
                .eq(MusicShare::getReceiverId, userId)
                .ne(MusicShare::getCurStatus, MusicShareStatus.DROPPED));

        return shares.stream().map(share -> {
            MusicShareMessage msg = new MusicShareMessage();
            msg.setShareId(share.getId());
            msg.setSenderId(share.getSenderId());
            msg.setReceiverId(share.getReceiverId());
            msg.setSongId(share.getSongId());
            msg.setShareTime(share.getAddedAt().toString());
            msg.setCurStatus(share.getCurStatus());
            msg.setSenderAvatar(UserUtils.getAvatar());
            msg.setSenderName(userService.getUserInfoById(share.getReceiverId()).getNickname());
            msg.setContentText(share.getContentText());
            return msg;
        }).collect(Collectors.toList());
    }
}
