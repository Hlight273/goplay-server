package com.github.goplay.controller;

import com.github.goplay.cache.PlayerOperationCache;
import com.github.goplay.dto.*;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.Room;
import com.github.goplay.event.EventType;
import com.github.goplay.event.RoomUpdateEvent;
import com.github.goplay.mapper.PlaylistSongMapper;
import com.github.goplay.service.*;
import com.github.goplay.utils.*;
import com.github.goplay.utils.Data.PrivilegeCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
public class RoomController {

    //private final WebSocketSessionRegistry sessionRegistry;
    private final ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private PlaylistSongService playlistSongService;
    @Autowired
    private PlaylistSongMapper playlistSongMapper;
    @Autowired
    private PlayerOperationCache playerOperationCache;

    public RoomController(/*WebSocketSessionRegistry sessionRegistry,*/ ApplicationEventPublisher eventPublisher) {
        //this.sessionRegistry = sessionRegistry;
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

    @Transactional
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
            eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_USER_LIST));
            //http响应
            return Result.ok()
                    .oData(room)
                    .message("加入成功");
        }else {
            return Result.error().message("加入失败");
        }
    }

    @Transactional
    @DeleteMapping("/{roomCode}/user/{userId}")
    public Result RoomExit(@PathVariable String roomCode, @PathVariable Integer userId){
        boolean exitSuccessful = roomService.deleteUserFromRoom(userId,roomCode);
        if(exitSuccessful){
            Room room = roomService.getRoomByRoomCode(roomCode);
            eventPublisher.publishEvent(new RoomUpdateEvent(this, room.getId(), EventType.ROOM_USER_LIST));
            return Result.ok().message("退出成功");
        }else {
            return Result.error().message("退出失败");
        }
    }

    @Transactional
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
    @Transactional
    @PostMapping("/{roomCode}/song/{songId}/remove")
    public Result RoomSongRemove(@PathVariable String roomCode, @PathVariable Integer songId, @RequestBody UserId userId){
        Room room = roomService.getRoomByRoomCode(roomCode);
        if (room == null) {
            return Result.error()
                    .message("房间不存在！");
        }
        Integer privilege = roomUserService.get_RoomUserInfo_By2Id(room.getId(), userId.getUserId()).getPrivilege();
        if(privilege> PrivilegeCode.ADMIN){
            return Result.error()
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

    @Transactional
    @PostMapping("/{roomCode}/saveAsPlaylist")
    public Result saveRoomSongsAsPlaylist(@RequestHeader("token") String token, @PathVariable String roomCode) {
        // 1. 验证发消息的用户是否是房主
        Integer requesterId = JwtUtils.getUserIdFromToken(token);
        UserInfo requesterInfo = userService.getUserInfoById(requesterId);
        Room room = roomService.getRoomByRoomCode(roomCode);
        Integer roomId = room.getId();
        if (room == null) {
            return Result.error().message("房间不存在！");
        }
        if(roomUserService.get_RoomUserInfo_By2Id(roomId, requesterId).getPrivilege() > PrivilegeCode.ROOM_OWNER){
            return Result.error().message("权限不足，只有房主可以进行此操作");
        }
        // 2. 获取房间所有歌曲
        List<SongContent> songContentListInRoom = roomSongService.getSongContentListInRoom(roomId);
        if(songContentListInRoom == null||songContentListInRoom.size() == 0){
            return Result.error().message("房间内暂无歌曲！");
        }
        List<Integer> songIds = songContentListInRoom.stream().map(songContent-> songContent.getSongInfo().getId()).collect(Collectors.toList());
        // 3. 创建新歌单
        int playlistId = playlistService.addPlaylist(Playlist.newPlaylistByRoom(requesterId, requesterInfo.getNickname()));
        if(playlistId == -1) return Result.error().message("歌单创建失败！");
        // 4. 复制歌曲到新歌单
        int success = playlistSongMapper.insertSongsIntoPlaylist(playlistId, songIds, requesterId, requesterInfo.getNickname());
        // 5. 返回成功消息
        if(success != -1)
            return Result.ok().message("歌单已创建！");
        else
            return Result.error().message("歌单创建失败！");
    }

    //转发客户端消息并广播
    //转发聊天室消息并广播到该房间
    @MessageMapping("/{roomId}/{userId}/say")
    @SendTo("/topic/{roomId}/receive")
    public RoomMsg HandleUserMsg(@DestinationVariable("roomId") Integer roomId,
                                 @DestinationVariable("userId") Integer userId,
                                 @Payload String message) {
        System.out.println("房间id"+roomId+",用户id"+userId+":"+message+",准备转发");
        return new RoomMsg(userService.getUserInfoById(userId), CommonUtils.curTime(), /*sessionRegistry.getTargetSession(roomId, userId)+" "+*/message);
    }



    //转发管理员点歌状态并广播到该房间 (需要检查管理员权限)
    @MessageMapping("/{roomId}/{userId}/change/playerStatus")
    @SendTo("/topic/{roomId}/playerData")
    public PlayerData HandlePlayerStatusOld(@DestinationVariable("roomId") Integer roomId,
                                   @DestinationVariable("userId") Integer userId,
                                   @Payload PlayerData playerData) {
        // 检查是否是1秒内的重复操作
        if (!playerOperationCache.isOperationAllowed(roomId, userId, playerData)) {
            return null;
        }
        Integer privilege = roomUserService.get_RoomUserInfo_By2Id(roomId, userId).getPrivilege();
        if(privilege> PrivilegeCode.ADMIN)
            return null;
        System.out.println("房间id"+roomId+",用户id"+userId+":"+playerData+",准备转发");
        return playerData;
    }
}
