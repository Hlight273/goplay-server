package com.github.goplay.websocket;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebSocketSessionRegistry {

    private static final String ROOM_SESSION_KEY_PREFIX = "room:sessions:"; // Redis 中房间会话的前缀

    private RedisTemplate<String, Object> redisTemplate; // RedisTemplate 用于操作 Redis 数据

    public WebSocketSessionRegistry(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 用户连接时注册session
    public void registerSession(String roomId, String userId, String sessionId) {
        String roomSessionKey = ROOM_SESSION_KEY_PREFIX + roomId;
        redisTemplate.opsForHash().put(roomSessionKey, userId, sessionId); // 使用哈希存储用户的 sessionId
    }

    // 用户断开时注销session
    public void unregisterSession(String roomId, String userId) {
        String roomSessionKey = ROOM_SESSION_KEY_PREFIX + roomId;
        redisTemplate.opsForHash().delete(roomSessionKey, userId); // 从哈希中移除用户的 sessionId
    }

    // 获取房间内所有session（排除发送者）
    public Map<Object, Object> getRoomSessionsExcludingSender(String roomId, String senderUserId) {
        String roomSessionKey = ROOM_SESSION_KEY_PREFIX + roomId;
        Map<Object, Object> userSessions = redisTemplate.opsForHash().entries(roomSessionKey); // 获取所有房间内的 sessionId
        userSessions.remove(senderUserId); // 排除发送者
        return userSessions;
    }

    // 获取某个房间内用户的session
    public String getTargetSession(Integer roomId, Integer userId) {
        String roomSessionKey = ROOM_SESSION_KEY_PREFIX + roomId;
        // 从 Redis 中获取房间的所有 session
        Map<Object, Object> userSessions = redisTemplate.opsForHash().entries(roomSessionKey);

        if (userSessions == null || !userSessions.containsKey(userId.toString())) {
            // 如果没有找到用户的 session，可以抛出异常或返回 null
            throw new RuntimeException("User session not found for userId: " + userId + " in room: " + roomId);
        }

        // 返回目标用户的 sessionId
        return (String) userSessions.get(userId.toString());
    }
}