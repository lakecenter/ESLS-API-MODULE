package com.wdy.module.serviceUtil;


import com.wdy.module.entity.User;
import com.wdy.module.utils.RedisUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 访问用户信息
 *
 * @author dongyang_wu
 * @date 2019/4/12 12:22
 */
public class ContextUtil {
    public static String AUTHORIZATION = "ESLS";
    private static User user = new User("未携带token用户");

    public static User getUser() {
        RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader(AUTHORIZATION);
            if (!StringUtils.isEmpty(token)) {
                user = (User) redisUtil.sentinelGet(token, User.class);
            }
        } catch (Exception e) {
        }
        return user;
    }


    public static User getUserByToken(HttpServletRequest request) {
        String id = request.getHeader(AUTHORIZATION);
        RedisUtil redisUtil = (RedisUtil) SpringContextUtil.getBean("RedisUtil");
        return (User) redisUtil.get(id);
    }
}
