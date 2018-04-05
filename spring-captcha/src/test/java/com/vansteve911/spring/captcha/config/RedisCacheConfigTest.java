package com.vansteve911.spring.captcha.config;

import com.vansteve911.spring.captcha.TestAppContext;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import static com.vansteve911.spring.captcha.common.TestUtils.getPrivateFieldValue;
import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@Import(RedisCacheConfig.class)
@ContextConfiguration(classes = TestAppContext.class)
public class RedisCacheConfigTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Test
    public void checkConnectionConfig() {
        assertNotNull(connectionFactory);
        RedisStandaloneConfiguration config = (RedisStandaloneConfiguration) getPrivateFieldValue(connectionFactory, "standaloneConfig");
        assertTrue(config.getHostName().equals("localhost")
                && config.getPort() == 6379);
    }
}