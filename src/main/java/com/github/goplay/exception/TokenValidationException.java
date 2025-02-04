package com.github.goplay.exception;

public class TokenValidationException extends Exception {
    private String token;
    private String message;

    public TokenValidationException(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getMessage() {
        return message;
    }
}