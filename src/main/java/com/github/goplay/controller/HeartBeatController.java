package com.github.goplay.controller;

import com.github.goplay.websocket.RoomOnlineManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class HeartBeatController {


    private final RoomOnlineManager roomOnlineManager;

    public HeartBeatController(@Qualifier("redisTemplate") RedisTemplate redisTemplate, RoomOnlineManager roomOnlineManager) {
        this.roomOnlineManager = roomOnlineManager;
    }

    @MessageMapping("/heartbeat")
    public void receiveHeartbeat(@Payload String message, @Header("userId") String userId, @Header("roomId") String roomId, StompHeaderAccessor stompHeaderAccessor) {
        String sessionId = stompHeaderAccessor.getSessionId();
        System.out.println(">>> 收到心跳包: 用户ID=" + userId + ", 房间ID=" + roomId + ", 会话ID=" + sessionId);
        roomOnlineManager.markOnline(roomId, userId, sessionId);
    }
}
