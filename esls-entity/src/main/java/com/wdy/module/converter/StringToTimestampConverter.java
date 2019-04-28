package com.wdy.module.converter;

import com.github.crab2died.converter.ReadConvertible;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;

/**
 * @program: esls-parent
 * @description: Excel导入String转Timestamp转换器
 * @author: dongyang_wu
 * @create: 2019-04-26 21:21
 */
public class StringToTimestampConverter implements ReadConvertible {
    @Override
    public Object execRead(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        else
        return Timestamp.valueOf(s);
    }
}