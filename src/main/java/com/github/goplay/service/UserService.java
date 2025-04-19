package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.VipInfo;
import com.github.goplay.entity.*;
import com.github.goplay.mapper.*;
import com.github.goplay.utils.Data.PrivilegeCode;
import com.github.goplay.utils.UserUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    public User getUserByLoginInfo(User formUser) {
        User targetUser = getUserByUsername(formUser.getUsername());
        if (targetUser == null)
            return null;
        String rawPwd = formUser.getPassword();
        String encryptedPwd = targetUser.getPassword();
        if(UserUtils.verifyPassword(rawPwd, encryptedPwd))
            return targetUser;
        else
            return null;
    }
    public boolean verifiedPwd(String rawPwd, Integer userId) {
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null)
            return false;
        String encryptedPwd = targetUser.getPassword();
        if(UserUtils.verifyPassword(rawPwd, encryptedPwd))
            return true;
        else
            return false;
    }

    public int createUser(User user) {
        String rawPwd = user.getPassword();
        user.setPassword(UserUtils.encryptPassword(rawPwd));
        int i = userMapper.insert(new User(0, user.getUsername(), user.getPassword(), 0));
        return i;
    }

    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    @Cacheable(value = "userInfo", key = "#id")
    public UserInfo getUserInfoById(int id) {
        User user = userMapper.selectById(id);
        if (user == null) return null;
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), UserUtils.getAvatar(), user.getLevel(), user.getNickname());
        if(user.getMbtiType()!=null)
            userInfo.setMbtiType(user.getMbtiType());
        userInfo.setIsActive(user.getIsActive());
        return userInfo;
    }


    @Caching(evict = {
            @CacheEvict(value = "userInfo", key = "#userId"),
            @CacheEvict(value = "similarMbtiUsers", key = "#userId",cacheManager = "midnightCacheManager")})
    public Boolean setUserMbtiType(int userId, Integer mbtiType) {
        User user = new User();
        user.setMbtiType(mbtiType);
        return userMapper.update(user,
                new LambdaQueryWrapper<User>()
                        .eq(User::getId, userId)) > 0;
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

    @CacheEvict(value = "userInfo", key = "#userId")
    public boolean updateUserNickname(Integer userId, String nickname) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);
        wrapper.set(User::getNickname, nickname);
        return userMapper.update(wrapper)>0;
    }

    @CacheEvict(value = "userInfo", key = "#userId")
    public boolean updateUserPwd(Integer userId, String rawPwd) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);
        wrapper.set(User::getPassword, UserUtils.encryptPassword(rawPwd));
        return userMapper.update(wrapper)>0;
    }

    @CacheEvict(value = "userInfo", key = "#userId")
    public Integer getUserHPoints(Integer userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        User user = userMapper.selectOne(queryWrapper);
        Integer hPoints = user.gethPoints();
        return hPoints;
    }
    @CacheEvict(value = "userInfo", key = "#userId")
    public boolean updateUserHPoints(Integer userId, Integer hPoints) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId);
        //wrapper.set(User::gethPoints, hPoints);
        wrapper.setSql("h_points = h_points + " + hPoints);
        return userMapper.update(wrapper)>0;
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
        if(validDays==-1)//-1代表无限期
            validDays = 365*99;
        UserVip userVip = new UserVip(userId, vipLevel, startTime, Timestamp.valueOf(LocalDateTime.now().plusDays(validDays)));
        return userVipMapper.insert(userVip) >= 1;
    }
    private boolean updateUserVip(UserVip userVip, int vipLevel, Timestamp startTime, int validDays) {
        if(validDays==-1)//-1代表无限期
            validDays = 365*99;
        LambdaUpdateWrapper<UserVip> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("vip_level = vip_level + " + vipLevel);
        updateWrapper.set(UserVip::getStartDate,startTime);
        updateWrapper.set(UserVip::getEndDate, LocalDateTime.now().plusDays(validDays));
        updateWrapper.eq(UserVip::getId,userVip.getId());
        return userVipMapper.update(userVip,updateWrapper) >= 1;
    }


    public boolean isUserExist(Integer userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        User user = userMapper.selectOne(queryWrapper);
        return user != null && user.getIsActive()==1;
    }

}
