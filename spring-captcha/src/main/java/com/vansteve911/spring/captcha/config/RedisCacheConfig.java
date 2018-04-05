package com.vansteve911.spring.captcha.config;

import com.vansteve911.spring.captcha.dto.CaptchaCode;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by vansteve911
 */
public class RedisCacheConfig {

    @Bean
    public RedisTemplate<String, CaptchaCode> captchaRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, CaptchaCode> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
