package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.entity.RecommendPlaylist;
import com.github.goplay.mapper.RecommendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RecommendService {

    //管理员推荐歌单缓存Key
    private static final String CACHE_MANAGER_RECOMMEND_PLAYLISTS = "manager_RecommendPlaylists";
    private static final String CACHE_SINGLE_RECOMMEND_PLAYLIST = "single_RecommendPlaylist";

    private static final long CACHE_EXPIRATION_TIME = -1;  // 默认缓存过期时间为无限期
    private static final TimeUnit CACHE_EXPIRATION_UNIT = TimeUnit.HOURS; // 时间单位


    private final RecommendMapper recommendMapper;
    private final PlaylistService playlistService;
    private final RedisTemplate redisTemplate;

    @Lazy
    @Autowired
    private RecommendService selfProxy;

    public RecommendService(RecommendMapper recommendMapper, PlaylistService playlistService, @Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        this.recommendMapper = recommendMapper;
        this.playlistService = playlistService;
        this.redisTemplate = redisTemplate;
    }

    //尝试从缓存获取推荐歌单列表，未命中再从数据库中获取
    public List<PlaylistInfo> getRecommendPlaylists() {
        //尝试从缓存获取推荐歌单
        List<PlaylistInfo> playlistInfos = (List<PlaylistInfo>) redisTemplate.opsForValue().get(CACHE_MANAGER_RECOMMEND_PLAYLISTS);
        if (playlistInfos != null) {
            return playlistInfos;  //命中
        }

        //从数据库获取
        List<Integer> playlistIds = recommendMapper.selectList(
                new LambdaQueryWrapper<RecommendPlaylist>()
                        .eq(RecommendPlaylist::getIsActive, 1)
                        .orderByDesc(RecommendPlaylist::getId)
        ).stream().map(RecommendPlaylist::getPlaylistId).toList();

        playlistInfos = playlistService.get_PlaylistInfoList_ByPlaylistIds(playlistIds);

        //将查询结果缓存到 Redis（如果时间大于0，则设置过期时间；如果小于等于0，则不设置过期）
        setCacheWithExpiration(CACHE_MANAGER_RECOMMEND_PLAYLISTS, playlistInfos, CACHE_EXPIRATION_TIME);

        return playlistInfos;
    }

    // 判断歌单是否已推荐
    public boolean isRecommended(Integer playlistId) {
        return selfProxy.getRecommendPlaylistOptional(playlistId).isPresent();
    }

    //查一个推荐歌单的内容，有缓存
    @Cacheable(value = CACHE_SINGLE_RECOMMEND_PLAYLIST, key = "#playlistId")
    public Optional<RecommendPlaylist> getRecommendPlaylistOptional(Integer playlistId) {
        LambdaQueryWrapper<RecommendPlaylist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendPlaylist::getPlaylistId, playlistId)
                .eq(RecommendPlaylist::getIsActive, 1);
        return Optional.ofNullable(recommendMapper.selectOne(wrapper));
    }


    //添加推荐歌单，要更新缓存
    public boolean addRecommendPlaylist(Integer playlistId) {
        //检查歌单是否已经推荐
        Optional<RecommendPlaylist> existing = selfProxy.getRecommendPlaylistOptional(playlistId);
        if (existing.isPresent()) return false;  //歌单已经存在，不需要重复添加

        RecommendPlaylist recommendPlaylist = new RecommendPlaylist(0, playlistId);
        int result = recommendMapper.insert(recommendPlaylist);

        if (result > 0) {
            //数据库插入成功后，更新缓存
            updateCacheAfterRecommendation();
        }
        return result > 0;
    }

    //删除推荐歌单，要更新缓存
    @CacheEvict(value = CACHE_SINGLE_RECOMMEND_PLAYLIST, key = "#playlistId")
    public boolean removeRecommendPlaylist(Integer playlistId) {
        return selfProxy.getRecommendPlaylistOptional(playlistId)
                .map(recommend -> {
                    recommend.setIsActive(0); //软删除
                    int result = recommendMapper.updateById(recommend);

                    if (result > 0) {
                        //新缓存
                        updateCacheAfterRecommendation();
                    }
                    return result > 0;
                })
                .orElse(false);
    }

    //懒更新,仅在数据变更时调用(cacheEvict)
    private void updateCacheAfterRecommendation() {
        redisTemplate.delete(CACHE_MANAGER_RECOMMEND_PLAYLISTS);
        getRecommendPlaylists();
    }

    // 缓存设置方法，根据过期时间决定是否设置过期
    private void setCacheWithExpiration(String cacheKey, List<PlaylistInfo> playlistInfos, long expirationTime) {
        if (expirationTime > 0) {
            redisTemplate.opsForValue().set(cacheKey, playlistInfos, expirationTime, CACHE_EXPIRATION_UNIT);
        } else {
            redisTemplate.opsForValue().set(cacheKey, playlistInfos); // 不设置过期时间，表示无限期缓存
        }
    }
}
