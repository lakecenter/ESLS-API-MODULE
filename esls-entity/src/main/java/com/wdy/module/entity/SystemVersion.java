package com.wdy.module.entity;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToLongConverter;
import com.wdy.module.converter.StringToTimestampConverter;
import lombok.Data;
import lombok.ToString;

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
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @Column(name = "softVersion")
    @ExcelField(title = "softVersion", order = 2)
    private String softVersion;
    @Column(name = "productor")
    @ExcelField(title = "productor", order = 3)
    private String productor;
    @Column(name = "date")
    @ExcelField(title = "date", order = 4, readConverter = StringToTimestampConverter.class)
    private Timestamp date;
    @Column(name = "tokenAliveTime")
    @ExcelField(title = "tokenAliveTime", order = 5)
    private String tokenAliveTime;
    @Column(name = "commandRepeatTime")
    @ExcelField(title = "commandRepeatTime", order = 6)
    private String commandRepeatTime;
    @Column(name = "packageLength")
    @ExcelField(title = "packageLength", order = 7)
    private String packageLength;
    @Column(name = "commandWaitingTime")
    @ExcelField(title = "commandWaitingTime", order = 8)
    private String commandWaitingTime;
    @Column(name = "outNetIp")
    @ExcelField(title = "outNetIp", order = 9)
    private String outNetIp;
    @Column(name = "recursionDepth")
    @ExcelField(title = "recursionDepth", order = 10)
    private String recursionDepth;
    @Column(name = "timeGapAndTime")
    @ExcelField(title = "timeGapAndTime", order = 11)
    private String timeGapAndTime;
    @Column(name = "basePermissions")
    @ExcelField(title = "basePermissions", order = 12)
    private String basePermissions;
    @Column(name = "tagsLengthCommand")
    @ExcelField(title = "tagsLengthCommand", order = 13)
    private String tagsLengthCommand;
}
