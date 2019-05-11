package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import com.wdy.module.converter.StringToTimestampConverter;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "systemversion", schema = "tags", catalog = "")
@ToString
public class SystemVersion implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "idOrGenerate")
    @GenericGenerator(name = "idOrGenerate", strategy = "com.wdy.module.serviceUtil.IdOrGenerate")
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "softVersion")
    @ExcelField(title = "管理系统软件版本号", order = 2)
    private String softVersion;
    @Column(name = "productor")
    @ExcelField(title = "开发者", order = 3)
    private String productor;
    @Column(name = "date")
    @ExcelField(title = "开发日期", order = 4, readConverter = StringToTimestampConverter.class)
    private Timestamp date;
    @Column(name = "tokenAliveTime")
    @ExcelField(title = "Token有效时间", order = 5)
    private String tokenAliveTime;
    @Column(name = "commandRepeatTime")
    @ExcelField(title = "命令重发次数", order = 6)
    private String commandRepeatTime;
    @Column(name = "packageLength")
    @ExcelField(title = "命令包大小", order = 7)
    private String packageLength;
    @Column(name = "commandWaitingTime")
    @ExcelField(title = "命令等待时间", order = 8)
    private String commandWaitingTime;
    @Column(name = "outNetIp")
    @ExcelField(title = "服务器公网IP", order = 9)
    private String outNetIp;
    @Column(name = "recursionDepth")
    @ExcelField(title = "递归层数", order = 10)
    private String recursionDepth;
    @Column(name = "timeGapAndTime")
    @ExcelField(title = "发送样式间隔时间及次数", order = 11)
    private String timeGapAndTime;
    @Column(name = "basePermissions")
    @ExcelField(title = "基础权限集合", order = 12)
    private String basePermissions;
    @Column(name = "tagsLengthCommand")
    @ExcelField(title = "标签唤醒量", order = 13)
    private String tagsLengthCommand;
    @ExcelField(title = "商品导入导出数据格式", order = 14)
    private String goodDataFormat;
}
