package com.github.goplay.controller;

import com.github.goplay.dto.PlaylistInfo;
import com.github.goplay.dto.UserInfo;
import com.github.goplay.dto.VipInfo;
import com.github.goplay.entity.Room;
import com.github.goplay.entity.User;
import com.github.goplay.service.PlaylistService;
import com.github.goplay.service.RoomService;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import com.github.goplay.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

import static com.github.goplay.utils.UserUtils.canCheckFullPlaylistInfo;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RoomService roomService;
    private final PlaylistService playlistService;

    public UserController(UserService userService, RoomService roomService, PlaylistService playlistService) {
        this.userService = userService;
        this.roomService = roomService;
        this.playlistService = playlistService;
    }

    //登录需要传入用户名和密文密码
    @PostMapping("/login")
    public Result login(@RequestBody User user){

        User targetUser = userService.getUserByLoginInfo(user);
        if (targetUser == null) {
            return Result.error().message("用户名或密码不正确");
        }else{
            String token = JwtUtils.generateToken( targetUser.getId(), user.getUsername());
            return Result.ok()
                    .data("userid",targetUser.getId())
                    .data("username",targetUser.getUsername())
                    .data("token",token)
                    .message("登录成功");
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user){
        if(userService.getUserByUsername(user.getUsername()) != null){
            return Result.error().message("用户名已存在！");
        }
        int i = userService.createUser(user);
        if(i>0){
            return Result.ok().message("注册成功");
        }else {
            return Result.error().message("注册失败");
        }
    }

    @PostMapping("/logout")
    public Result logout(){
        return Result.ok().message("登出成功");
    }

    @GetMapping("/{userId}/room")
    public Result usersRoomInfo(@PathVariable Integer userId){
        Room room = roomService.getUsersRoomInfoById(userId);
        if(room != null){
            return Result.ok()
                    .oData(room)
                    .message("用户已加入房间");
        }else {
            return Result.empty()
                    .message("用户未加入房间！");
        }
    }

    @GetMapping("/{userId}/info")
    public Result userInfo(@PathVariable Integer userId){
        UserInfo userinfo = userService.getUserInfoById(userId);
        if(userinfo != null){
            return Result.ok()
                    .oData(userinfo)
                    .message("查询成功");
        }else {
            return Result.empty()
                    .message("查询为空！");
        }
    }

    @GetMapping("/{userId}/vipInfo")
    public Result userVipInfo(@PathVariable Integer userId){
        VipInfo vipInfo = userService.getVipInfoByUserId(userId);
        if(vipInfo != null){
            return Result.ok()
                    .oData(vipInfo)
                    .message("查询成功");
        }else {
            return Result.empty()
                    .message("查询为空！");
        }
    }

    @PutMapping("/nickname")
    public Result updateNickname(@RequestHeader("token") String token, @RequestParam("newNickname") String newNickname){
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        if(userService.updateUserNickname(requestUserId, newNickname)){
            return Result.ok().oData(true).message("昵称修改成功！");
        }else{
            return Result.error().message("昵称修改失败！");
        }
    }

    @PutMapping("/pwd")
    public Result updatePwd(@RequestHeader("token") String token, @RequestParam("oldPwd") String oldPwd, @RequestParam("newPwd") String newPwd){
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        if(!userService.verifiedPwd(oldPwd, requestUserId)){
            return Result.error().message("密码错误！");
        }
        if(userService.updateUserPwd(requestUserId, newPwd)){
            return Result.ok().oData(true).message("密码修改成功！");
        }else{
            return Result.error().message("密码修改失败！");
        }
    }

    @Transactional
    @PostMapping("/{userId}/renew/vipInfo")
    public Result renewVipInfo(@PathVariable Integer userId, int vipLevel, Timestamp startTime, int validDays){
        //假设已经经过支付系统鉴权
        boolean renewSuccess = userService.renewUserVipInfo(userId,vipLevel, startTime, validDays);
        if(renewSuccess){
            VipInfo targetVipInfo = userService.getVipInfoByUserId(userId);
            if(targetVipInfo!=null){
                return Result.ok()
                        .oData(targetVipInfo)
                        .message("获得vip成功");
            }
        }
        return Result.error()
                .message("获取vip失败！");
    }

    ///灵活查询，根据对比token的用户id和请求查询歌单拥有者id，判断是给全部歌单还是仅公开歌单，(负责人以上依旧所有歌单)
    @Transactional
    @GetMapping("/{playlistOwnerId}/playlist/info")
    public Result userPlaylistInfo(@RequestHeader("token") String token, @PathVariable Integer playlistOwnerId){
        List<PlaylistInfo> playlistInfos = null;
        Integer requestUserId = JwtUtils.getUserIdFromToken(token);
        if(canCheckFullPlaylistInfo(playlistOwnerId, requestUserId, userService)){
            playlistInfos = playlistService.get_PlaylistInfoList_ByOwnerId(playlistOwnerId);
        }else{
            playlistInfos = playlistService.get_PublicPlaylistInfoList_ByOwnerId(playlistOwnerId);
        }
        if(playlistInfos!=null && playlistInfos.size()>0){
            return Result.ok()
                    .oData(playlistInfos)
                    .message("查询用户歌单成功");
        }
        return Result.empty()
                .message("查询用户歌单为空！");
    }
}
