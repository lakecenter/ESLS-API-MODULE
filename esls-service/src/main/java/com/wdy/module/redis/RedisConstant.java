package com.wdy.module.redis;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisConstant {
    // 缓存key
    public static final String CACHE_PRODUCTS = "cache_products";
    public static final String CACHE_USERS = "cache_users";
    public static final String CACHE_TAGS = "cache_tags";
    public static final String CACHE_GOODS = "cache_goods";
    public static final String CACHE_STYLES = "cache_styles";
    public static final String CACHE_SCANS = "cache_scans";
    public static final String CACHE_LOGS = "cache_logs";
    public static final String CACHE_ROUTERS = "cache_routers";
    public static final String CACHE_DISPMS = "cache_dispms";
    public static final String CACHE_DISPMMANAGERS = "cache_dispmmanagers";
    public static final String CACHE_SHOPS = "cache_shops";
    // 缓存时间
    public static final Long CACHE_PRODUCTS_SECOND = 80L;
    // 根据key设定具体的缓存时间
    private Map<String, Long> expiresMap = null;

    @PostConstruct
    public void init(){
        expiresMap = new HashMap<>();
        expiresMap.put(CACHE_PRODUCTS, CACHE_PRODUCTS_SECOND);
        expiresMap.put(CACHE_TAGS, CACHE_PRODUCTS_SECOND);
    }
    public Map<String, Long> getExpiresMap(){
        return this.expiresMap;
    }
}
