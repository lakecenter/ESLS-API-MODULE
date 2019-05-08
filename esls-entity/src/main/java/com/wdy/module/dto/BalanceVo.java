package com.wdy.module.dto;

import com.github.crab2died.annotation.ExcelField;
import com.wdy.module.converter.StringToByteConverter;
import com.wdy.module.converter.StringToLongConverter;
import lombok.Data;
import lombok.ToString;


/**
 * @program: esls-parent
 * @description:
 * @author: dongyang_wu
 * @create: 2019-04-27 13:57
 */
@Data
@ToString
public class BalanceVo {
    @ExcelField(title = "主键", order = 1, readConverter = StringToLongConverter.class)
    private Long id;
    @ExcelField(title = "重量", order = 2)
    private String weight;
    @ExcelField(title = "是否稳定", order = 3, readConverter = StringToByteConverter.class)
    private Byte steady;
    @ExcelField(title = "是否置零", order = 4, readConverter = StringToByteConverter.class)
    private Byte zero;
    @ExcelField(title = "是否超重", order = 5, readConverter = StringToByteConverter.class)
    private Byte overWeight;
    @ExcelField(title = "是否净重", order = 6, readConverter = StringToByteConverter.class)
    private Byte netWeight;
    @ExcelField(title = "电量整数位", order = 7)
    private String powerInterger;
    @ExcelField(title = "电量小数位", order = 8)
    private String powerDecimal;
    @ExcelField(title = "外键标签ID", order = 9, readConverter = StringToLongConverter.class)
    private Long tagId;
}