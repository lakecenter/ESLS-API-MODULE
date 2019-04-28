package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class RouterVo {
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private long id;
    @ExcelField(title = "mac", order = 2)
    private String mac;
    @ExcelField(title = "ip", order = 3)
    private String ip;
    @ExcelField(title = "port", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer port;
    @ExcelField(title = "channelId", order = 5)
    private String channelId;
    @ExcelField(title = "state", order = 6, readConverter = StringToByteConverter.class)
    private Byte state;
    @ExcelField(title = "softVersion", order = 7)
    private String softVersion;
    @ExcelField(title = "frequency", order = 8)
    private String frequency;
    @ExcelField(title = "hardVersion", order = 9)
    private String hardVersion;
    @ExcelField(title = "execTime", order = 10, readConverter = StringToIntegerConverter.class)
    private Integer execTime;
    @ExcelField(title = "barCode", order = 11)
    private String barCode;
    @ExcelField(title = "isWorking", order = 12, readConverter = StringToByteConverter.class)
    private Byte isWorking;
    @ExcelField(title = "completeTime", order = 13, readConverter = StringToTimestampConverter.class)
    private Timestamp completeTime;
    @ExcelField(title = "outNetIp", order = 14)
    private String outNetIp;
    @ExcelField(title = "shopId", order = 15, readConverter = StringToLongConverter.class)
    private long shopId;
    private byte type;
    private String number;
    private String fatherShop;
    private String name;
    private String manager;
    private String address;
    private String account;
    private String password;
    private String phone;
}
