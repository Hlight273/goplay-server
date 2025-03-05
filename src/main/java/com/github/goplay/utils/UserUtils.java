package com.github.goplay.utils;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.Playlist;
import com.github.goplay.entity.User;

public class UserUtils {
    public static String getAvatar(){
        return "https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132";
    }

    public static boolean hasPlaylistPermission_by_userId(Playlist playlist, User user){
        return playlist.getUserId().equals(user.getId()) || user.getLevel()>=UserLevel.MANAGER;
    }
    public static boolean hasPlaylistPermission_by_userId(Playlist playlist, UserInfo user){
        return playlist.getUserId().equals(user.getId()) || user.getLevel()>=UserLevel.MANAGER;
    }
}
