package com.vansteve911.spring.captcha.generator;

import com.vansteve911.spring.captcha.common.CommonUtils;
import com.vansteve911.spring.captcha.dto.CaptchaCode;

/**
 * Created by vansteve911
 */
public abstract class AbstractCaptchaCodeGenerator implements CaptchaCodeGenerator {

    protected int resendIntervals = -1;
    protected int codeLength;
    protected boolean debugMode = false;

    public AbstractCaptchaCodeGenerator(int codeLength, boolean debugMode) {
        this.codeLength = codeLength;
        this.debugMode = debugMode;
    }

    public AbstractCaptchaCodeGenerator(int resendIntervals, int codeLength, boolean debugMode) {
        this.resendIntervals = resendIntervals;
        this.codeLength = codeLength;
        this.debugMode = debugMode;
    }

    protected String genCaptchaCodeValue(int length) {
        int mod = 1;
        for (int i = 0; i < length; i++) {
            mod *= 10;
        }
        StringBuilder s = new StringBuilder().append(CommonUtils.genRandomInt(mod));
        while (s.length() < length) {
            s.insert(0, 0);
        }
        return s.toString();
    }

    @Override
    public boolean checkGenerateTimeInterval(CaptchaCode code) {
        return resendIntervals > 0 && (System.currentTimeMillis() - code.getCreateTime()) > resendIntervals * 1000;
    }

}
