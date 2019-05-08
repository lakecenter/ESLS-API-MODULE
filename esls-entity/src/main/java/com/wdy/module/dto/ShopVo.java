package com.wdy.module.dto;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class ShopVo {
    private long id;
    private byte type;
    private String number;
    private String fatherShop;
    private String name;
    private String manager;
    private String address;
    private String account;
    private String phone;
    private List<Long> routerIds = new ArrayList();
    private List<Long> userIds = new ArrayList();
}
