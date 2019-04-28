package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToByteConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;


/**
 * @program: esls-parent
 * @description:
 * @author: dongyang_wu
 * @create: 2019-04-27 13:57
 */
@Data
@ToString
public class BalanceVo {
    @ExcelField(title = "id", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "weight", order = 2)
    private String weight;
    @ExcelField(title = "steady", order = 3, readConverter = StringToByteConverter.class)
    private Byte steady;
    @ExcelField(title = "zero", order = 4, readConverter = StringToByteConverter.class)
    private Byte zero;
    @ExcelField(title = "overWeight", order = 5, readConverter = StringToByteConverter.class)
    private Byte overWeight;
    @ExcelField(title = "netWeight", order = 6, readConverter = StringToByteConverter.class)
    private Byte netWeight;
    @ExcelField(title = "powerInterger", order = 7)
    private String powerInterger;
    @ExcelField(title = "powerDecimal", order = 8)
    private String powerDecimal;
    @ExcelField(title = "tagId", order = 9, readConverter = StringToLongConverter.class)
    private Long tagId;
}