package com.wdy.module.common.response;

import com.wdy.module.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class SuccessAndFailList {
    private Integer successNumber ;
    private List<Tag> noSuccessTags;
    private List<Tag> successTags;
    public SuccessAndFailList(Integer successNumber,List<Tag> notSuccessTags,List<Tag> successTags){
        this.successNumber = successNumber;
        this.noSuccessTags = notSuccessTags;
        this.successTags = successTags;
    }
}
