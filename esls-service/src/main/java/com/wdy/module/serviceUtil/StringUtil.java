package com.wdy.module.serviceUtil;

import com.wdy.module.entity.Dispms;
import com.wdy.module.entity.Good;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class StringUtil {
    public static String NUMBER = "数字";
    public static String BACKGROUND = "背景";
    public static String PHOTO = "图片";
    public static String LINE = "线段";
    public static String Str = "字符串";
    public static String QRCODE = "二维码";
    public static String BARCODE = "条形码";

    public static boolean isEmpty(String value) {
        return StringUtils.isEmpty(value) || value.contains("null") || value == null;
    }

    public static String getRealString(Dispms dispM, Good good) {
        StringBuffer sb = new StringBuffer();
        if (!isEmpty(dispM.getStartText())) {
            sb.append(dispM.getStartText());
        }
        // 为与商品有关字段
        if (!dispM.getSourceColumn().equals("0")) {
            String text = SpringContextUtil.getSourceData(dispM.getSourceColumn(), good);
            if (NUMBER.equals(dispM.getColumnType())) {
                if (text.contains(".")) {
                    String right = text.substring(text.indexOf(".") + 1);
                    if (right.length() >= 2) {
                        text = text.substring(0, text.indexOf(".") + 3);
                    } else
                        text += "0";
                } else
                    text += ".00";
            }
            sb.append(text);
        }
        // 为与商品无关字段
        else if (!isEmpty(dispM.getText())) {
            sb.append(dispM.getText());
        }
        if (!isEmpty(dispM.getEndText())) {
            sb.append(dispM.getEndText());
        }
        return sb.toString();
    }

    /**
     * 获取方法中指定注解的value值返回
     *
     * @param method               方法名
     * @param validationParamValue 注解的类名
     * @return
     */
    public static String getMethodAnnotationOne(Method method, String validationParamValue) {
        String retParam = null;
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                String str = parameterAnnotations[i][j].toString();
                if (str.indexOf(validationParamValue) > 0) {
                    retParam = str.substring(str.indexOf("=") + 1, str.indexOf(")"));
                }
            }
        }
        return retParam;
    }

    //首字母大写
    public static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;

    }

    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
