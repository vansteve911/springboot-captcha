package com.vansteve911.spring.captcha.generator.impl;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.vansteve911.spring.captcha.common.CaptchaException;
import com.vansteve911.spring.captcha.config.MobileCaptchaProperties;
import com.vansteve911.spring.captcha.dto.CaptchaCode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static com.vansteve911.spring.captcha.common.CaptchaException.Type.SEND_FAILED;
import static com.vansteve911.spring.captcha.common.TestUtils.expectExceptionThrown;
import static com.vansteve911.spring.captcha.common.TestUtils.invokePrivateMethod;
import static com.vansteve911.spring.captcha.common.TestUtils.setPrivateFieldValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vansteve911 on 18/4/6.
 */
@RunWith(SpringRunner.class)
public class MobileCaptchaCodeGeneratorTest {

    private static final int RESEND_INTERVALS = 2;
    private MobileCaptchaCodeGenerator generator;
    private MobileCaptchaProperties properties;
    private IAcsClient mockAcsClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    {
        properties = mobileCaptchaProperties();
        generator = new MobileCaptchaCodeGenerator(properties);
        mockAcsClient = mock(IAcsClient.class);
        makeMockAscClientReturn(successResponse());
        invokePrivateMethod(generator, "getAcsClient"); // for coverage
        setPrivateFieldValue(generator, "acsClient", mockAcsClient);
    }

    @Test
    public void generateCaptchaCode() throws Exception {
        CaptchaCode code = generator.generateCaptchaCode("a");
        assertTrue(code != null && code.getValue().length() == properties.getCodeLength());
        // check resend intervals
        Thread.sleep(RESEND_INTERVALS * 1000 + 200);
        assertTrue(generator.checkGenerateTimeInterval(code));
    }

    @Test
    public void genCodeOnSendFailed() throws Exception {
        makeMockAscClientReturn(null);
        expectExceptionThrown(thrown, CaptchaException.class, CaptchaException.typeMsg(SEND_FAILED));
        generator.generateCaptchaCode("a");
    }

    private MobileCaptchaProperties mobileCaptchaProperties() {
        MobileCaptchaProperties p = new MobileCaptchaProperties();
        p.setRegion("SAMPLE");
        p.setAccessKeyId("SAMPLE");
        p.setAccessKeySecret("SAMPLE");
        p.setTemplateFile("src/test/resources/sms_template.json");
        p.setResendIntervals(RESEND_INTERVALS);
        p.setDebugMode(true);
        return p;
    }

    private void makeMockAscClientReturn(SendSmsResponse response) {
        try {
            when(mockAcsClient.getAcsResponse(any())).thenReturn(response);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private SendSmsResponse successResponse() {
        SendSmsResponse resp = new SendSmsResponse();
        resp.setCode("OK");
        return resp;
    }

}