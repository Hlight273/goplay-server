package com.github.goplay.exception;

import com.github.goplay.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenValidationException.class)
    public Result handleTokenValidationException(TokenValidationException e) {
        return Result.expired().message("token验证失败").data("token", e.getToken()).data("errorMessage", e.getMessage());
    }

    // 其他异常处理方法...

}
