package com.vansteve911.spring.captcha.cache.impl;

import com.vansteve911.spring.captcha.cache.AbstractCacheProvider;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created by vansteve911
 */
public class RedisCacheProvider <T> extends AbstractCacheProvider<String, T> {

    private RedisTemplate<String, T> redisTemplate;

    public RedisCacheProvider(int expireSeconds, RedisTemplate<String, T> redisTemplate) {
        super(expireSeconds, Integer.MAX_VALUE);
        if (expireSeconds <= 0) {
            throw new IllegalArgumentException("illegal expireSeconds");
        }
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(String key, T value) {
        redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public T get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
