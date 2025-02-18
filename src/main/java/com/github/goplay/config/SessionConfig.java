package com.github.goplay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession // 启用 Redis 来存储 Session
public class SessionConfig {
    // 默认情况下 Spring Session 会将会话存储在 Redis 中
}