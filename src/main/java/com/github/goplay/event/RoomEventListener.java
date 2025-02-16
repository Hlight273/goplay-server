package com.github.goplay.event;

import com.github.goplay.service.RoomSongService;
import com.github.goplay.service.RoomUserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RoomEventListener {
    private final RoomUserService roomUserService;
    private final RoomSongService roomSongService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomEventListener(RoomUserService roomUserService, RoomSongService roomSongService, SimpMessagingTemplate messagingTemplate) {
        this.roomUserService = roomUserService;
        this.roomSongService = roomSongService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleRoomUpdate(RoomUpdateEvent event) {
        switch (event.getType()) {
            case EventType.ROOM_USER_LIST:
                var userInfoList = roomUserService.getUserInfoListInRoom(event.getRoomId());
                messagingTemplate.convertAndSend("/topic/" + event.getRoomId() + "/userInfoList", userInfoList);
                break;

            case EventType.ROOM_SONG_LIST:
                var songContentList = roomSongService.getSongContentListInRoom(event.getRoomId());
                messagingTemplate.convertAndSend("/topic/" + event.getRoomId() + "/songContentList", songContentList);
                break;

            default:
                System.out.println(">>>RoomListener收到了未知事件类型: " + event.getType());
                break;
        }
    }
}
