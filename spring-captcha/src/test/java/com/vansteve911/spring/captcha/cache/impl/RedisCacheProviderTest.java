package com.vansteve911.spring.captcha.cache.impl;

import com.vansteve911.spring.captcha.TestAppContext;
import com.vansteve911.spring.captcha.config.RedisCacheConfig;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by vansteve911 on 18/4/6.
 */
@RunWith(SpringRunner.class)
@Import(RedisCacheConfig.class)
@ContextConfiguration(classes = TestAppContext.class)
public class RedisCacheProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheProviderTest.class);

    @Autowired
    private RedisTemplate<String, CaptchaCode> redisTemplate;

    private static final int EXPIRE_SECONDS = 2;

    @Test
    public void tests() throws Exception {
        if (!isRedisConnected()) {
            logger.warn("redis not connected, tests will be passed.");
            return;
        }
        RedisCacheProvider<CaptchaCode> cacheProvider = new RedisCacheProvider<>(EXPIRE_SECONDS, redisTemplate);
        String k = "__TEST_KEY__" + System.currentTimeMillis();
        String v = "b";
        CaptchaCode code = new CaptchaCode(k, v, System.currentTimeMillis());
        try {
            cacheProvider.put(k, code);
            CaptchaCode cachedCode = cacheProvider.get(k);
            assertEquals(code, cachedCode);
            // delete test
            cacheProvider.delete(k);
            assertNull(cacheProvider.get(k));
            // expire test
            cacheProvider.put(k, code);
            Thread.sleep((EXPIRE_SECONDS + 1) * 1000);
            assertNull(cacheProvider.get(k));
        } finally {
            redisTemplate.delete(k);
        }
    }

    private boolean isRedisConnected() {
        try {
            redisTemplate.getConnectionFactory().getConnection();
            return true;
        } catch (Exception e) {
            logger.warn("failed to connect redis");
        }
        return false;
    }
}