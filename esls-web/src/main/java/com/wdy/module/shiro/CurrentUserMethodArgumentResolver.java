package com.wdy.module.shiro;

import com.wdy.module.aop.CurrentUser;
import com.wdy.module.entity.User;
import com.wdy.module.service.UserService;
import com.wdy.module.serviceUtil.SpringContextUtil;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 增加方法注入，将含有 @CurrentUser 注解的方法参数注入当前登录用户
 *
 * @author dongyang_wu
 * @since 2018-05-03
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class)
                && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String userName = (String) webRequest.getAttribute("currentUser", RequestAttributes.SCOPE_REQUEST);
        if (userName == null) {
            throw new UnauthorizedException("获取用户信息失败");
        }
        UserService userService = (UserService) SpringContextUtil.getBean("UserService");
        return userService.findByName(userName);
    }
}
