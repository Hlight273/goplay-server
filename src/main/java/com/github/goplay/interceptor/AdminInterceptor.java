package com.github.goplay.interceptor;

import com.github.goplay.dto.UserInfo;
import com.github.goplay.exception.AdminAuthorizationException;
import com.github.goplay.service.UserService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.UserLevel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public AdminInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        Integer requesterId = JwtUtils.getUserIdFromToken(token);
        UserInfo requester = userService.getUserInfoById(requesterId);
        if (requesterId == null || requester.getLevel() < UserLevel.ADMIN) {
            throw new AdminAuthorizationException("无管理员权限");
        }
        return true;
    }
}
