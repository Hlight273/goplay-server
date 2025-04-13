//package com.github.goplay.job;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CacheEvictJob {
//
//    // 每天凌晨00:00清除此缓存
//    @Scheduled(cron = "0 0 0 * * ?")
//    @CacheEvict(value = {"recommendPlaylists", "hotSongs"}, allEntries = true)
//    public void evictAllCachesAtMidnight() {
//    }
//}