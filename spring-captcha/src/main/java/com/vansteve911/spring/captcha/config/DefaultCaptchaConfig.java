package com.vansteve911.spring.captcha.config;

import com.vansteve911.spring.captcha.cache.CacheProvider;
import com.vansteve911.spring.captcha.cache.impl.LocalCacheProvider;
import com.vansteve911.spring.captcha.common.*;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import com.vansteve911.spring.captcha.generator.CaptchaCodeGenerator;
import com.vansteve911.spring.captcha.generator.impl.ImgCaptchaGenerator;
import com.vansteve911.spring.captcha.generator.impl.MobileCaptchaCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vansteve911
 */
@EnableConfigurationProperties({MobileCaptchaProperties.class, ImgCaptchaProperties.class})
public class DefaultCaptchaConfig {

    @Autowired
    protected MobileCaptchaProperties mobileCaptchaProperties;

    @Autowired
    protected ImgCaptchaProperties imgCaptchaProperties;

    @Bean
    public FactoryRegistry<CaptchaType, CaptchaCodeGenerator> captchaGeneratorFactory() {
        return new DefaultFactoryRegistry<CaptchaType, CaptchaCodeGenerator>()
                .registerFactory(CaptchaType.MOBILE, new MobileCaptchaCodeGenerator(mobileCaptchaProperties))
                .registerFactory(CaptchaType.IMG, new ImgCaptchaGenerator(imgCaptchaProperties));
    }

    @Bean
    public FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> cacheProviderFactory() {
        return new DefaultFactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>>()
                .registerFactory(CaptchaType.MOBILE, new LocalCacheProvider<>(mobileCaptchaProperties.getExpireSeconds()))
                .registerFactory(CaptchaType.IMG, new LocalCacheProvider<>(imgCaptchaProperties.getExpireSeconds(),
                        imgCaptchaProperties.getMaxCacheSize()));
    }

    @PostConstruct
    public void init() {
        loadExceptionMsg("exception_msg.json");
    }

    static void loadExceptionMsg(String fileName) {
        String json = CommonUtils.loadFile(fileName);
        if (json == null) {
            return;
        }
        Map<String, String> map = JsonUtils.fromJsonToStrMap(json);
        if (map == null) {
            return;
        }
        Map<CaptchaException.Type, String> m = map.entrySet().stream().collect(Collectors.toMap(
                kv -> Enum.valueOf(CaptchaException.Type.class, kv.getKey()), Map.Entry::getValue));
        CaptchaException.setMsgTypes(m);
    }
}
