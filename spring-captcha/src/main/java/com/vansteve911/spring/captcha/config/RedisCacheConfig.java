package com.vansteve911.spring.captcha.config;

import com.vansteve911.spring.captcha.dto.CaptchaCode;
import org.springframework.context.annotation.Bean;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
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
        template.setValueSerializer(new RedisSerializer<CaptchaCode>() {
            private SerializingConverter serializer = new SerializingConverter();
            private DeserializingConverter deserializer = new DeserializingConverter();

            @Override
            public byte[] serialize(CaptchaCode captchaCode) throws SerializationException {
                return serializer.convert(captchaCode);
            }

            @Override
            public CaptchaCode deserialize(byte[] bytes) throws SerializationException {
                return (CaptchaCode) deserializer.convert(bytes);
            }
        });
        return template;
    }
}
