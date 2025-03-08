package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.Room;
import com.github.goplay.entity.RoomUser;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.RoomMapper;
import com.github.goplay.mapper.RoomUserMapper;
import com.github.goplay.mapper.UserMapper;
import com.github.goplay.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoomUserService {


    private final RoomUserMapper roomUserMapper;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;

    public RoomUserService(RoomUserMapper roomUserMapper, RoomMapper roomMapper, UserMapper userMapper) {
        this.roomUserMapper = roomUserMapper;
        this.roomMapper = roomMapper;
        this.userMapper = userMapper;
    }

    public Room isUserInRoom(Integer userId){
        RoomUser lastRoomUser = roomUserMapper.selectOne(
                new QueryWrapper<RoomUser>()
                        .eq("user_id", userId)
                        .eq("is_active", 1)
                        .orderByDesc("joined_at")
        );
        if (lastRoomUser != null) {
            Integer roomId = lastRoomUser.getRoomId();
            return roomMapper.selectById(roomId);
        }
        return null;
    }

    public List<UserInfo> getUserInfoListInRoom(Integer roomId){
        // 获取与房间关联的用户 ID 列表
        List<RoomUser> roomUsers = roomUserMapper.selectList(
                new QueryWrapper<RoomUser>().eq("room_id", roomId)
        );

        // 如果没有找到用户，返回空列表
        if (roomUsers.isEmpty())
            return Collections.emptyList();

        // 提取 userId 列表
        List<Integer> userIds = roomUsers.stream()
                .map(RoomUser::getUserId)
                .collect(Collectors.toList());

        // 根据 userId 列表查询 User 信息
        List<User> userList = userMapper.selectBatchIds(userIds);

        // 转换 User 为 UserDTO，并返回
        return userList.stream()
            .map(user -> {
                RoomUser roomUser = roomUsers.stream()
                    .filter(ru -> ru.getUserId().equals(user.getId()) && ru.getIsActive() == 1)
                    .findFirst()
                    .orElse(null);

                // 只在 roomUser 不为 null 时才创建 UserInfo
                if (roomUser != null) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setId(user.getId());
                    userInfo.setUsername(user.getUsername());
                    userInfo.setAvatarUrl(UserUtils.getAvatar());
                    userInfo.setPrivilege(roomUser.getPrivilege());
                    userInfo.setLevel(user.getLevel());
                    userInfo.setNickname(user.getNickname());
                    return userInfo;
                } else {
                    return null; // 返回 null 或者处理方式
                }
            })
            .filter(Objects::nonNull) // 过滤掉 null 的 UserInfo
            .collect(Collectors.toList());
    }

    public RoomUser get_RoomUserInfo_By2Id(Integer roomId, Integer userId){
        return roomUserMapper.selectOne(
            new QueryWrapper<RoomUser>()
                .eq("room_id", roomId)
                .eq("user_id", userId)
                .eq("is_active", true)
        );
    }

}
