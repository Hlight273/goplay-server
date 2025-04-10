package com.github.goplay.event;

import com.github.goplay.service.RoomSongService;
import com.github.goplay.service.RoomUserService;
import com.github.goplay.websocket.RoomOnlineManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Component
public class RoomEventListener {
    private final RoomUserService roomUserService;
    private final RoomSongService roomSongService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomOnlineManager roomOnlineManager;

    public RoomEventListener(RoomUserService roomUserService, RoomSongService roomSongService, SimpMessagingTemplate messagingTemplate, @Qualifier("redisTemplate") RedisTemplate redisTemplate, RoomOnlineManager roomOnlineManager) {
        this.roomUserService = roomUserService;
        this.roomSongService = roomSongService;
        this.messagingTemplate = messagingTemplate;
        this.roomOnlineManager = roomOnlineManager;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = Optional.ofNullable(accessor.getNativeHeader("userId"))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElse(null);

        String roomId = Optional.ofNullable(accessor.getNativeHeader("roomId"))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElse(null);

        if (userId == null || roomId == null) {
            return;
        }
        String sessionId = accessor.getSessionId();

        //roomUserService.setOnlineStatus(Integer.parseInt(userId), true);//更新数据库在线状态
        System.out.println(">>>用户: "+ userId +"/"+ sessionId+ "连接，已记录在线状态");

        roomOnlineManager.markOnline(roomId, userId, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        roomOnlineManager.markOfflineBySession(sessionId);
        System.out.println(">>>用户: "+ sessionId + "断开连接，已记录在线状态");
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
