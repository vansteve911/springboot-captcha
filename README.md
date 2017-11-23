springboot-captcha
--

基于SpringBoot的图片&短信（基于阿里云）的验证码方案封装。

# 基本需求

- JDK1.8
- SpringBoot2

# 使用

## 1.引入依赖

- maven

```
<dependency>
  <groupId>com.vansteve911.springboot</groupId>
  <artifactId>captcha</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```
- gradle

```
dependencies {
	compile('com.vansteve911.springboot:captcha:0.1.0-SNAPSHOT')
}
```

## 2. 导入到你的SpringBoot项目中

### 如果你的项目使用JavaConfig：

新建Config类，继承自DefaultCaptchaConfig（或者参考DefaultCaptchaConfig自己实现一个），并且将CaptchaService声明为Bean。

```
@Configuration
public class CaptchaConfig extends DefaultCaptchaConfig {
    @Bean
    public CaptchaService captchaService() {
        return new CaptchaService();
    }
}

```
DefaultCaptchaConfig

继承DefaultCaptchaConfig类的时候，你可以通过重写captchaGeneratorFactory或cacheProviderFactory两个方法，自定义不同类型验证码的生成器/缓存。如下是将生成短信验证码的缓存改为用redis实现（该实现也在spring-captcha中）：

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

### 如果你的项目使用XMLConfig：

- 将DefaultCaptchaConfig声明为bean（或实现一个继承自DefaultCaptchaConfig的类并声明为bean）
- 将CaptchaService声明为Bean

```
  <bean name="captchaConfig" class="com.vansteve911.spring.captcha.config.DefaultCaptchaConfig"/>
  <bean name="captchaService" class="com.vansteve911.spring.captcha.service.CaptchaService"/>
```

## 3. 修改配置

### 在app.properties中指定验证码的配置

*也可以在任意载入了classpath的properties文件/环境变量中指定.*

```
# 短信验证码配置
captcha-mobile.codeLength={验证码长度}
captcha-mobile.region={阿里云短信region}
captcha-mobile.accessKeyId={阿里云短信key}
captcha-mobile.accessKeySecret={阿里云短信secret}
captcha-mobile.expireSeconds={缓存过期秒数}
# img captcha config
captcha-mobile.codeLength={验证码长度}
captcha-img.width={图片验证码宽度像素}
captcha-img.height={图片验证码高度像素}
captcha-img.fontSize={图片验证码字体大小}
```

若使用库中自带的redis缓存（基于[spring-boot-starter-data-redis](https://spring.io/guides/gs/messaging-redis/)），则如下配置redis参数：

```
spring.redis.host=127.0.0.1
spring.redis.password=pwd
spring.redis.port=6379
spring.redis.database=0
# 其他参数
```

### 修改短信模板
 
 阿里云短信发送需要指定模板的ID及格式等，在resource文件夹中创建sms_template.json文件定义：
 
 ```
 {
  "signName": "阿里云短信测试专用",
  "templateCode": "{your_code}",
  "json": "{\"code\":\"%s\"}"
}
 ```
 
### 自定义报错信息
 
 在resource文件夹中创建sms_template.json文件定义验证码相关错误类型的报错信息：
 
 ```
 {
  "GENERATE_FAILED": "验证码生成失败",
  "GENERATE_TOO_FREQUENTLY": "请勿频繁请求验证码",
  "SEND_FAILED": "验证码发送失败, 请稍后再试"
}
 ```


其他细节可参考spring-captcha-demo代码。
