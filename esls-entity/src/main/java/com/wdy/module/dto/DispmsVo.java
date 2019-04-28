package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DispmsVo {
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "name", order = 2)
    private String name;
    @ExcelField(title = "x", order = 3, readConverter = StringToIntegerConverter.class)
    private Integer x;
    @ExcelField(title = "y", order = 4, readConverter = StringToIntegerConverter.class)
    private Integer y;
    @ExcelField(title = "width", order = 5, readConverter = StringToIntegerConverter.class)
    private Integer width;
    @ExcelField(title = "height", order = 6, readConverter = StringToIntegerConverter.class)
    private Integer height;
    @ExcelField(title = "sourceColumn", order = 7)
    private String sourceColumn;
    @ExcelField(title = "columnType", order = 8)
    private String columnType;
    @ExcelField(title = "backgroundColor", order = 9, readConverter = StringToIntegerConverter.class)
    private Integer backgroundColor;
    @ExcelField(title = "text", order = 10)
    private String text;
    @ExcelField(title = "startText", order = 11)
    private String startText;
    @ExcelField(title = "endText", order = 12)
    private String endText;
    @ExcelField(title = "fontType", order = 13)
    private String fontType;
    @ExcelField(title = "fontFamily", order = 14)
    private String fontFamily;
    @ExcelField(title = "fontColor", order = 15, readConverter = StringToIntegerConverter.class)
    private Integer fontColor;
    @ExcelField(title = "fontSize", order = 16, readConverter = StringToIntegerConverter.class)
    private Integer fontSize;
    @ExcelField(title = "status", order = 17, readConverter = StringToByteConverter.class)
    private Byte status;
    @ExcelField(title = "imageUrl", order = 18)
    private String imageUrl;
    @ExcelField(title = "backup", order = 19)
    private String backup;
    @ExcelField(title = "regionId", order = 20)
    private String regionId;
    @ExcelField(title = "styleId", order = 21, readConverter = StringToLongConverter.class)
    private Long styleId;
}
