package com.github.goplay.websocket;

import com.github.goplay.event.EventType;
import com.github.goplay.event.RoomUpdateEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RoomOnlineManager {

    private final ApplicationEventPublisher eventPublisher;

    private static final String ONLINE_KEY_PREFIX = "room:online:";
    private static final String SESSION_KEY_PREFIX = "session:user:";

    private static final long ROOM_EXPIRE_SECONDS = 120; // 房间活跃状态过期时间
    private static final long SESSION_EXPIRE_SECONDS = 120; // 会话过期时间
    private static final long USER_ACTIVITY_TIMEOUT = 180; // 用户不活跃的超时值，单位秒


    private final StringRedisTemplate stringRedisTemplate;

    public RoomOnlineManager(ApplicationEventPublisher eventPublisher, StringRedisTemplate stringRedisTemplate) {
        this.eventPublisher = eventPublisher;

        this.stringRedisTemplate = stringRedisTemplate;
    }


    //定时检查用户的在线状态，清除超时用户
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkUserStatus() {
        // 获取所有房间的用户列表
        Set<String> roomKeys = stringRedisTemplate.keys(ONLINE_KEY_PREFIX + "*");

        for (String roomKey : roomKeys) {
            String roomId = roomKey.substring(ONLINE_KEY_PREFIX.length());
            Map<Object, Object> onlineUsers = this.getOnlineUsers(roomId);

            for (Map.Entry<Object, Object> entry : onlineUsers.entrySet()) {
                String userId = (String) entry.getKey();
                String sessionId = (String) entry.getValue();

                String sessionKey = buildSessionKey(sessionId);
                String value =  stringRedisTemplate.opsForValue().get(sessionKey);

                if (value == null) {
                    //如果没有心跳包，说明用户超时
                    markOffline(roomKey.substring(ONLINE_KEY_PREFIX.length()), userId);
                    //发布房间用户列表更新事件
                    eventPublisher.publishEvent(new RoomUpdateEvent(this, Integer.parseInt(roomId), EventType.ROOM_USER_LIST));
                }
            }
        }
    }


    /**
     * 用户上线（加入房间）
     * 使用 Hash 来存储房间内的用户信息
     */
    public void markOnline(String roomId, String userId, String sessionId) {
        String roomKey = buildRoomKey(roomId);
        String sessionKey = buildSessionKey(sessionId);

        // 房间内的 Hash 存储，用户 ID 映射到 sessionId
        stringRedisTemplate.opsForHash().put(roomKey, userId, sessionId);
        stringRedisTemplate.expire(roomKey, ROOM_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 存储 session -> userId + roomId 映射
        stringRedisTemplate.opsForValue().set(sessionKey, userId + ":" + roomId, SESSION_EXPIRE_SECONDS, TimeUnit.SECONDS);
        eventPublisher.publishEvent(new RoomUpdateEvent(this, Integer.parseInt(roomId), EventType.ROOM_USER_LIST));
    }

    /**
     * 用户下线（通过 roomId 和 userId）
     * 使用 Hash 来移除用户
     */
    private void markOffline(String roomId, String userId) {
        String roomKey = buildRoomKey(roomId);
        stringRedisTemplate.opsForHash().delete(roomKey, userId);
    }

    /**
     * 用户下线（通过 sessionId）
     * 根据 sessionId 获取对应的 userId 和 roomId，然后删除
     */
    public void markOfflineBySession(String sessionId) {
        String sessionKey = buildSessionKey(sessionId);
        String value = stringRedisTemplate.opsForValue().get(sessionKey);

        if (value != null) {
            String[] parts = value.split(":");
            String userId = parts[0];
            String roomId = parts[1];
            markOffline(roomId, userId);
            stringRedisTemplate.delete(sessionKey);  // 删除 session 映射
            eventPublisher.publishEvent(new RoomUpdateEvent(this, Integer.parseInt(roomId), EventType.ROOM_USER_LIST));
        }
    }

    /**
     * 获取房间内所有在线用户
     * 使用 Redis Hash 来获取用户列表
     */
    public Map<Object, Object> getOnlineUsers(String roomId) {
        String roomKey = buildRoomKey(roomId);
        return stringRedisTemplate.opsForHash().entries(roomKey);
    }

    /**
     * 判断用户是否在线（是否在某个房间中）
     * 通过 Hash 判断是否存在该用户
     */
    public boolean isOnline(String roomId, String userId) {
        String roomKey = buildRoomKey(roomId);
        return stringRedisTemplate.opsForHash().hasKey(roomKey, userId);
    }

    private String buildRoomKey(String roomId) {
        return ONLINE_KEY_PREFIX + roomId;
    }

    private String buildSessionKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }
}
