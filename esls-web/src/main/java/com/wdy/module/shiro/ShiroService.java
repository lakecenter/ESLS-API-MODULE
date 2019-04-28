package com.wdy.module.shiro;

import com.wdy.module.entity.Permission;
import com.wdy.module.service.PermissionService;
import com.wdy.module.serviceUtil.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ShiroService {
    @Autowired
    private PermissionService permissionService;

    /**
     * 初始化权限
     */
    public Map<String, String> loadFilterChainDefinitions() {
        // 权限控制map.从数据库获取
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 所有请求通过我们自己的JWT Filter
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/user/registry", "anon");
        filterChainDefinitionMap.put("/user/activate", "anon");
        filterChainDefinitionMap.put("/user/identifyCode", "anon");
        filterChainDefinitionMap.put("/user/sendIdentifyCode", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/font-awesome/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        List<Permission> permissions = permissionService.findAll();
        for (Permission item : permissions) {
            String permission = "perms[" + item.getName() + "]";
            filterChainDefinitionMap.put(item.getUrl(), permission);
        }
        filterChainDefinitionMap.put("/**", "authc");
        return filterChainDefinitionMap;
    }

    public void updatePermission() {
        try {
            ShiroFilterFactoryBean shiroFilterFactoryBean = (ShiroFilterFactoryBean) SpringContextUtil.getBean("ShiroFilterFactoryBean");
            synchronized (shiroFilterFactoryBean) {
                AbstractShiroFilter shiroFilter = null;
                try {
                    shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean
                            .getObject();
                } catch (Exception e) {
                    throw new RuntimeException(
                            "get ShiroFilter from shiroFilterFactoryBean error!");
                }
                PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                        .getFilterChainResolver();
                DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver
                        .getFilterChainManager();

                // 清空老的权限控制
                manager.getFilterChains().clear();
                shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
                shiroFilterFactoryBean
                        .setFilterChainDefinitionMap(loadFilterChainDefinitions());
                // 重新构建生成
                Map<String, String> chains = shiroFilterFactoryBean
                        .getFilterChainDefinitionMap();
                for (Map.Entry<String, String> entry : chains.entrySet()) {
                    String url = entry.getKey();
                    String chainDefinition = entry.getValue().trim()
                            .replace(" ", "");
                    manager.createChain(url, chainDefinition);
                }
                System.out.println("更新权限成功！！");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
