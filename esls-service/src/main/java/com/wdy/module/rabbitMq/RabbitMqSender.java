package com.wdy.module.rabbitMq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    public void send(RabbiMqSendBean rabbiMqSendBean) {
        this.rabbitTemplate.convertAndSend("directExchange", "TAG_STYLE_SEND",rabbiMqSendBean);
    }
}
