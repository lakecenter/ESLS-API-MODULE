package com.wdy.module.dto;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class StyleVo {
    private long id;
    private String styleNumber;
    private String styleType;
    private String name;
    private Integer width;
    private Integer height;
    private Byte isPromote;
    private List<Long> tagIdList = new ArrayList<>();
}
