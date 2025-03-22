package com.github.goplay.exception;

import com.github.goplay.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenValidationException.class)
    public Result handleTokenValidationException(TokenValidationException e) {
        return Result.expired().message(e.getMessage()).data("token", e.getToken());
    }

    @ExceptionHandler(AdminAuthorizationException.class)
    public Result handleAdminAuthorizationException(AdminAuthorizationException e) {
        return Result.error().message(e.getMessage());
    }

    @ExceptionHandler(UserBannedException.class)
    public Result handleUserBannedException(UserBannedException e) {
        return Result.error().message(e.getMessage());
    }

    // 其他异常处理方法...

}
