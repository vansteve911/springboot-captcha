package com.vansteve911.spring.captcha.generator.impl;

import com.vansteve911.spring.captcha.common.CaptchaException;
import com.vansteve911.spring.captcha.config.ImgCaptchaProperties;
import com.vansteve911.spring.captcha.dto.ImgCaptchaCode;
import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.vansteve911.spring.captcha.common.CaptchaException.Type.GENERATE_FAILED;
import static com.vansteve911.spring.captcha.common.TestUtils.expectExceptionThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by vansteve911 on 18/4/5.
 */
@RunWith(SpringRunner.class)
public class ImgCaptchaGeneratorTest {

    private ImgCaptchaGenerator imgCaptchaGenerator;
    private ImgCaptchaProperties properties;

    @Rule
    public ExpectedException thrown;

    {
        properties = new ImgCaptchaProperties();
        properties.setDebugMode(true);
        imgCaptchaGenerator = new ImgCaptchaGenerator(properties);
        thrown = ExpectedException.none();

    }

    @Test
    public void generateCaptchaCode() throws Exception {
        String key = "a";
        ImgCaptchaCode code = imgCaptchaGenerator.generateCaptchaCode(key);
        assertTrue(code != null && code.getValue().length() == properties.getCodeLength()
                && code.getBufferedImage() != null);
    }

    @Test
    public void outputToHttpServletResponse() throws Exception {
        HttpServletResponse mockResp = mockResponse();
        ImgCaptchaCode code = imgCaptchaGenerator.generateCaptchaCode("key");
        ImgCaptchaGenerator.outputToHttpServletResponse(code, mockResp);
    }

    @Test
    public void outputToHttpServletResponseOnNullImage() throws Exception {
        expectExceptionThrown(thrown, CaptchaException.class, CaptchaException.typeMsg(GENERATE_FAILED));
        ImgCaptchaGenerator.outputToHttpServletResponse(new ImgCaptchaCode("a", "b", 0L, null), mockResponse());
    }

    @Test
    public void outputToHttpServletResponseOnIOException() throws Exception {
        HttpServletResponse mockResp = mockResponse();
        when(mockResp.getOutputStream()).thenThrow(new IOException());
        ImgCaptchaCode code = imgCaptchaGenerator.generateCaptchaCode("key");
        expectExceptionThrown(thrown, CaptchaException.class, CaptchaException.typeMsg(GENERATE_FAILED));
        ImgCaptchaGenerator.outputToHttpServletResponse(code, mockResp);
    }

    private HttpServletResponse mockResponse() throws IOException {
        HttpServletResponse mockResp = mock(HttpServletResponse.class);
        ByteArrayServletOutputStream outputStream = new ByteArrayServletOutputStream();
        when(mockResp.getOutputStream()).thenReturn(outputStream);
        return mockResp;
    }
}