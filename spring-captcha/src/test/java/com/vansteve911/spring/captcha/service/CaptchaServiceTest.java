package com.vansteve911.spring.captcha.service;

import com.vansteve911.spring.captcha.TestAppContext;
import com.vansteve911.spring.captcha.cache.CacheProvider;
import com.vansteve911.spring.captcha.common.CaptchaException;
import com.vansteve911.spring.captcha.common.CaptchaType;
import com.vansteve911.spring.captcha.common.DefaultFactoryRegistry;
import com.vansteve911.spring.captcha.common.FactoryRegistry;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import com.vansteve911.spring.captcha.generator.CaptchaCodeGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.vansteve911.spring.captcha.common.CaptchaException.Type.GENERATE_TOO_FREQUENTLY;
import static com.vansteve911.spring.captcha.common.CaptchaException.Type.SEND_FAILED;
import static com.vansteve911.spring.captcha.common.TestUtils.expectExceptionThrown;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Created by vansteve911 on 18/4/5.
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestAppContext.class)
public class CaptchaServiceTest {

    private static final String MOCK_CAPTCHA_PREFIX = "MOCK_";
    private static CaptchaType CAPTCHA_TYPE = CaptchaType.IMG;

    private CaptchaCodeGenerator<CaptchaCode> mockCaptchaGenerator = spy(mockCaptchaGenerator());

    private CacheProvider<String, CaptchaCode> mockCacheProvider = mockCacheProvider();

    @Spy
    private FactoryRegistry<CaptchaType, CaptchaCodeGenerator> captchaGeneratorFactory = captchaGeneratorFactory();

    @Spy
    private FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> cacheProviderFactory = cacheProviderFactory();

    @InjectMocks
    private CaptchaService captchaService;

    private Map<String, CaptchaCode> mockCache = new HashMap<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void clearCache() {
        mockCache.clear();
        System.out.println("cleared cache");
    }

    @Test
    public void genNewCodeTest() throws Exception {
        String key = "a";
        CaptchaCode code = captchaService.genCaptchaCode(key, CAPTCHA_TYPE);
        assertTrue("generate failed", isCorrectNewCode(code, key));
        mockCacheProvider.delete(key);
    }

    private boolean isCorrectNewCode(CaptchaCode code, String key) {
        return code != null && (MOCK_CAPTCHA_PREFIX + key).equals(code.getValue());
    }

    @Test
    public void genTooFrequentlyTest() {
        String key = "a";
        // make generator.checkGenerateTimeInterval always return true
        doReturn(true).when(mockCaptchaGenerator).checkGenerateTimeInterval(any());
        captchaService.genCaptchaCode(key, CAPTCHA_TYPE);
        CaptchaCode code = captchaService.genCaptchaCode(key, CAPTCHA_TYPE);
        assertTrue("is not valid code", isCorrectNewCode(code, key));
        // make generator.checkGenerateTimeInterval act normal
        doReturn(false).when(mockCaptchaGenerator).checkGenerateTimeInterval(any());
        expectExceptionThrown(thrown, CaptchaException.class, CaptchaException.typeMsg(GENERATE_TOO_FREQUENTLY));
        captchaService.genCaptchaCode(key, CAPTCHA_TYPE);
        mockCacheProvider.delete(key);
    }

    @Test
    public void validateCodeTest() throws Exception {
        String key = "a";
        captchaService.genCaptchaCode(key, CAPTCHA_TYPE);
        assertTrue("validate code failed", captchaService.validateCaptchaCode(key, MOCK_CAPTCHA_PREFIX + key, CAPTCHA_TYPE));
        // test for unsupported captcha code type
        thrown.expect(NullPointerException.class);
        captchaService.genCaptchaCode(key, CaptchaType.MOBILE);
    }

    @Test
    public void expireCaptchaCode() throws Exception {
        String key = "a";
        captchaService.genCaptchaCode(key, CAPTCHA_TYPE);
        captchaService.expireCaptchaCode(key, CAPTCHA_TYPE);
        assertNull("expire code failed", mockCache.get(key));
        // test for unsupported captcha code type
        boolean success = captchaService.expireCaptchaCode(key, CaptchaType.MOBILE);
        assertTrue("should fail when expire unsupported captcha type", !success);
    }

    private CacheProvider<String, CaptchaCode> mockCacheProvider() {
        return new CacheProvider<String, CaptchaCode>() {

            @Override
            public void put(String key, CaptchaCode value) {
                mockCache.put(key, value);
            }

            @Override
            public CaptchaCode get(String key) {
                return mockCache.get(key);
            }

            @Override
            public void delete(String key) {
                mockCache.remove(key);
            }
        };
    }

    private CaptchaCodeGenerator<CaptchaCode> mockCaptchaGenerator() {
        return new CaptchaCodeGenerator<CaptchaCode>() {
            @Override
            public CaptchaCode generateCaptchaCode(String key) {
                return new CaptchaCode(key, MOCK_CAPTCHA_PREFIX + key, System.currentTimeMillis());
            }

            @Override
            public boolean checkGenerateTimeInterval(CaptchaCode code) {
                return false;
            }
        };
    }

    private FactoryRegistry<CaptchaType, CaptchaCodeGenerator> captchaGeneratorFactory() {
        FactoryRegistry<CaptchaType, CaptchaCodeGenerator> factoryRegistry = new DefaultFactoryRegistry<>();
        return factoryRegistry.registerFactory(CAPTCHA_TYPE, mockCaptchaGenerator);
    }

    private FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> cacheProviderFactory() {
        FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> factoryRegistry = new DefaultFactoryRegistry<>();
        return factoryRegistry.registerFactory(CAPTCHA_TYPE, mockCacheProvider);
    }

}