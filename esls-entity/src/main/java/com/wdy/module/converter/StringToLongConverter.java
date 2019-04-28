package com.wdy.module.converter;

import com.github.crab2died.converter.ReadConvertible;
import org.springframework.util.StringUtils;

/**
 * @program: esls-parent
 * @description: 导入Excel String类型转Long类型
 * @author: dongyang_wu
 * @create: 2019-04-26 21:20
 */
public class StringToLongConverter implements ReadConvertible {
    @Override
    public Object execRead(String s) {
        if (StringUtils.isEmpty(s))
            return null;
        else
            return Long.valueOf(s);
    }
}