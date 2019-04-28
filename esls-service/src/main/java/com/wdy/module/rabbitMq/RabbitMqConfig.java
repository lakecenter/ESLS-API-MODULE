package com.wdy.module.rabbitMq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue userQueue() {
        return new Queue("userQueue");
    }
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange("directExchange");
    }
    @Bean
    Binding bindingExchangeMessage(Queue userQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(userQueue).to(directExchange).with("TAG_STYLE_SEND");
    }
}
