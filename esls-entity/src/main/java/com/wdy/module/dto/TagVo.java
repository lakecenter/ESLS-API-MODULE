package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class TagVo {
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "电量", order = 2)
    private String power;
    @ExcelField(title = "标签Rssi", order = 3)
    private String tagRssi;
    @ExcelField(title = "路由器Rssi", order = 4)
    private String apRssi;
    @ExcelField(title = "绑定状态", order = 5, readConverter = StringToByteConverter.class)
    private Byte state;
    @ExcelField(title = "硬件版本号", order = 6)
    private String hardwareVersion;
    @ExcelField(title = "软件版本号", order = 7)
    private String softwareVersion;
    @ExcelField(title = "禁用状态", order = 8, readConverter = StringToIntegerConverter.class)
    private Integer forbidState;
    @ExcelField(title = "是否等待更新", order = 9, readConverter = StringToIntegerConverter.class)
    private Integer waitUpdate;
    @ExcelField(title = "最近命令执行时间", order = 10, readConverter = StringToIntegerConverter.class)
    private Integer execTime;
    @ExcelField(title = "最近命令完成时间", order = 11, readConverter = StringToTimestampConverter.class)
    private Timestamp completeTime;
    @ExcelField(title = "条形码", order = 12)
    private String barCode;
    @ExcelField(title = "标签地址", order = 13)
    private String tagAddress;
    @ExcelField(title = "屏幕类型", order = 14)
    private String screenType;
    @ExcelField(title = "分辨率宽", order = 15)
    private String resolutionWidth;
    @ExcelField(title = "分辨率高", order = 16)
    private String resolutionHeight;
    @ExcelField(title = "巡检标志", order = 17, readConverter = StringToByteConverter.class)
    private Byte isWorking;
    @ExcelField(title = "外键商品ID", order = 18, readConverter = StringToLongConverter.class)
    private Long goodId;
    @ExcelField(title = "外键样式ID", order = 19, readConverter = StringToLongConverter.class)
    private Long styleId;
    @ExcelField(title = "外键路由ID", order = 20, readConverter = StringToLongConverter.class)
    private Long routerId;
}
