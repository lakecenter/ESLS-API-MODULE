package com.wdy.module.rabbitMq;

import com.wdy.module.entity.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RabbiMqSendBean implements Serializable {
    private List<Tag> tags;
    private Boolean isWaiting;
    public RabbiMqSendBean(){}
    public RabbiMqSendBean(List<Tag> tags,Boolean isWaiting){
        this.tags = tags;
        this.isWaiting = isWaiting;
    }
}
