package com.github.goplay.controller;

import com.github.goplay.dto.*;
import com.github.goplay.entity.Room;
import com.github.goplay.event.EventType;
import com.github.goplay.event.RoomUpdateEvent;
import com.github.goplay.service.RoomService;
import com.github.goplay.service.RoomSongService;
import com.github.goplay.service.RoomUserService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.CommonUtils;
import com.github.goplay.utils.PrivilegeCode;
import com.github.goplay.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final ApplicationEventPublisher eventPublisher;
    public RoomController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomUserService roomUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private RoomSongService roomSongService;

    //增
    @PostMapping
    public Result RoomCreate(@RequestBody UserId userId){
        //先查询用户有没有在别的房间
        Boolean userHasRoom = roomService.userAlreadyInRoom(userId.getUserId());
        if(userHasRoom){
            return Result.error().message("您已经在房间里了！");
        }
        Room room = roomService.createRoom(userId.getUserId());
        if(room != null){
            return Result.ok()
                    .oData(room)
                    .message("创建成功");
        }else {
            return Result.error().message("创建失败");
        }
    }

    //查
    @GetMapping("/{roomCode}/userList")
    public Result RoomMember(@PathVariable String roomCode){
        Room room = roomService.getRoomByRoomCode(roomCode);
        if (room == null) {
            return Result.error()
                    .message("房间不存在！");
        }
        List<UserInfo> userInfoList = roomUserService.getUserInfoListInRoom(room.getId());
        if(userInfoList != null){
            return Result.ok()
                    .oData(userInfoList)
                    .message("查询成功");
        }else {
            return Result.error()
                    .message("查询失败！");
        }
    }

    @GetMapping("/{roomCode}/songList")
    public Result RoomSongContent(@PathVariable String roomCode){
        Room room = roomService.getRoomByRoomCode(roomCode);
        if (room == null) {
            return Result.error()
                    .message("房间不存在！");
        }
        List<SongContent> SongContentList = roomSongService.getSongContentListInRoom(room.getId());
        if(SongContentList != null){
            return Result.ok()
                    .oData(SongContentList)
                    .message("查询成功");
        }else {
            return Result.empty()
                    .message("歌曲列表为空！");
        }
    }


    //改:推送userlist
    @PostMapping("/{roomCode}/user/{userId}/privilege/{privilegeCode}")
    public Result RoomMemberPrivilege(@PathVariable String roomCode,@PathVariable Integer userId,@PathVariable Integer privilegeCode,
                                      @RequestParam Integer callerUserId){
        Room room = roomService.getRoomByRoomCode(roomCode);
        if (room == null) {
            return Result.error()
                    .message("房间不存在！");
        }
        Boolean setSuccess = userService.setUserPrivilegeInRoom(room.getId(), userId, callerUserId, privilegeCode);
        if(setSuccess){
            eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_USER_LIST));
            return Result.ok()
                    .message("权限更新成功");
        }else {
            return Result.error()
                    .message("权限更新失败！");
        }
    }

    @PostMapping("/{roomCode}/join")
    public Result RoomJoin(@PathVariable String roomCode, @RequestBody UserId userId){
        //先查询用户有没有在别的房间
        Boolean userHasRoom = roomService.userAlreadyInRoom(userId.getUserId());
        if(userHasRoom){
            return Result.error().message("您已经在房间里了！");
        }
        Room room = roomService.addUserToRoom(userId.getUserId(),roomCode);
        if(room!=null){
            //加入房间需要在房间广播
            eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_SONG_LIST));
            //http响应
            return Result.ok()
                    .oData(room)
                    .message("加入成功");
        }else {
            return Result.error().message("加入失败");
        }
    }

    @DeleteMapping("/{roomCode}/user/{userId}")
    public Result RoomExit(@PathVariable String roomCode, @PathVariable Integer userId){
        boolean exitSuccessful = roomService.deleteUserFromRoom(userId,roomCode);
        if(exitSuccessful){
            Room room = roomService.getRoomByRoomCode(roomCode);
            eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_SONG_LIST));
            return Result.ok().message("退出成功");
        }else {
            return Result.error().message("退出失败");
        }
    }

    @PostMapping("/{roomCode}/user/{userId}/owner/to/user/{targetUserId}")
    public Result RoomOwnerTransPrivilege(@PathVariable String roomCode,@PathVariable Integer userId, @PathVariable Integer targetUserId){
        Room room = roomService.getRoomByRoomCode(roomCode);
        if (room == null) {
            return Result.error()
                    .message("房间不存在！");
        }
        Boolean setSuccess = userService.setUserTransOwnerInRoom(room.getId(), targetUserId, userId);
        if(setSuccess){
            eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_USER_LIST));
            return Result.ok()
                    .message("权限移交成功");
        }else {
            return Result.error()
                    .message("权限移交失败！");
        }
    }


    //改:推送songlist
    @PostMapping("/{roomCode}/song/{songId}/remove")
    public Result RongSongRemove(@PathVariable String roomCode, @PathVariable Integer songId, @RequestBody UserId userId){
        Room room = roomService.getRoomByRoomCode(roomCode);
        if (room == null) {
            return Result.error()
                    .message("房间不存在！");
        }
        Integer privilege = roomUserService.get_RoomUserInfo_By2Id(room.getId(), userId.getUserId()).getPrivilege();
        if(privilege> PrivilegeCode.ADMIN){
            return Result.ok()
                    .message("权限不足");
        }
        boolean removeSuccess = roomSongService.removeSongInRoom(room.getId(), songId);
        if(removeSuccess){
            eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_SONG_LIST));
            return Result.ok()
                    .message("移除成功");
        }else {
            return Result.error()
                    .message("移除失败！");
        }
    }

    //转发客户端消息并广播
    //转发聊天室消息并广播到该房间
    @MessageMapping("/{roomId}/{userId}/say")
    @SendTo("/topic/{roomId}/receive")
    public RoomMsg HandleUserMsg(@DestinationVariable("roomId") Integer roomId,
                                 @DestinationVariable("userId") Integer userId,
                                 @Payload String message) {
        System.out.println("房间id"+roomId+",用户id"+userId+":"+message+",准备转发");
        return new RoomMsg(userService.getUserInfoById(userId), CommonUtils.curTime(), message);
    }
    //转发管理员点歌状态并广播到该房间 (需要检查管理员权限)
    @MessageMapping("/{roomId}/{userId}/change/playerStatus")
    @SendTo("/topic/{roomId}/playerData")
    public PlayerData HandlePlayerStatus(@DestinationVariable("roomId") Integer roomId,
                                 @DestinationVariable("userId") Integer userId,
                                 @Payload PlayerData playerData) {
        Integer privilege = roomUserService.get_RoomUserInfo_By2Id(roomId, userId).getPrivilege();
        if(privilege> PrivilegeCode.ADMIN)
            return null;
        System.out.println("房间id"+roomId+",用户id"+userId+":"+playerData+",准备转发");
        return playerData;
    }
}
