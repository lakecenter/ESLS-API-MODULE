package com.wdy.module.shiro;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Map;

/* *
 * @Author dongyang_wu
 * @Description shiro配置
 * @Date 12:41 2018/3/6
 */
@Configuration
public class ShiroConfig {
    @Autowired
    private ShiroService shiroService;
    @Bean("ShiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        RestShiroFilterFactoryBean shiroFilterFactoryBean = new RestShiroFilterFactoryBean();
        //用户未登录不进行跳转，而是自定义返回json数据
        //获取filters
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("authc", new ShiroLoginFilter());
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> filterRuleMap  = shiroService.loadFilterChainDefinitions();
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterRuleMap);
        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthenticator(authenticator());
        //定义session管理
        securityManager.setSessionManager(sessionManager());
        securityManager.setRealm(ShiroRealm());
        return securityManager;
    }
    public Realm ShiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return shiroRealm;
    }
    @Bean
    public Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        //设置两个Realm，一个用于用户登录验证和访问权限获取；一个用于jwt token的认证
//        authenticator.setRealms(Arrays.asList(jwtShiroRealm(), dbShiroRealm()));
        //设置多个realm认证策略，一个成功即跳过其它的
       // authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }
    @Bean
    public SessionManager sessionManager(){
        ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
        //这里可以不设置。Shiro有默认的session管理。如果缓存为Redis则需改用Redis的管理
        shiroSessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());
        return shiroSessionManager;
    }
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //散列的次数，比如散列两次，相当于 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(3);
        return hashedCredentialsMatcher;
    }
    // 功能:开启shiro aop注解支持.
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
