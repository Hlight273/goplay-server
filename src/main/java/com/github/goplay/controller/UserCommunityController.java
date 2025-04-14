package com.github.goplay.controller;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.service.UserCommunityService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class UserCommunityController {

    private final UserCommunityService userCommunityService;

    public UserCommunityController(UserCommunityService userCommunityService) {
        this.userCommunityService = userCommunityService;
    }

    @GetMapping("/mySimilarUsers")
    public Result getRandomSimilarMbtiUsers(@RequestHeader("token") String token) {
        Integer userId = JwtUtils.getUserIdFromToken(token);
        List<UserInfo> userInfoList = userCommunityService.getRandomSimilarMbtiUsers(userId, 5, 3);
        if(userInfoList==null||userInfoList.isEmpty())
            return Result.empty().oData(userInfoList).message("暂未找到志趣相投的其他用户哦~");
        return Result.ok().oData(userInfoList).message("查询成功！");
    }
}
