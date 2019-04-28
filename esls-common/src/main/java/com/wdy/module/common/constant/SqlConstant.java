package com.wdy.module.common.constant;


import com.wdy.module.common.request.RequestBean;
import com.wdy.module.common.request.RequestItem;

import java.util.*;

public class SqlConstant {

    public static String SELECT_QUERY = "select * from ";
    public static String QUERY_TABLIE_COLUMN = "SELECT column_name FROM information_schema.Columns WHERE table_name= ";
    public static String QUERY_ALL_TABLE = "SELECT table_name FROM information_schema.tables WHERE  table_schema= 'tags' ";

    public static String getQuerySql(String table, String query, String connection, String queryString) {
        if (query != null && query.contains(" ")) {
            StringBuffer sql = new StringBuffer();
            sql.append(SELECT_QUERY)
                    .append(table)
                    .append(" where ");
            int i = 0;
            if (connection.equals("like"))
                queryString = "\'%" + queryString + "%\'";
            String[] querys = query.split(" ");
            for (; i < querys.length - 1; i++) {
                sql.append(querys[i])
                        .append(" " + connection + " ")
                        .append(queryString + " or ");
            }
            sql.append(querys[i])
                    .append(" " + connection + " ")
                    .append(queryString);
            return sql.toString();
        }
        StringBuffer sql = new StringBuffer().append(SELECT_QUERY)
                .append(table)
                .append(" where ")
                .append(query + " ")
                .append(connection + " ");
        if ("is".equalsIgnoreCase(connection))
            sql.append(queryString);
        else
            sql.append("\'" + queryString + "\'");
        return sql.toString();
    }

    public static String getQuerySql(String table, String connection, RequestBean requestBean) {
        StringBuffer sql = new StringBuffer().append(SELECT_QUERY)
                .append(table)
                .append(" where ");
        List<RequestItem> items = requestBean.getItems();
        int i = 0;
        for (; i < items.size() - 1; i++) {
            RequestItem requestItem = items.get(i);
            String queryString = requestItem.getQueryString();
            if (connection.equals("like"))
                queryString = "\'%" + requestItem.getQueryString() + "%\'";
            sql.append(requestItem.getQuery()).append(" " + connection + " ").append(queryString).append(" and ");
        }
        RequestItem requestItem = items.get(i);
        String queryString = requestItem.getQueryString();
        if (connection.equals("like"))
            queryString = "\'%" + requestItem.getQueryString() + "%\'";
        sql.append(requestItem.getQuery()).append(" " + connection + " ").append(queryString);
        return sql.toString();
    }

    public static String getUpdateSql(String table, RequestBean source, RequestBean target) {
        StringBuffer sql = new StringBuffer();
        sql.append("update ").append(table + " set ");
        List<RequestItem> items = target.getItems();
        RequestItem item;
        int i = 0;
        for (; i < items.size() - 1; i++) {
            item = items.get(i);
            sql.append(item.getQuery() + "=").append("\'" + item.getQueryString() + "\' ,");
        }
        sql.append(items.get(i).getQuery() + "=").append("\'" + items.get(i).getQueryString()).append("\' where ");
        items = source.getItems();
        for (i = 0; i < items.size() - 1; i++) {
            item = items.get(i);
            sql.append(item.getQuery() + "=").append("\'" + item.getQueryString() + "\'" + " and ");
        }
        sql.append(items.get(i).getQuery() + "=").append("\'" + items.get(i).getQueryString() + "\'");
        return sql.toString();
    }

    public static String getDeleteSql(String table, String query, List<Long> idList) {
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ")
                .append(table)
                .append(" where ")
                .append(query)
                .append(" in ( ");
        int i = 0;
        for (; i < idList.size() - 1; i++) {
            sql.append("\'" + idList.get(i) + "\' , ");
        }
        sql.append("\'" + idList.get(i) + "\')");
        return sql.toString();
    }

    public static Map<String, String> EntityToSqlMap = new HashMap();
    public static List<String> needVoEntity;
    public static List<String> needIgnoreEntity;

    static {
        EntityToSqlMap.put("goods", "Good");
        EntityToSqlMap.put("tags", "Tag");
        EntityToSqlMap.put("routers", "Router");
        EntityToSqlMap.put("logs", "Logs");
        EntityToSqlMap.put("T_User", "User");
        EntityToSqlMap.put("dispms", "Dispms");
        EntityToSqlMap.put("cyclejobs", "CycleJob");
        EntityToSqlMap.put("permission", "Permission");
        EntityToSqlMap.put("role", "Role");
        EntityToSqlMap.put("role_permission", "RolePermission");
        EntityToSqlMap.put("shops", "Shop");
        EntityToSqlMap.put("styles", "Style");
        EntityToSqlMap.put("systemversion", "SystemVersion");
        EntityToSqlMap.put("balance", "Balance");
        EntityToSqlMap.put("user_role", "UserRole");
        EntityToSqlMap.put("sms_verify", "SmsVerify");
        EntityToSqlMap.put("operation_log", "OperationLog");
        EntityToSqlMap.put("user_thirdparty", "UserThirdparty");
        needVoEntity = Arrays.asList("T_User", "routers", "balance", "dispms", "tags");
        needIgnoreEntity = Arrays.asList("user_thirdparty");
    }
}
