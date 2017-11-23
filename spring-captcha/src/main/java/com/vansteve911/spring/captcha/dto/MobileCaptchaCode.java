package com.vansteve911.spring.captcha.dto;

/**
 * Created by vansteve911
 */
public class MobileCaptchaCode extends CaptchaCode {

    public MobileCaptchaCode() {
    }

    public MobileCaptchaCode(String key, String value, Long createTime) {
        super(key, value, createTime);
    }
}