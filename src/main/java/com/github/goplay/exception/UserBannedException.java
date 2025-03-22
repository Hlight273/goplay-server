package com.github.goplay.exception;

public class UserBannedException extends RuntimeException{
    public UserBannedException() {
        super("您的用户已被封禁！");
    }
}
