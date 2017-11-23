package com.vansteve911.spring.captcha.generator.impl;

import com.vansteve911.spring.captcha.common.CaptchaException;
import com.vansteve911.spring.captcha.common.CommonUtils;
import com.vansteve911.spring.captcha.config.ImgCaptchaProperties;
import com.vansteve911.spring.captcha.dto.ImgCaptchaCode;
import com.vansteve911.spring.captcha.generator.AbstractCaptchaCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by vansteve911
 */
public class ImgCaptchaGenerator extends AbstractCaptchaCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ImgCaptchaGenerator.class);

    private int fontSize;
    private int width;
    private int height;
    private Font font;

    public ImgCaptchaGenerator(ImgCaptchaProperties properties) {
        super(properties.getCodeLength(), properties.isDebugMode());
        fontSize = properties.getFontSize();
        font = new Font(null, Font.PLAIN, (int) (fontSize * 0.8));
        width = properties.getWidth();
        height = properties.getHeight();
    }

    @Override
    public ImgCaptchaCode generateCaptchaCode(String key) {
        String value = genCaptchaCodeValue(codeLength);
        BufferedImage bufferedImage = genImg(value);
        return new ImgCaptchaCode(key, value, System.currentTimeMillis(), bufferedImage);
    }

    private BufferedImage genImg(String value) {
        long start = System.currentTimeMillis();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        int randomLineCount = 4;
        for (int i = 0; i < randomLineCount; i++) {
            int xs = CommonUtils.genRandomInt(width);
            int ys = CommonUtils.genRandomInt(height);
            int xe = xs + CommonUtils.genRandomInt(width);
            int ye = ys + CommonUtils.genRandomInt(height);
            g2d.setColor(genRandomColor());
            g2d.drawLine(xs, ys, xe, ye);
        }
        g2d.setFont(font);
        int len = value.length();
        int x, y;
        int secWidth = width / len;
        for (int i = 0; i < len; i++) {
            x = CommonUtils.genRandomInt(i * secWidth + fontSize / 2, (i + 1) * secWidth - fontSize / 2);
            y = CommonUtils.genRandomInt(fontSize / 2, height - fontSize / 2);
            g2d.setColor(genRandomColor());
            g2d.drawString(String.valueOf(value.charAt(i)), x, y);
        }
        g2d.dispose();
        if (debugMode) {
            logger.info(String.format("generated img code: %s,cost time: %s", value, (System.currentTimeMillis() - start)));
        }
        return bufferedImage;
    }

    private static Color genRandomColor() {
        return new Color(CommonUtils.genRandomInt(255), CommonUtils.genRandomInt(255), CommonUtils.genRandomInt(255));
    }

    public static void outputToHttpServletResponse(ImgCaptchaCode captchaCode, HttpServletResponse response) {
        if (captchaCode.getBufferedImage() == null) {
            throw new CaptchaException(CaptchaException.Type.GENERATE_FAILED);
        }
        try {
            ImageIO.write(captchaCode.getBufferedImage(), "jpg", response.getOutputStream());
        } catch (IOException e) {
            throw new CaptchaException(CaptchaException.Type.GENERATE_FAILED);
        }
    }
}
