package com.wdy.module.utils;

public class ErrorUtil {

    public static Boolean isErrorCommunication(String resultString) {
        if (resultString.contains("超时") || resultString.contains("失败"))
            return true;
        return false;
    }
}
