package com.wdy.module.shiro;

import com.wdy.module.entity.*;
import com.wdy.module.service.UserService;
import com.wdy.module.serviceUtil.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken userpasswordToken = (UsernamePasswordToken) token;
        String username = userpasswordToken.getUsername();
        User user = ((UserService) SpringContextUtil.getBean("UserService")).findByName(username);
        if (user == null)
            throw new AuthenticationException("用户名或者密码错误");
        else if (user.getStatus() == 0)
            throw new AuthenticationException("用户已经被禁用");
        else if (user.getActivateStatus() == 0)
            throw new AuthenticationException("用户未激活");
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, user.getPasswd(),
                //得到加密密码的盐值
                ByteSource.Util.bytes(user.getName()), getName());
        return authenticationInfo;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 获取用户角色
        List<Role> roles = user.getRoleList();
        Set<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());
        info.setRoles(roleNames);
        // 获取用户权限
        List<Permission> permissions = new ArrayList<>();
        for (Role role : roles)
            permissions.addAll(role.getPermissions());
        Set<String> permissionNames = permissions.stream().filter(p -> !StringUtils.isEmpty(p.getName()))
                .map(Permission::getName).collect(Collectors.toSet());
        info.addStringPermissions(permissionNames);
        return info;
    }
}
