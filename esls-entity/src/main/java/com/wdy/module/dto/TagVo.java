package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class TagVo {
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "power", order = 2)
    private String power;
    @ExcelField(title = "tagRssi", order = 3)
    private String tagRssi;
    @ExcelField(title = "apRssi", order = 4)
    private String apRssi;
    @ExcelField(title = "state", order = 5, readConverter = StringToByteConverter.class)
    private Byte state;
    @ExcelField(title = "hardwareVersion", order = 6)
    private String hardwareVersion;
    @ExcelField(title = "softwareVersion", order = 7)
    private String softwareVersion;
    @ExcelField(title = "forbidState", order = 8, readConverter = StringToIntegerConverter.class)
    private Integer forbidState;
    @ExcelField(title = "waitUpdate", order = 9, readConverter = StringToIntegerConverter.class)
    private Integer waitUpdate;
    @ExcelField(title = "execTime", order = 10, readConverter = StringToIntegerConverter.class)
    private Integer execTime;
    @ExcelField(title = "completeTime", order = 11, readConverter = StringToTimestampConverter.class)
    private Timestamp completeTime;
    @ExcelField(title = "barCode", order = 12)
    private String barCode;
    @ExcelField(title = "tagAddress", order = 13)
    private String tagAddress;
    @ExcelField(title = "screenType", order = 14)
    private String screenType;
    @ExcelField(title = "resolutionWidth", order = 15)
    private String resolutionWidth;
    @ExcelField(title = "resolutionHeight", order = 16)
    private String resolutionHeight;
    @ExcelField(title = "isWorking", order = 17, readConverter = StringToByteConverter.class)
    private Byte isWorking;
    @ExcelField(title = "goodId", order = 18, readConverter = StringToLongConverter.class)
    private Long goodId;
    @ExcelField(title = "styleId", order = 19, readConverter = StringToLongConverter.class)
    private Long styleId;
    @ExcelField(title = "routerId", order = 20, readConverter = StringToLongConverter.class)
    private Long routerId;
}
