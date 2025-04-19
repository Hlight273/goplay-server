package com.github.goplay.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.AllowedListDeserializingMessageConverter;

import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "music.share.queue";
    public static final String EXCHANGE_NAME = "music.share.exchange";
    public static final String ROUTING_KEY = "music.share.routing";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); //durable持久化
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    public SimpleMessageConverter messageConverter() {
        AllowedListDeserializingMessageConverter converter = new SimpleMessageConverter(); // 继承自它
        converter.addAllowedListPatterns("com.github.goplay.dto.*");
        return (SimpleMessageConverter) converter;
    }
}