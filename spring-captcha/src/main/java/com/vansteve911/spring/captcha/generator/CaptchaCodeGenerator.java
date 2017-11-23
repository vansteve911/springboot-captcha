package com.vansteve911.spring.captcha.generator;

import com.vansteve911.spring.captcha.dto.CaptchaCode;

/**
 * Created by vansteve911
 */
public interface CaptchaCodeGenerator <T extends CaptchaCode> {

    T generateCaptchaCode(String key);

    boolean checkGenerateTimeInterval(CaptchaCode code);
}
