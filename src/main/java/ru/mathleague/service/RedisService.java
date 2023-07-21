package ru.mathleague.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


/*
Service for save config using redis
 */
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveConfig(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getConfig(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
