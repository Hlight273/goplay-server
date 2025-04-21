package com.github.goplay.messageQueue;

import com.github.goplay.config.RabbitMQConfig;
import com.github.goplay.dto.newDTO.MusicShareMessage;
import com.github.goplay.service.MusicShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareMessageReceiver {

    private final MusicShareService musicShareService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receive(MusicShareMessage message) {
        musicShareService.handleIncomingMessage(message);
    }
}