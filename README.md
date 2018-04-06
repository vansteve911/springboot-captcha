springboot-captcha
--

A SpringBoot-based tool for image & mobile captcha (using [Aliyun](https://help.aliyun.com/document_detail/59210.html)）

[![Build Status](https://travis-ci.org/vansteve911/springboot-captcha.svg?branch=master)](https://travis-ci.org/vansteve911/springboot-captcha)
[![codecov](https://codecov.io/gh/vansteve911/springboot-captcha/branch/master/graph/badge.svg)](https://codecov.io/gh/vansteve911/springboot-captcha)

[中文文档](https://github.com/vansteve911/springboot-captcha/wiki#springboot-captcha)

# Requirements

- JDK1.8
- SpringBoot2

# Usage

## 1.Add to dependencies

- maven

```
<dependency>
  <groupId>com.vansteve911</groupId>
  <artifactId>springboot-captcha</artifactId>
  <version>0.1.2</version>
</dependency>
```
- gradle

```
dependencies {
	compile('com.vansteve911:springboot-captcha:0.1.2')
}
```

## 2. Import in your SpringBoot project

### For JavaConfig：

Create a @Configuration class which extends from `DefaultCaptchaConfig`, or implement your own configuration class refering to `DefaultCaptchaConfig`. The `CaptchaService` should be declared as a bean for either way you use.

```
@Configuration
public class CaptchaConfig extends DefaultCaptchaConfig {
    @Bean
    public CaptchaService captchaService() {
        return new CaptchaService();
    }
}

```

When extending `DefaultCaptchaConfig`，you can override the `captchaGeneratorFactory` or `cacheProviderFactory`method to customizing different types of captcha generater or cache provider.

For example, you can use Redis as the cache provider(which is implemented in spring-captcha) for mobile captchas like this:

```
@Override
@Bean
public FactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>> cacheProviderFactory() {
    return new DefaultFactoryRegistry<CaptchaType, CacheProvider<String, CaptchaCode>>()
            .registerFactory(CaptchaType.MOBILE,
                    new RedisCacheProvider<>(mobileCaptchaProperties.getExpireSeconds(), redisTemplate))
            .registerFactory(CaptchaType.IMG, new LocalCacheProvider<>(imgCaptchaProperties.getExpireSeconds(),
                    imgCaptchaProperties.getMaxCacheSize()));
}

```

### For old-plain XMLConfig:

- declare or extend `DefaultCaptchaConfig` as a bean
- declare `CaptchaService` as a bean

```
  <bean name="captchaConfig" class="com.vansteve911.spring.captcha.config.DefaultCaptchaConfig"/>
  <bean name="captchaService" class="com.vansteve911.spring.captcha.service.CaptchaService"/>
```

## 3. Customizing properties

###  customizing captcha configs in `app.properties`

*You can also put these properties in any loaded resource file or env variables.*

```
# mobile captcha config
captcha-mobile.codeLength=4
captcha-mobile.region=SAMPLE
captcha-mobile.accessKeyId=SAMPLE
captcha-mobile.accessKeySecret=SAMPLE
captcha-mobile.expireSeconds=80
# img captcha config
captcha-img.codeLength=6
captcha-img.width=240
captcha-img.height=60
```

When using the integrated redis cache with spring-captcha (based on [spring-boot-starter-data-redis](https://spring.io/guides/gs/messaging-redis/)), you can specify redis params in `app.properties` as below:

```
spring.redis.host=127.0.0.1
spring.redis.password=pwd
spring.redis.port=6379
spring.redis.database=0
# other params
```

### Modify the SMS template
 
 Define signature, template code and content template in `sms_template.json` for sending Aliyun mobile captcha:
 
 ```
 {
  "signName": "阿里云短信测试专用",
  "templateCode": "{your_code}",
  "json": "{\"code\":\"%s\"}"
}
 ```
 
### Customizing exception messages:
 
 Create `exception_msg.json` under resource directory to customize captcha exception messages:
 
 ```
 {
  "GENERATE_FAILED": "failed to generate captcha",
  "GENERATE_TOO_FREQUENTLY": "please do not generate captcha too frequently",
  "SEND_FAILED": "failed to send captcha, please try again"
}
 ```

More details are involved in source code of `spring-captcha-demo` project.
