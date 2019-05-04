package com.wdy.module.serviceUtil;


import com.wdy.module.entity.User;
import com.wdy.module.utils.RedisUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 访问用户信息
 *
 * @author dongyang_wu
 * @date 2019/4/12 12:22
 */
public class ContextUtil {
    public static String AUTHORIZATION = "ESLS";
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        ContextUtil.user = user;
    }

    public static User getUserByToken(HttpServletRequest request) {
        String id = request.getHeader(AUTHORIZATION);
        RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
        return (User) redisUtil.get(id);
    }
}
