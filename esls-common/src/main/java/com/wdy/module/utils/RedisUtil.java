package com.wdy.module.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.TimeUnit;

@Component("RedisUtil")
public class RedisUtil {
    //操作字符串的template，StringRedisTemplate是RedisTemplate的一个子集
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //RedisTemplate可以进行所有的操作
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    public Object sentinelGet(String key,Class clazz){
        String value =  this.stringRedisTemplate.opsForValue().get(key);
        return JSON.parseObject(value,clazz);
    }
    public boolean sentinelSet(final String key , Object value , Long expireTime) {
        boolean result ;
        ValueOperations<String, String> valueOperations = this.stringRedisTemplate.opsForValue();
        valueOperations.set(key, JSON.toJSONString(value));
        redisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
        result = true;
        return result;
    }
    public boolean isExist(String key){return this.stringRedisTemplate.hasKey(key);}
    public void del(String key){
        this.redisTemplate.delete(key);
    }
    // 获取指定的key过期时间
    public long getExpireByKey(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }
    // 判断key是否存在
    public boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 删除缓存  可多个
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    // 获取缓存值
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }
    // 存key
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 存key并设置时间
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
