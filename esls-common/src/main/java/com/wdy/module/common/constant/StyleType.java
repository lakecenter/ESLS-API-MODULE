package com.wdy.module.common.constant;

import java.util.HashMap;
import java.util.Map;

public class StyleType {
    public static Integer StyleType_21 = 0;
    public static Integer StyleType_29 = 1;
    public static Integer StyleType_42 = 2;
    public static Map<String, String> keyToWHMap = new HashMap();
    static {
        keyToWHMap.put("21","212 104");
        keyToWHMap.put("25","250 122");
        keyToWHMap.put("29","296 128");
        keyToWHMap.put("42","400 300");
    }
}
