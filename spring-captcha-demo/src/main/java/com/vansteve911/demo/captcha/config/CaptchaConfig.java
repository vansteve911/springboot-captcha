package com.vansteve911.demo.captcha.config;

import com.vansteve911.spring.captcha.cache.CacheProvider;
import com.vansteve911.spring.captcha.cache.impl.LocalCacheProvider;
import com.vansteve911.spring.captcha.cache.impl.RedisCacheProvider;
import com.vansteve911.spring.captcha.common.CaptchaType;
import com.vansteve911.spring.captcha.common.DefaultFactoryRegistry;
import com.vansteve911.spring.captcha.common.FactoryRegistry;
import com.vansteve911.spring.captcha.config.DefaultCaptchaConfig;
import com.vansteve911.spring.captcha.config.RedisCacheConfig;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import com.vansteve911.spring.captcha.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by vansteve911 on 2018/3/30.
 */
@Configuration
@Import({RedisCacheConfig.class})
public class CaptchaConfig extends DefaultCaptchaConfig {

    @Bean
    public CaptchaService captchaService() {
        return new CaptchaService();
    }

    @Autowired
    private RedisTemplate<String, CaptchaCode> redisTemplate;

    @Override
    @Bean
    public FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> cacheProviderFactory() {
        return new DefaultFactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>>()
                .registerFactory(CaptchaType.MOBILE,
                        new RedisCacheProvider<>(mobileCaptchaProperties.getExpireSeconds(), redisTemplate))
                .registerFactory(CaptchaType.IMG, new LocalCacheProvider<>(imgCaptchaProperties.getExpireSeconds(),
                        imgCaptchaProperties.getMaxCacheSize()));
    }
}
