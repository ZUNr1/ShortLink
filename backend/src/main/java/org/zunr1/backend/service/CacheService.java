package org.zunr1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void setString(String key, String value, long timeout, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value,timeout,timeUnit);
    }

    public String getString(String key){
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public Boolean setIfAbsent(String key,String value,long timeout,TimeUnit timeUnit){
        return redisTemplate.opsForValue().setIfAbsent(key,value,timeout,timeUnit);
    }

    public Boolean expire(String key,long timeout,TimeUnit timeUnit){
        return redisTemplate.expire(key,timeout,timeUnit);
    }
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }
    public Boolean delete(String key){
        return redisTemplate.delete(key);
    }
    public Long deleteBatch(Collection<String> keys){
        return redisTemplate.delete(keys);
    }
    public Boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

}
