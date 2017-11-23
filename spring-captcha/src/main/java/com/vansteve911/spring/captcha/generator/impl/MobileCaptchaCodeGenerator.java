package com.vansteve911.spring.captcha.generator.impl;

import com.vansteve911.spring.captcha.common.CaptchaException;
import com.vansteve911.spring.captcha.common.CommonUtils;
import com.vansteve911.spring.captcha.common.JsonUtils;
import com.vansteve911.spring.captcha.config.MobileCaptchaProperties;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import com.vansteve911.spring.captcha.dto.MobileCaptchaCode;
import com.vansteve911.spring.captcha.generator.AbstractCaptchaCodeGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vansteve911
 */
public class MobileCaptchaCodeGenerator extends AbstractCaptchaCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MobileCaptchaCodeGenerator.class);

    private static final String PRODUCT = "Dysmsapi";
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String CONNECT_TIMEOUT_KEY = "sun.net.client.defaultConnectTimeout";
    private static final String READ_TIMEOUT_KEY = "sun.net.client.defaultReadTimeout";

    private String region = "cn-hangzhou";
    private String accessKeyId = "";
    private String accessKeySecret = "";
    private SmsTemplate smsTemplate;

    private int connectTimeout;
    private int readTimeout;

    @Override
    public CaptchaCode generateCaptchaCode(String key) {
        String code = genCaptchaCodeValue(codeLength);
        if (!sendSms(key, smsTemplate, code)) {
            throw new CaptchaException(CaptchaException.Type.SEND_FAILED);
        }
        if (debugMode) {
            logger.info(String.format("generated mobile captcha, key: %s, value: %s ", key, code));
        }
        return new MobileCaptchaCode(key, code, System.currentTimeMillis());
    }

    public MobileCaptchaCodeGenerator(MobileCaptchaProperties properties) {
        super(properties.getResendIntervals(), properties.getCodeLength(), properties.isDebugMode());
        region = properties.getRegion();
        accessKeyId = properties.getAccessKeyId();
        accessKeySecret = properties.getAccessKeySecret();
        if (region == null || accessKeyId == null || accessKeySecret == null) {
            throw new IllegalArgumentException("illegal mobile captcha properties!");
        }
        connectTimeout = properties.getConnectTimeout();
        readTimeout = properties.getReadTimeout();
        smsTemplate = parseSmsTemplate(properties.getTemplateFile());
    }

    private SmsTemplate parseSmsTemplate(String filepath) {
        String json = CommonUtils.loadFile(filepath);
        if (json != null) {
            SmsTemplate template = JsonUtils.fromJson(json, SmsTemplate.class);
            if (template != null) {
                return template;
            }
            logger.error("parseSmsTemplate failed");
        }
        return new SmsTemplate("阿里云短信测试专用", "SAMPLE", "test");
    }

    public static class SmsTemplate {
        private String signName;
        private String templateCode;
        private String json;

        public SmsTemplate(String signName, String templateCode, String json) {
            this.signName = signName;
            this.templateCode = templateCode;
            this.json = json;
        }
    }

    private IAcsClient acsClient;

    private IAcsClient getAcsClient() {
        if (acsClient != null) {
            return acsClient;
        }
        logger.info("initializing acs client...");
        try {
            System.setProperty(CONNECT_TIMEOUT_KEY, String.valueOf(connectTimeout));
            System.setProperty(READ_TIMEOUT_KEY, String.valueOf(readTimeout));
            IClientProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint(region, region, PRODUCT, DOMAIN);
            acsClient = new DefaultAcsClient(profile);
        } catch (Exception e) {
            logger.error("error in initializing acs client", e);
            acsClient = null;
        }
        logger.info("initializing acs client complete.");
        return acsClient;
    }

    private boolean sendSms(String mobile, SmsTemplate smsTemplate, String... args) {
        SendSmsResponse response = null;
        try {
            String content = String.format(smsTemplate.json, (Object[]) args);
            response = sendSms(mobile, smsTemplate.signName, smsTemplate.templateCode, content, null, null);
            if (response != null && "OK".equals(response.getCode())) {
                if (debugMode) {
                    logger.info(String.format("sendSms success, resp: %s", sendResponse2Str(response)));
                }
                return true;
            }
            logger.warn("sendSms failed, resp: " + sendResponse2Str(response));
        } catch (Exception e) {
            logger.error("sendSms error, resp: " + sendResponse2Str(response), e);
        }
        return false;
    }

    private static String sendResponse2Str(SendSmsResponse response) {
        return response == null ? null : String.format("code=%s, message=%s, requestId=%s, bizId=%s",
                response.getCode(), response.getMessage(), response.getRequestId(), response.getBizId());
    }

    private SendSmsResponse sendSms(String mobile, String signName, String templateCode, String content, String upExtendCode, String outId) throws ClientException {
        IAcsClient acsClient = getAcsClient();
        if (acsClient == null) {
            return null;
        }
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(mobile);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam(content);
        if (upExtendCode != null) {
            request.setSmsUpExtendCode(upExtendCode);
        }
        if (outId != null) {
            request.setOutId(outId);
        }
        return acsClient.getAcsResponse(request);
    }

    private QuerySendDetailsResponse querySendDetails(String mobile, String bizId, long pageSize, long currentPage) throws ClientException {
        IAcsClient acsClient = getAcsClient();
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        request.setPhoneNumber(mobile);
        request.setBizId(bizId);
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        request.setPageSize(pageSize);
        request.setCurrentPage(currentPage);
        return acsClient.getAcsResponse(request);
    }
}