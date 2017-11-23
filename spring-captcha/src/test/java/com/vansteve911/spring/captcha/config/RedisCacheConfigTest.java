package com.vansteve911.spring.captcha.config;

import com.vansteve911.spring.captcha.dto.CaptchaCode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.Assert.*;

public class RedisCacheConfigTest {

     @Autowired
    private RedisTemplate<String, CaptchaCode> redisTemplate;

    @Test
    public void captchaRedisTemplate() throws Exception {
        String k = "a";
        String v = "b";
        CaptchaCode code = new CaptchaCode(k, v, 123L);
        redisTemplate.opsForValue().set(k, code);
        CaptchaCode cachedCode = redisTemplate.opsForValue().get(k);
        assertEquals(code, cachedCode);
    }

}