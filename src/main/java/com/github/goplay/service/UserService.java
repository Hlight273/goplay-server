package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.RoomUser;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.RoomMapper;
import com.github.goplay.mapper.RoomUserMapper;
import com.github.goplay.mapper.UserMapper;
import com.github.goplay.utils.PrivilegeCode;
import com.github.goplay.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomUserMapper roomUserMapper;
    @Autowired
    private RoomUserService roomUserService;

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
        return new UserInfo(user.getId(),user.getUsername(), UserUtils.getAvatar());
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


}
