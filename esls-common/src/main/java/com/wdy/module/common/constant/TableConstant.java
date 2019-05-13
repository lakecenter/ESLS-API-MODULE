package com.wdy.module.common.constant;


import java.util.HashMap;
import java.util.Map;

public class TableConstant {
    public final static String TABLE_USER = "t_user";
    public final static String TABLE_ROUTERS = "routers";
    public final static String TABLE_TAGS = "tags";
    public final static String TABLE_DISPMS = "dispms";
    public final static String TABLE_SHOPS = "shops";
    public final static String TABLE_LOGS = "operation_log";
    public final static String TABLE_STYLE = "styles";
    public final static String TABLE_GOODS = "goods";
    public final static String TABLE_ROLES = "role";
    public final static String TABLE_PERMISSION = "permission";
    public final static String TABLE_USER_ROLE = "user_role";
    public final static String TABLE_ROLE_PERMISSION = "role_permission";
    public final static String TABLE_CYCLEJOBS = "cyclejobs";
    public final static String TABLE_BALANCE = "balance";
    public static Map<String, String> entityNameToTableName = new HashMap<>();

    static {
        entityNameToTableName.put("Good", TABLE_GOODS);
        entityNameToTableName.put("Tag", TABLE_TAGS);
        entityNameToTableName.put("Style", TABLE_STYLE);
        entityNameToTableName.put("Router", TABLE_ROUTERS);
        entityNameToTableName.put("Role", TABLE_ROLES);
        entityNameToTableName.put("Permission", TABLE_PERMISSION);
        entityNameToTableName.put("Dispms", TABLE_DISPMS);
        entityNameToTableName.put("User", TABLE_USER);
        entityNameToTableName.put("Shop", TABLE_SHOPS);
        entityNameToTableName.put("OperationLog", TABLE_LOGS);
        entityNameToTableName.put("CycleJob", TABLE_CYCLEJOBS);
        entityNameToTableName.put("RolePermission",TABLE_ROLE_PERMISSION );
        entityNameToTableName.put("UserRole", TABLE_USER_ROLE);
        entityNameToTableName.put("Balance", TABLE_BALANCE);
    }
}
