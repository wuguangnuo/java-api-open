package com.xyj.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 接收 RabbitMQ 消息
 *
 * @author WuGuangNuo
 */
@Slf4j
@Component
public class RabbitMQ {
    @RabbitListener(queues = "message1")
    public void handle1(String body) {
        log.info("接收消息1：" + body);
    }

    @RabbitListener(queues = "message2")
    public void handle2(String body) {
        log.info("接收消息2：" + body);
    }
}
