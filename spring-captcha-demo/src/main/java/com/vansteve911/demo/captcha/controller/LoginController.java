package com.vansteve911.demo.captcha.controller;

import com.vansteve911.demo.captcha.dto.ApiResult;
import com.vansteve911.demo.captcha.exception.ErrorCode;
import com.vansteve911.demo.captcha.exception.BusinessException;
import com.vansteve911.spring.captcha.common.CaptchaType;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import com.vansteve911.spring.captcha.dto.ImgCaptchaCode;
import com.vansteve911.spring.captcha.generator.impl.ImgCaptchaGenerator;
import com.vansteve911.spring.captcha.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by vansteve911 on 2018/3/30.
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private CaptchaService captchaService;

    @RequestMapping(value = "/loginByCaptcha", method = RequestMethod.POST)
    public ApiResult loginByCaptcha(@RequestParam String mobile,
                                    @RequestParam String mobileCaptcha,
                                    @RequestParam String imgCaptcha,
                                    HttpSession httpSession) {
        validateCaptcha(httpSession.getId(), imgCaptcha, CaptchaType.IMG);
        validateCaptcha(mobile, mobileCaptcha, CaptchaType.MOBILE);
        return new ApiResult(ErrorCode.SUCCESS);
    }

    private void validateCaptcha(String key, String value, CaptchaType type) {
        try {
            if (!captchaService.validateCaptchaCode(key, value, type)) {
                throw new BusinessException(ErrorCode.FAILED, String.format("wrong %s captcha", type.name()));
            }
        } finally {
            captchaService.expireCaptchaCode(key, type);
        }
    }

    @RequestMapping(value = "/mobileCaptcha", method = RequestMethod.GET)
    public ApiResult genMobileCaptcha(@RequestParam String mobile) {
        if(captchaService.genCaptchaCode(mobile, CaptchaType.MOBILE) != null) {
            return new ApiResult(ErrorCode.SUCCESS);
        }
        return new ApiResult(ErrorCode.FAILED);
    }

    @RequestMapping(value = "/imgCaptcha", method = RequestMethod.GET)
    public void genImgCaptcha(HttpServletResponse response, HttpSession httpSession) {
        String sessionId = httpSession.getId();
        CaptchaCode captchaCode = captchaService.genCaptchaCode(sessionId, CaptchaType.IMG);
        if (captchaCode != null && captchaCode instanceof ImgCaptchaCode) {
            ImgCaptchaGenerator.outputToHttpServletResponse((ImgCaptchaCode) captchaCode, response);
        } else {
            throw new BusinessException(ErrorCode.FAILED);
        }
    }
}
