//package com.github.goplay.job;
//
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class PlayerStatusThrottler {
//    private Map<String, Long> lastUpdateTime = new ConcurrentHashMap<>();
//    private static final long THROTTLE_INTERVAL = 1000; // 1秒的节流时间
//
//    public boolean shouldAllowUpdate(Integer roomId, Integer userId) {
//        String key = roomId + "_" + userId;
//        long currentTime = System.currentTimeMillis();
//        Long lastTime = lastUpdateTime.get(key);
//
//        if (lastTime == null || (currentTime - lastTime) >= THROTTLE_INTERVAL) {
//            lastUpdateTime.put(key, currentTime);
//            return true;
//        }
//        return false;
//    }
//}
