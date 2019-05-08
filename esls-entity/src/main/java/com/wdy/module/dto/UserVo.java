package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class UserVo {
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "用户名", order = 2)
    private String name;
    @ExcelField(title = "密码", order = 3)
    private String passwd;
    @ExcelField(title = "原密码", order = 4)
    private String rawPasswd;
    @ExcelField(title = "电话号码", order = 5)
    private String telephone;
    @ExcelField(title = "地址", order = 6)
    private String address;
    @ExcelField(title = "部门", order = 7)
    private String department;
    @ExcelField(title = "创建时间", order = 8, readConverter = StringToTimestampConverter.class)
    private Timestamp createTime;
    @ExcelField(title = "最后一次登录时间", order = 9, readConverter = StringToTimestampConverter.class)
    private Timestamp lastLoginTime;
    @ExcelField(title = "禁用状态", order = 10, readConverter = StringToByteConverter.class)
    private Byte status;
    @ExcelField(title = "激活状态", order = 11, readConverter = StringToByteConverter.class)
    private Byte activateStatus;
    @ExcelField(title = "邮件", order = 12)
    private String mail;
    @ExcelField(title = "用户头像URL", order = 13)
    private String avatarUrl;
    @ExcelField(title = "外键商店ID", order = 14, readConverter = StringToLongConverter.class)
    private Long shopId;
    private String roleList;
}
