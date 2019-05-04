package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class UserVo {
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "name", order = 2)
    private String name;
    @ExcelField(title = "passwd", order = 3)
    private String passwd;
    @ExcelField(title = "rawPasswd", order = 4)
    private String rawPasswd;
    @ExcelField(title = "telephone", order = 5)
    private String telephone;
    @ExcelField(title = "address", order = 6)
    private String address;
    @ExcelField(title = "department", order = 7)
    private String department;
    @ExcelField(title = "createTime", order = 8, readConverter = StringToTimestampConverter.class)
    private Timestamp createTime;
    @ExcelField(title = "lastLoginTime", order = 9, readConverter = StringToTimestampConverter.class)
    private Timestamp lastLoginTime;
    @ExcelField(title = "status", order = 10, readConverter = StringToByteConverter.class)
    private Byte status;
    @ExcelField(title = "activateStatus", order = 11, readConverter = StringToByteConverter.class)
    private Byte activateStatus;
    @ExcelField(title = "mail", order = 12)
    private String mail;
    @ExcelField(title = "avatarUrl", order = 13)
    private String avatarUrl;
    @ExcelField(title = "shopId", order = 14, readConverter = StringToLongConverter.class)
    private Long shopId;
    private String roleList;
}
