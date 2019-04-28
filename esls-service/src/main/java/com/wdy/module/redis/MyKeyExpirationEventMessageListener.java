package com.wdy.module.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
public class MyKeyExpirationEventMessageListener extends KeyExpirationEventMessageListener {

    public MyKeyExpirationEventMessageListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info(new String(message.getBody()) + "已经过期！");
        //  SecurityUtils.getSubject().logout();
        super.onMessage(message, pattern);
    }
}

