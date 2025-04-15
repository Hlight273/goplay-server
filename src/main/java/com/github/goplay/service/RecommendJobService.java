package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.SongContent;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.Song;
import com.github.goplay.mapper.PlaylistMapper;
import com.github.goplay.mapper.SongMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendJobService {

    // 推荐歌单缓存Key
    private static final String CACHE_RECOMMEND_PLAYLISTS = "recommendPlaylists";
    // 热门歌曲缓存Key
    private static final String CACHE_HOT_SONGS = "hotSongs";
    //过期时间3h
    private static final Integer EXPIRE_HOURS = 3;

    private final SongService songService;
    private final SongMapper songMapper;
    private final PlaylistMapper playlistMapper;
    private final PlaylistService playlistService;
    private final RedisTemplate redisTemplate;
    private final RecommendService recommendService;

    public RecommendJobService(SongService songService, SongMapper songMapper, PlaylistMapper playlistMapper, PlaylistService playlistService, @Qualifier("redisTemplate") RedisTemplate redisTemplate, RecommendService recommendService) {
        this.songService = songService;
        this.songMapper = songMapper;
        this.playlistMapper = playlistMapper;
        this.playlistService = playlistService;
        this.redisTemplate = redisTemplate;
        this.recommendService = recommendService;
    }

    //这个缓存方法防止脏数据的思想是什么？ 总结就是缓存命中后校验数据状态：
    //当用户请求推荐歌单时，先从 Redis 获取推荐列表，然后根据这些 ID 去数据库查询对应的歌单(IN)，然后移除无效(isactive等失效)的Id，并返回给用户。
    //相比直接查数据库或者直接返回缓存，至少保证了数据的可靠
    //不过在该方法中我们还是采用了不移除而是更新 懒更新缓存：如果发现缓存中有“脏数据”，在后台更新或重新写入缓存。避免用户看到已经被隐藏或删除的歌单。
    public List<PlaylistInfo> getCachedSysRecommendPlaylists(int limit) {
        String cacheKey = CACHE_RECOMMEND_PLAYLISTS + ":" + limit;
        List<Integer> playlistIds = redisTemplate.opsForList().range(cacheKey, 0, -1);

        List<Playlist> playlists;

        if (playlistIds == null || playlistIds.isEmpty()) {

            playlists = fetchRecommendPlaylistsFromDB(limit);//缓存未命中，查询数据库并缓存
            cachePlaylistIds(cacheKey, playlists);
        } else {

            playlists = playlistMapper.selectBatchIds(playlistIds).stream()//命中，查库验证状态（懒更新）
                    .filter(p -> p.getIsActive() == 1 && p.getIsPublic() == 1)
                    .collect(Collectors.toList());


            if (playlists.size() < playlistIds.size()) { //异步或懒更新：若数量变化，更新缓存（可选）
                cachePlaylistIds(cacheKey, playlists);
            }
        }

        return playlistService.convertToPlaylistInfo(playlists);
    }



    public List<SongContent> getCachedHotSongs(int limit) {
        String cacheKey = CACHE_HOT_SONGS + ":" + limit;
        List<Integer> songIds = redisTemplate.opsForList().range(cacheKey, 0, -1);

        List<Song> songs;

        if (songIds == null || songIds.isEmpty()) {
            songs = fetchHotSongsFromDB(limit);
            cacheSongIds(cacheKey, songs);
        } else {
            songs = songMapper.selectBatchIds(songIds).stream()
                    .filter(s -> s.getIsActive() == 1)
                    .collect(Collectors.toList());

            if (songs.size() < songIds.size()) {
                cacheSongIds(cacheKey, songs);
            }
        }

        return songs.stream()
                .map(songService::getSongContentBySong)
                .collect(Collectors.toList());
    }
    private void cachePlaylistIds(String key, List<Playlist> playlists) {
        List<Integer> ids = playlists.stream().map(Playlist::getId).collect(Collectors.toList());
        redisTemplate.delete(key);
        if (!ids.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(key, ids.toArray());
            redisTemplate.expire(key, Duration.ofHours(EXPIRE_HOURS)); //过期时间
        }
    }

    private void cacheSongIds(String key, List<Song> songs) {
        List<Integer> ids = songs.stream().map(Song::getId).collect(Collectors.toList());
        redisTemplate.delete(key);
        if (!ids.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(key, ids.toArray());
            redisTemplate.expire(key, Duration.ofHours(EXPIRE_HOURS));
        }
    }

    private List<Playlist> fetchRecommendPlaylistsFromDB(int limit) {
        Page<Playlist> page = new Page<>(1, limit);
        LambdaQueryWrapper<Playlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Playlist::getIsActive, 1)
                .eq(Playlist::getIsPublic, 1)
                .orderByDesc(Playlist::getAddedAt);
        return playlistMapper.selectPage(page, wrapper).getRecords();
    }

    public List<Song> fetchHotSongsFromDB(int limit) {
        Page<Song> page = new Page<>(1, limit);
        LambdaQueryWrapper<Song> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Song::getIsActive, 1)
                .orderByDesc(Song::getPlayCount);
        return songMapper.selectPage(page, wrapper).getRecords();
    }
}
