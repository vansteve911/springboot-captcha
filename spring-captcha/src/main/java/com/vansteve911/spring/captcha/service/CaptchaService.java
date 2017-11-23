package com.vansteve911.spring.captcha.service;

import com.vansteve911.spring.captcha.cache.CacheProvider;
import com.vansteve911.spring.captcha.common.CaptchaException;
import com.vansteve911.spring.captcha.common.CaptchaType;
import com.vansteve911.spring.captcha.common.FactoryRegistry;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import com.vansteve911.spring.captcha.generator.CaptchaCodeGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Created by vansteve911
 */
public class CaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaService.class);

    @Autowired
    private FactoryRegistry<CaptchaType, CaptchaCodeGenerator> captchaGeneratorFactory;

    @Autowired
    private FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> cacheProviderFactory;

    public CaptchaCode genCaptchaCode(String key, CaptchaType codeType) {
        CaptchaCode validCode = getValidCaptchaCode(key, codeType);
        CaptchaCodeGenerator generator = captchaGeneratorFactory.getFactory(codeType);
        if (validCode != null && !generator.checkGenerateTimeInterval(validCode)) {
            throw new CaptchaException(CaptchaException.Type.GENERATE_TOO_FREQUENTLY);
        }
        CaptchaCode code = generator.generateCaptchaCode(key);
        cacheProviderFactory.getFactory(codeType).put(key, new CaptchaCode(code)); // cache the clone object
        return code;
    }

    public boolean validateCaptchaCode(String key, String value, CaptchaType codeType) {
        CaptchaCode validCode = getValidCaptchaCode(key, codeType);
        return validCode != null && validCode.getValue().equals(value);
    }

    public boolean expireCaptchaCode(String key, CaptchaType codeType) {
        try {
            cacheProviderFactory.getFactory(codeType).delete(key);
            return true;
        } catch (Exception e) {
            logger.error("expireCaptchaCode error, key: " + key, e);
        }
        return false;
    }

    private CaptchaCode getValidCaptchaCode(String key, CaptchaType codeType) {
        try {
            if (!StringUtils.isEmpty(key)) {
                return cacheProviderFactory.getFactory(codeType).get(key);
            }
        } catch (Exception e) {
            logger.error("getValidCaptchaCode error, key: " + key, e);
        }
        return null;
    }
}