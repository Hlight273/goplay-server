package com.github.goplay.interceptor;
import lombok.extern.slf4j.Slf4j;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.User;
import com.github.goplay.exception.TokenValidationException;
import com.github.goplay.exception.UserBannedException;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.replayatk.tolerance}")
    private long tolerance;
    private final UserService userService;


    public UserInterceptor(UserService userService, StringRedisTemplate stringRedisTemplate) {
        this.userService = userService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //处理预请求的代码
        if (request.getMethod().equals("OPTIONS")) {
            response.setHeader("Access-Control-Allow-Origin", "*");//*表示放行所有的源
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String token = request.getHeader("token");

        if (StringUtils.isBlank(token)) {
            throw new TokenValidationException(token, "无token,请重新登陆");
        }
        try {
            Integer targetUserId = JwtUtils.getUserIdFromToken(token);
            UserInfo targetUser = userService.getUserInfoById(targetUserId);
            if (targetUser==null) {
                throw new TokenValidationException(token, "用户不存在");//用户是否存在
            }
            else if(targetUser.getIsActive().equals(0)){//用户封禁
                throw new UserBannedException();
            }

            processUserDailyHPoint(targetUserId);//用户每日积分检查

            return true;
        } catch (TokenValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenValidationException(token, "token失效");
        }
    }




    private static final int DAILY_POINTS = 2; //每日奖励积分数2

    private void processUserDailyHPoint(Integer userId) {
        String key = "daily_points:" + userId;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.with(LocalTime.MAX);
        long secondsUntilEndOfDay = ChronoUnit.SECONDS.between(now, endOfDay);

        boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", secondsUntilEndOfDay, TimeUnit.SECONDS);//需要保证原子操作 用redis分布式锁写入

        if (success) {
            //log.info("为用户[{}]添加{}积分", userId, DAILY_POINTS);
            userService.updateUserHPoints(userId, DAILY_POINTS);
            //log.info("用户[{}]积分添加完成", userId);
        } else {
            //log.info("用户[{}]今日已领取积分", userId);
        }
    }
}
