package com.github.goplay.event;

import com.github.goplay.service.RoomSongService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class PlayerDataEventListener {
    private final RoomSongService roomSongService;
    private final SimpMessagingTemplate messagingTemplate;
    public PlayerDataEventListener(RoomSongService roomSongService, SimpMessagingTemplate messagingTemplate) {
        this.roomSongService = roomSongService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleRoomUpdate(RoomUpdateEvent event) {
        switch (event.getType()) {
            case EventType.ROOM_PLAYER_DATA:
                var songContentList = roomSongService.getSongContentListInRoom(event.getRoomId());
                messagingTemplate.convertAndSend("/topic/" + event.getRoomId() + "/songContentList", songContentList);
                break;

            default:
                System.out.println(">>>PlayerDataListener收到了未知事件类型: " + event.getType());
                break;
        }
    }
}
