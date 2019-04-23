package com.xyj.api.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列 RabbitMQ
 */
@Configuration
public class RabbitConfig {
    @Bean
    public Queue message1() {
        return new Queue("message1", true);
    }

    @Bean
    public Queue message2() {
        return new Queue("message2", true);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("wgnEx");
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }
}
