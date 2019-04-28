package com.wdy.module.rabbitMq;

import com.wdy.module.serviceUtil.SendCommandUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {
    @RabbitListener(queues = "userQueue")
    public void process(RabbiMqSendBean rabbiMqSendBean){
        SendCommandUtil.updateTagStyle(rabbiMqSendBean.getTags(),rabbiMqSendBean.getIsWaiting(),false);
        System.out.println(rabbiMqSendBean.getTags().size()+" "+rabbiMqSendBean.getIsWaiting());
    }
}
