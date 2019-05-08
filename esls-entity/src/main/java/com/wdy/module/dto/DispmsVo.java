package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DispmsVo {
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "样式名字", order = 2)
    private String name;
    @ExcelField(title = "x", order = 3, readConverter = StringToIntegerConverter.class)
    private Integer x;
    @ExcelField(title = "y", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer y;
    @ExcelField(title = "宽", order = 5, readConverter = StringToIntegerConverter.class)
    private Integer width;
    @ExcelField(title = "高", order = 6, readConverter = StringToIntegerConverter.class)
    private Integer height;
    @ExcelField(title = "对应商品列", order = 7)
    private String sourceColumn;
    @ExcelField(title = "区域类型", order = 8)
    private String columnType;
    @ExcelField(title = "背景色", order = 9, readConverter = StringToIntegerConverter.class)
    private Integer backgroundColor;
    @ExcelField(title = "文字内容", order = 10)
    private String text;
    @ExcelField(title = "文字前缀", order = 11)
    private String startText;
    @ExcelField(title = "文字后缀", order = 12)
    private String endText;
    @ExcelField(title = "字体风格", order = 13)
    private String fontType;
    @ExcelField(title = "字体系列", order = 14)
    private String fontFamily;
    @ExcelField(title = "字体颜色", order = 15, readConverter = StringToIntegerConverter.class)
    private Integer fontColor;
    @ExcelField(title = "字体大小", order = 16, readConverter = StringToIntegerConverter.class)
    private Integer fontSize;
    @ExcelField(title = "是否显示", order = 17, readConverter = StringToByteConverter.class)
    private Byte status;
    @ExcelField(title = "图片URL", order = 18)
    private String imageUrl;
    @ExcelField(title = "备用字段", order = 19)
    private String backup;
    @ExcelField(title = "区域编号", order = 20, readConverter = StringToLongConverter.class)
    private Long regionId;
    @ExcelField(title = "样式外键ID", order = 21, readConverter = StringToLongConverter.class)
    private Long styleId;
}
