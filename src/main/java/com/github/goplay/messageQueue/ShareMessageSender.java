package com.github.goplay.messageQueue;

import com.github.goplay.config.RabbitMQConfig;
import com.github.goplay.dto.newDTO.MusicShareMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareMessageSender {
    private final RabbitTemplate rabbitTemplate;

    public void sendShareMessage(MusicShareMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}
