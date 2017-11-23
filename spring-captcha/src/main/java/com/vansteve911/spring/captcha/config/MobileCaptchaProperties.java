package com.vansteve911.spring.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by vansteve911
 */
@ConfigurationProperties(prefix = "captcha-mobile")
public class MobileCaptchaProperties {

    private boolean debugMode = false;
    private int codeLength = 6;
    private int resendIntervals = 60;
    private String region;
    private String accessKeyId;
    private String accessKeySecret;
    private int connectTimeout = 10000;
    private int readTimeout = 10000;
    private String templateFile = "sms_template.json";
    private int expireSeconds;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public int getResendIntervals() {
        return resendIntervals;
    }

    public void setResendIntervals(int resendIntervals) {
        this.resendIntervals = resendIntervals;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
