package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class RouterVo {
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private long id;
    @ExcelField(title = "Mac地址", order = 2)
    private String mac;
    @ExcelField(title = "Ip地址", order = 3)
    private String ip;
    @ExcelField(title = "端口号", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer port;
    @ExcelField(title = "信道号", order = 5)
    private String channelId;
    @ExcelField(title = "禁用状态", order = 6, readConverter = StringToByteConverter.class)
    private Byte state;
    @ExcelField(title = "软件版本号", order = 7)
    private String softVersion;
    @ExcelField(title = "频率", order = 8)
    private String frequency;
    @ExcelField(title = "硬件版本号", order = 9)
    private String hardVersion;
    @ExcelField(title = "最近命令执行时间", order = 10, readConverter = StringToIntegerConverter.class)
    private Integer execTime;
    @ExcelField(title = "条码", order = 11)
    private String barCode;
    @ExcelField(title = "巡检应答", order = 12, readConverter = StringToByteConverter.class)
    private Byte isWorking;
    @ExcelField(title = "最近命令完成时间", order = 13, readConverter = StringToTimestampConverter.class)
    private Timestamp completeTime;
    @ExcelField(title = "公网IP", order = 14)
    private String outNetIp;
    @ExcelField(title = "外键商店ID", order = 15, readConverter = StringToLongConverter.class)
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
