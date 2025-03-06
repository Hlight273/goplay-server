package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.VipInfo;
import com.github.goplay.entity.*;
import com.github.goplay.mapper.*;
import com.github.goplay.utils.PrivilegeCode;
import com.github.goplay.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.goplay.utils.CommonUtils.getDaysDiff;

@Service
public class UserService {


    private final UserVipMapper userVipMapper;
    private final RoomUserMapper roomUserMapper;
    private final RoomUserService roomUserService;
    private final UserMapper userMapper;

    public UserService(UserVipMapper userVipMapper, RoomUserMapper roomUserMapper, RoomUserService roomUserService, UserMapper userMapper) {
        this.userVipMapper = userVipMapper;
        this.roomUserMapper = roomUserMapper;
        this.roomUserService = roomUserService;
        this.userMapper = userMapper;
    }

    public User getUserByLoginInfo(User user) {
        User queryUser = new User(user.getUsername(), user.getPassword());
        User resultUser = userMapper.selectOne(new QueryWrapper<>(queryUser));
        return resultUser;
    }

    public int createUser(User user) {
        int i = userMapper.insert(user);
        return i;
    }

    public UserInfo getUserInfoById(int id) {
        User user = userMapper.selectById(id);
        if (user == null) return null;
        return new UserInfo(user.getId(),user.getUsername(), UserUtils.getAvatar(),user.getLevel());
    }

    public Integer getUserPrivilegeInRoom(Integer roomId, Integer userId) {
        RoomUser roomUser = roomUserMapper.selectOne(
                new QueryWrapper<RoomUser>()
                        .eq("room_id", roomId)
                        .eq("user_id", userId)
        );
        if(roomUser == null)
            return -1;
        return roomUser.getPrivilege();
    }

    public Boolean setUserPrivilegeInRoom(Integer roomId, Integer targetUserId, Integer setterUserId, Integer privilege) {
        RoomUser targetRoomUser = roomUserService.get_RoomUserInfo_By2Id(roomId, targetUserId);
        RoomUser setterRoomUser = roomUserService.get_RoomUserInfo_By2Id(roomId, setterUserId);
        if(targetRoomUser == null||setterRoomUser==null)
            return false;

        if(targetRoomUser.getPrivilege() < privilege){ //如果是降级， 大一个权限级别即可
            if(setterRoomUser.getPrivilege() < targetRoomUser.getPrivilege()){
                targetRoomUser.setPrivilege(privilege);
                return roomUserMapper.updateById(targetRoomUser)>0;
            }
        }
        else if(targetRoomUser.getPrivilege() > privilege){ //如果是升级 高两个级别 才能变更
            if(setterRoomUser.getPrivilege() < targetRoomUser.getPrivilege()-1){
                targetRoomUser.setPrivilege(privilege);
                return roomUserMapper.updateById(targetRoomUser)>0;
            }
        }
        else //权限无变化则返回false
            return false;

        return false;
    }

    @Transactional
    public Boolean setUserTransOwnerInRoom(Integer roomId, Integer newOwnerId, Integer oldOwnerId) {
        RoomUser OldRU = roomUserService.get_RoomUserInfo_By2Id(roomId, oldOwnerId);
        RoomUser NewRU = roomUserService.get_RoomUserInfo_By2Id(roomId, newOwnerId);
        if(OldRU == null||NewRU==null)
            return false;
        if(OldRU.getPrivilege() == PrivilegeCode.ROOM_OWNER){//如果是房主
            NewRU.setPrivilege(PrivilegeCode.ROOM_OWNER);
            OldRU.setPrivilege(PrivilegeCode.MEMBER);
            return roomUserMapper.updateById(NewRU)>0 && roomUserMapper.updateById(OldRU)>0;
        }
        return false;
    }

    public UserInfo getFullUserInfoByTwoId(Integer roomId, Integer userId){
        RoomUser roomUser = roomUserMapper.selectOne(
                new QueryWrapper<RoomUser>()
                        .eq("room_id", roomId)
                        .eq("user_id", userId)
        );
        UserInfo userInfo = this.getUserInfoById(userId);
        if(roomUser == null || userInfo == null){
            return null;
        }
        userInfo.setRoom_id(roomId);
        userInfo.setPrivilege(roomUser.getPrivilege());
        return userInfo;
    }


    //vip信息
    public boolean renewUserVipInfo(Integer userId, int vipLevel, Timestamp startTime, int validDays){
        UserVip targetUserVip = getUserVipById(userId);
        if(targetUserVip==null){
            return createUserVip(userId, vipLevel, startTime, validDays);
        }else{
            return updateUserVip(targetUserVip, vipLevel, startTime, validDays);
        }
    }
    public UserVip getUserVipById(Integer userId) {
        UserVip userVip = userVipMapper.selectById(userId);
        if (userVip == null) return null;
        return userVip;
    }
    public VipInfo getVipInfoByUserId(Integer userId) {
        UserVip userVip = userVipMapper.selectById(userId);
        if (userVip == null) return null;
        return new VipInfo(userId, userVip.getVipLevel(), userVip.getStartDate(), userVip.getEndDate(), getDaysDiff(userVip.getStartDate(), userVip.getEndDate()));
    }
    private boolean createUserVip(Integer userId, int vipLevel, Timestamp startTime, int validDays) {
        UserVip userVip = new UserVip(userId, vipLevel, startTime, validDays);
        return userVipMapper.insert(userVip) == 1;
    }
    private boolean updateUserVip(UserVip userVip, int vipLevel, Timestamp startTime, int validDays) {
        LambdaUpdateWrapper<UserVip> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserVip::getVipLevel,vipLevel);
        updateWrapper.set(UserVip::getStartDate,startTime);
        updateWrapper.set(UserVip::getEndDate, LocalDateTime.now().plusDays(validDays));
        updateWrapper.eq(UserVip::getId,userVip.getId());
        return userVipMapper.update(userVip,updateWrapper) == 1;
    }




}
