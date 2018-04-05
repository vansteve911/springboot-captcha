package com.vansteve911.spring.captcha.config;

import com.vansteve911.spring.captcha.TestAppContext;
import com.vansteve911.spring.captcha.cache.CacheProvider;
import com.vansteve911.spring.captcha.common.CaptchaException;
import com.vansteve911.spring.captcha.common.CaptchaType;
import com.vansteve911.spring.captcha.common.FactoryRegistry;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import com.vansteve911.spring.captcha.generator.CaptchaCodeGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static com.vansteve911.spring.captcha.common.CaptchaException.Type.GENERATE_FAILED;
import static com.vansteve911.spring.captcha.common.CaptchaException.Type.GENERATE_TOO_FREQUENTLY;
import static com.vansteve911.spring.captcha.common.CaptchaException.Type.SEND_FAILED;
import static org.junit.Assert.*;

/**
 * Created by vansteve911 on 18/4/6.
 */
@RunWith(SpringRunner.class)
@Import(DefaultCaptchaConfig.class)
@PropertySource("application.properties")
@ContextConfiguration(classes = TestAppContext.class)
public class DefaultCaptchaConfigTest {

    @Autowired
    private FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> cacheProviderFactory;

    @Autowired
    private FactoryRegistry<CaptchaType, CaptchaCodeGenerator> captchaGeneratorFactory;

    @Test
    public void autowiringTest() {
        assertNotNull(cacheProviderFactory);
        assertNotNull(captchaGeneratorFactory);
    }

    @Test
    public void loadExceptionMsg() {
        DefaultCaptchaConfig.loadExceptionMsg("src/test/resources/exception_msg.json");
        assertTrue(checkTypeMsg(GENERATE_FAILED, "验证码生成失败") &&
                checkTypeMsg(GENERATE_TOO_FREQUENTLY, "请勿频繁请求验证码") &&
                checkTypeMsg(SEND_FAILED, "验证码发送失败, 请稍后再试"));
    }

    private boolean checkTypeMsg(CaptchaException.Type type, String msg) {
        return CaptchaException.typeMsg(type).equals(msg);
    }

}