package com.wdy.module.common.constant;

import java.util.HashMap;
import java.util.Map;

public class FileConstant {
    public static String GOODS_MESSAGE = "goods_csv/";
    public static String GOODS_CHANGE_MESSAGE= "goods_change_csv/";
    public static Map ModeMap = new HashMap<Integer,String>();
    static {
        ModeMap.put(0,GOODS_MESSAGE);
        ModeMap.put(1,GOODS_CHANGE_MESSAGE);
    }
}
