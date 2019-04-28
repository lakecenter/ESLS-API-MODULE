package com.wdy.module.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: esls-parent
 * @description: 反射操作基本方法
 * @author: dongyang_wu
 * @create: 2019-04-26 20:58
 */
public class ReflectUtil {
    // 获取指定对象的指定属性值
    public static String getSourceData(String name, Object source) {
        String sourceData = null;
        try {
            Field field = source.getClass().getDeclaredField(name);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            sourceData = field.get(source).toString();
        } catch (Exception e) {
        }
        return sourceData;
    }

    /**
     * 获取属性名数组
     */
    public static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * 功能描述：设置实体属性
     * 参数 : obj 实体对象  | attrName 属性字段名   | attrValue 属性字段值
     */
    public static void setFiledAttrValue(Object obj, String attrName, Object attrValue) throws Exception {
        Class clazz = obj.getClass();
        Field field = clazz.getDeclaredField(attrName);
        field.setAccessible(true);
        field.set(obj, attrValue);
    }

    /**
     * @Description: 反射获取属性类型
     * @Param:
     * @return:
     * @Author: dongyang_wu
     * @date: 2019/4/27
     */
    public static List<String> getFiledType(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field f : fields) {
            result.add(f.getGenericType().getTypeName());
        }
        return result;
    }
}