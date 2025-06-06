package com.github.goplay.utils;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.User;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.Data.PrivilegeCode;
import com.github.goplay.utils.Data.UserLevel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Objects;

public class UserUtils {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String getAvatar(){
        return "https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132";
    }

    public static boolean hasPlaylistPermission_by_userId(Playlist playlist, User user){
        return Objects.equals(playlist.getUserId(), user.getId()) || user.getLevel()>= UserLevel.MANAGER;
    }
    public static boolean hasPlaylistPermission_by_userId(Playlist playlist, UserInfo user){
        return Objects.equals(playlist.getUserId(), user.getId()) || user.getLevel()>=UserLevel.MANAGER;
    }

    public static boolean canCheckFullPlaylistInfo(Integer playlistOwnerId, Integer requesterId, UserService userService){
        UserInfo userInfo = userService.getUserInfoById(requesterId);
        if (userInfo==null)
            return false;
        Integer requesterLevel = userInfo.getLevel();
        return Objects.equals(requesterId, playlistOwnerId) || requesterLevel >= UserLevel.MANAGER;
    }

    public static boolean canKick(int requesterPrivilege, int targetPrivilege) {
        if (requesterPrivilege == PrivilegeCode.ROOM_OWNER) {
            return true; // 房主可以踢任何人
        }
        if (requesterPrivilege == PrivilegeCode.ADMIN && targetPrivilege == PrivilegeCode.MEMBER) {
            return true; // 管理员只能踢普通成员
        }
        return false; // 其他情况都不行，比如普通成员踢人，管理员踢房主
    }

    // 加密密码
    public static String encryptPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    // 校验密码
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    ///一元=100积分 (先简化)
    public static Integer calculatePoints(BigDecimal amount){
        return (amount.intValue()*100);
    }

    public static Integer PointsToLevel(Integer points){
        if(points>=12800){
            return 3;
        }else if(points>=3000){
            return 2;
        }else if(points>=600){
            return 1;
        }
        return 0;
    }
}
