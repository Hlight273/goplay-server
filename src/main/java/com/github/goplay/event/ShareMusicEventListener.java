package com.github.goplay.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShareMusicEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    public ShareMusicEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleShareMusic(ShareMusicEvent event) {
        switch (event.getType()) {
            case EventType.MUSIC_SHARE:
                String userId = event.getShareMessage().getReceiverId().toString();
                String destination = "/queue/share/music";
                try {
                    messagingTemplate.convertAndSendToUser(
                            userId,
                            destination,
                            event.getShareMessage());
                    System.out.println("音乐分享消息已发送成功");
                } catch (Exception e) {
                    System.err.println("发送消息失败: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println("音乐分享消息已发送");
                break;
            default:
                System.out.println(">>>ShareMusicListener收到了未知事件类型: " + event.getType());
                break;
        }
    }
}
