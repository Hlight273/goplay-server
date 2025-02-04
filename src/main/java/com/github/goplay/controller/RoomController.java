package com.github.goplay.controller;

import com.github.goplay.dto.RoomMsg;
import com.github.goplay.dto.SongContent;
import com.github.goplay.dto.UserId;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.Room;
import com.github.goplay.service.RoomService;
import com.github.goplay.service.RoomSongService;
import com.github.goplay.service.RoomUserService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.CommonUtils;
import com.github.goplay.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
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
            Send_UserInfoList_to_Room(room.getId());
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
            Send_UserInfoList_to_Room(room.getId());
            return Result.ok().message("退出成功");
        }else {
            return Result.error().message("退出失败");
        }
    }

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
            Send_UserInfoList_to_Room(room.getId());
            return Result.ok()
                    .message("权限更新成功");
        }else {
            return Result.error()
                    .message("权限更新失败！");
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
            Send_UserInfoList_to_Room(room.getId());
            return Result.ok()
                    .message("权限移交成功");
        }else {
            return Result.error()
                    .message("权限移交失败！");
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

    @MessageMapping("/{roomId}/{userId}/say")
    @SendTo("/topic/{roomId}/receive")
    public RoomMsg HandleUserMsg(@DestinationVariable("roomId") Integer roomId,
                                 @DestinationVariable("userId") Integer userId,
                                 @Payload String message) {
        System.out.println("房间id"+roomId+",用户id"+userId+":"+message+",准备转发");
        return new RoomMsg(userService.getUserInfoById(userId), CommonUtils.curTime(), message);
    }

    private void Send_UserInfoList_to_Room(Integer roomId){
        List<UserInfo> userInfoList = roomUserService.getUserInfoListInRoom(roomId);
        messagingTemplate.convertAndSend("/topic/"+roomId+"/userInfoList", userInfoList);
    }


}
