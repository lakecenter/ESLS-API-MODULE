package com.wdy.module.utils;


public class ConditionUtil {
    // [不带条件] 查询   [全部] [不分页]   (4者均为空)
    public static String QUERY_ALL = "query_all";
    // [不带条件]  查询  [ 全部 ][ 分页 ]         (提供page和count字段)
    public static String QUERY_ALL_PAGE = "query_all_page";

    // [ 带条件 ]   查询 [ 指定属性 ][ 不分页 ]（提供query和queryString）
    public static String QUERY_ATTRIBUTE_ALL = "query_attribute_all";
    // [ 带条件 ]   查询 [ 指定属性 ] [ 分页 ]（提供4者字段）
    public static String QUERY_ATTRIBUTE_PAGE = "query_attribute_page";

    public static String judgeArgument(String query, String queryString, Integer page, Integer count) {
        // 参数不合规范
        if ((query == null && queryString != null) || (query != null && queryString == null) || (page == null && count != null) || (page != null && count == null))
            return null;
        if (query == null && queryString == null && page == null && count == null)
            return QUERY_ALL;
        if (query == null && queryString == null && page >= 0 && count >= 0)
            return QUERY_ALL_PAGE;
        if (query != null && queryString != null && page == null && count == null)
            return QUERY_ATTRIBUTE_ALL;
        if (query != null && queryString != null && page >= 0 && count >= 0)
            return QUERY_ATTRIBUTE_PAGE;
        return null;
    }
}