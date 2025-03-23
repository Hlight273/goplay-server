package com.github.goplay.config;

import com.github.goplay.websocket.WebSocketSessionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //private final WebSocketSessionRegistry webSocketSessionRegistry;

    public WebSocketConfig(/*WebSocketSessionRegistry webSocketSessionRegistry*/) {
        //this.webSocketSessionRegistry = webSocketSessionRegistry;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue","/topic"); // 使用内存中的简单代理来广播消息
        config.setApplicationDestinationPrefixes("/app"); // 客户端发送消息到服务器的前缀
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 STOMP 端点，并启用 SockJS 支持
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }



}