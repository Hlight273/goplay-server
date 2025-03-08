package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.goplay.entity.Room;
import com.github.goplay.entity.RoomUser;
import com.github.goplay.entity.User;
import com.github.goplay.mapper.RoomMapper;
import com.github.goplay.mapper.RoomUserMapper;
import com.github.goplay.mapper.UserMapper;
import com.github.goplay.utils.PrivilegeCode;
import com.github.goplay.utils.RoomCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RoomService {
    public static final int MAX_ROOM_COUNT = 6;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;
    private final RoomUserMapper roomUserMapper;

    public RoomService(RoomMapper roomMapper, UserMapper userMapper, RoomUserMapper roomUserMapper) {
        this.roomMapper = roomMapper;
        this.userMapper = userMapper;
        this.roomUserMapper = roomUserMapper;
    }


    // 创建房间
    @Transactional
    public Room createRoom(Integer userId) {
        java.sql.Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        User user = userMapper.selectById(userId);
        String roomName = user.getUsername() + "的房间";
        String roomCode = RoomCodeUtils.generateRoomCode(userId, currentTimestamp.getTime());
        Room room = new Room(0,roomName, userId, MAX_ROOM_COUNT, 1, roomCode, currentTimestamp, 1);
        roomMapper.insert(room);

        RoomUser roomUser = new RoomUser(0, room.getId(), userId, currentTimestamp, 1, PrivilegeCode.ROOM_OWNER);
        roomUserMapper.insert(roomUser);

        return room;
    }

    // 用户加入房间
    @Transactional
    public Room addUserToRoom(Integer userId, String roomCode) {
        //根据房间代码 查找目标房间
        Room targetRoom = this.getRoomByRoomCode(roomCode);

        if (targetRoom == null)
            return null; // 房间不存在

        if (targetRoom.getCurrentUsers() >= targetRoom.getMaxUsers())
            return null; //房间已满

        User user = userMapper.selectById(userId);
        if (user == null)
            return null; // 用户不存在

        // 房间_用户表 insert
        java.sql.Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        roomUserMapper.insert(new RoomUser(0,targetRoom.getId(),userId,currentTimestamp,1, PrivilegeCode.MEMBER));

        //房间表 update 当前用户+1
        targetRoom.setCurrentUsers(targetRoom.getCurrentUsers() + 1);
        roomMapper.updateById(targetRoom);

        return targetRoom; // 用户成功加入房间

    }

    // 用户退出房间
    @Transactional
    public boolean deleteUserFromRoom(Integer userId, String roomCode) {
        //根据房间代码 查找目标房间
        Room targetRoom = this.getRoomByRoomCode(roomCode);

        if (targetRoom == null)
            return false; // 房间不存在

        User targetUser = userMapper.selectById(userId);
        if (targetUser == null)
            return false; // 用户不存在

        //房间-用户表 update active为false
        roomUserMapper.update(
                new UpdateWrapper<RoomUser>()
                        .eq("is_active", true)
                        .eq("room_id", targetRoom.getId())
                        .eq("user_id", userId)
                        .set("is_active", false)
        );

        //房间表 update 当前用户-1，如果当前用户为0 则房间关闭。 如果没关闭要移交权限
        int curUser = targetRoom.getCurrentUsers() - 1;
        targetRoom.setCurrentUsers(curUser);
        if (curUser==0)
            targetRoom.setIsActive(0);
        else {
            //房主退出还有成员 则移交房主给先加入的权限高者
            if(targetRoom.getOwnerId() == targetUser.getId()){
                RoomUser targetRiseUpRoomUser = roomUserMapper.selectOne(//roomUser select
                    new UpdateWrapper<RoomUser>()
                        .eq("is_active", 1)
                        .eq("room_id", targetRoom.getId())
                        .orderByAsc("privilege")
                        .last("limit 1")
                );
                if(targetRiseUpRoomUser!=null){
                    targetRoom.setOwnerId(targetRiseUpRoomUser.getUserId());
                    targetRiseUpRoomUser.setPrivilegeRoomOwner();
                    roomUserMapper.updateById(targetRiseUpRoomUser);//roomUser update privilege
                }
            }
        }
        roomMapper.updateById(targetRoom);//最后房间表 update

        return true;
    }

    //根据代码获得房间
    public Room getRoomByRoomCode(String roomCode) {
        Room targetRoom = roomMapper.selectOne(new QueryWrapper<Room>()
                .eq("room_code", roomCode)
        );
        return targetRoom;
    }

    //用户是否已经有房间
    public Boolean userAlreadyInRoom(Integer userId) {
        Long count = roomUserMapper.selectCount(new QueryWrapper<RoomUser>()
                .eq("user_id", userId)
                .eq("is_active", true)
        );
        return count > 0;
    }

    //用户所在的房间信息
    public Room getUsersRoomInfoById(Integer userId) {
        RoomUser roomUser = roomUserMapper.selectOne(new QueryWrapper<RoomUser>()
            .eq("user_id", userId)
            .eq("is_active", true)
        );
        if (roomUser == null)
            return null;
        Room room = roomMapper.selectById(roomUser.getRoomId());
        return room;
    }
    
    //权限更新
    public Boolean setUserPermission(Integer userId, Integer roomId, Integer permission) {
        int i = roomUserMapper.update(new UpdateWrapper<RoomUser>()
            .eq("user_id",userId)
            .eq("room_id",roomId)
            .set("privilege", permission)
        );
        return i > 0;
    }



}