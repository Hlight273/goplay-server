package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.UserMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.goplay.utils.Data.MBTICodec.isMbtiSimilar;

@Service
public class UserCommunityService {
    private final UserMapper userMapper;
    private final UserService userService;

    public UserCommunityService(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    /**
     * 用户MBTI匹配查询
     * @param userId 用户I
     * @param n 要查几个
     * @param similarityThreshold 匹配mbti字母数(1-4个)
     * @return userinfoList
     */
    @Cacheable(value = "similarMbtiUsers",key = "#userId",cacheManager = "midnightCacheManager")
    public List<UserInfo> getRandomSimilarMbtiUsers(int userId, int n, int similarityThreshold) {
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null || currentUser.getMbtiType() == null) {
            return Collections.emptyList();
        }

        List<User> allUsersWithMbti = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .isNotNull(User::getMbtiType)
                        .ne(User::getId, userId) //自己除外
        );

        List<User> similarUsers = allUsersWithMbti.stream()
                .filter(user -> isMbtiSimilar(currentUser.getMbtiType(), user.getMbtiType(), similarityThreshold))
                .collect(Collectors.toList());

        Collections.shuffle(similarUsers);
        return similarUsers.stream()
                .limit(n)
                .map(user -> userService.getUserInfoById(user.getId()))
                .collect(Collectors.toList());
    }
}
