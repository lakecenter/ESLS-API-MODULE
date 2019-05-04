package com.wdy.module.config;

import com.wdy.module.license.LicenseCheckInterceptor;
import com.wdy.module.shiro.CurrentUserMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

//使用WebMvcConfigurerAdapter可以来扩展SpringMVC的功能
//@EnableWebMvc   不要接管SpringMVC
// @Configuration 申明这是一个配置类相当于xml配置文件，@Bean表示这是一个Spring管理的bean
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("upload");
//        registry.addViewController("/register.html").setViewName("register");
//        registry.addViewController("/tables.html").setViewName("tables");
    }

    @Bean
    public LicenseCheckInterceptor licenseCheckInterceptor() {
        return new LicenseCheckInterceptor();
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //super.addInterceptors(registry);
        //静态资源；  *.css , *.js
        //SpringBoot已经做好了静态资源映射
        registry.addInterceptor(licenseCheckInterceptor())
                .excludePathPatterns("/index.html", "/", "/user/login", "/asserts/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/license/installLicense", "/license/getServerInfos", "/license/generateLicense", "/license/downloadLicense")
                .addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/asserts/**")
                .addResourceLocations("classpath:/static/asserts");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentResolver());
    }


    @Bean
    public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new CurrentUserMethodArgumentResolver();
    }
}
