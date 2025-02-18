package com.github.goplay.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
public class WebSocketEventListener {

    public final WebSocketSessionRegistry sessionRegistry;

    public WebSocketEventListener(WebSocketSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        String userId = headerAccessor.getFirstNativeHeader("userId");
        String roomId = headerAccessor.getFirstNativeHeader("roomId");

        sessionRegistry.registerSession(roomId, userId, headerAccessor.getSessionId());
        if (sessionAttributes != null && userId != null && roomId != null) {
            sessionAttributes.put("userId", userId);
            sessionAttributes.put("roomId", roomId);
            System.out.println("✅ 用户连接: userId=" + userId + ", roomId=" + roomId + ", sId:" + headerAccessor.getSessionId());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 从 sessionAttributes 获取 userId 和 roomId
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            String userId = (String) sessionAttributes.get("userId");
            String roomId = (String) sessionAttributes.get("roomId");

            if (userId != null && roomId != null) {
                sessionRegistry.unregisterSession(roomId, userId);
                System.out.println("⚠ 用户断开: " + headerAccessor.getSessionId() + "（userId: " + userId + "，roomId: " + roomId + "）");
            } else {
                System.out.println("⚠ 无法找到 userId 或 roomId，可能用户未正确加入房间");
            }
        } else {
            System.out.println("⚠ sessionAttributes 为空，无法获取 userId 和 roomId");
        }
    }
}
