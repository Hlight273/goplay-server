package com.github.goplay.utils;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.User;
import com.github.goplay.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserUtils {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String getAvatar(){
        return "https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132";
    }

    public static boolean hasPlaylistPermission_by_userId(Playlist playlist, User user){
        return playlist.getUserId()==user.getId() || user.getLevel()>=UserLevel.MANAGER;
    }
    public static boolean hasPlaylistPermission_by_userId(Playlist playlist, UserInfo user){
        return playlist.getUserId()==user.getId() || user.getLevel()>=UserLevel.MANAGER;
    }

    public static boolean canCheckFullPlaylistInfo(Integer playlistOwnerId, Integer requesterId, UserService userService){
        UserInfo userInfo = userService.getUserInfoById(requesterId);
        if (userInfo==null)
            return false;
        Integer requesterLevel = userInfo.getLevel();
        return playlistOwnerId==playlistOwnerId || requesterLevel >= UserLevel.MANAGER;
    }

    // 加密密码
    public static String encryptPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    // 校验密码
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
