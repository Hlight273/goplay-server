package com.github.goplay.interceptor;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.entity.User;
import com.github.goplay.exception.TokenValidationException;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserInterceptor implements HandlerInterceptor {

    @Value("${jwt.replayatk.tolerance}")
    private long tolerance;
    private final UserService userService;

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
                throw new TokenValidationException(token, "用户不存在");
            }
            return true;
        } catch (TokenValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenValidationException(token, "token失效");
        }
    }


    public UserInterceptor(UserService userService) {
        this.userService = userService;
    }
}
