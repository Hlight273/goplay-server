package com.github.goplay.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public String processMessageFromClient(String message) throws Exception {
        // 接收到客户端消息后，将其发送到 /topic/messages
        return "服务器收到: " + message;
    }
}
