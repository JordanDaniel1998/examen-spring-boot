package com.examen.security.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void saveToRedis(String key, String value, int expire){
        stringRedisTemplate.opsForValue().set(key, value);
        stringRedisTemplate.expire(key, expire, TimeUnit.MINUTES);
    }

    public String getDataFromRedis(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void deleteDataFromRedis(String key){
        stringRedisTemplate.delete(key);
    }
}
