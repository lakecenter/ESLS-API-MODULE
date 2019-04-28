package com.wdy.module.utils;


import com.wdy.module.entity.User;

/**
 * 访问用户信息
 *
 * @author dongyang_wu
 * @date 2019/4/12 12:22
 */
public class ContextUtil {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ContextUtil.user = user;
    }
}
