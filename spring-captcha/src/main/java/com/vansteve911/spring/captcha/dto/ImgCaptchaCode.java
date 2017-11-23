package com.vansteve911.spring.captcha.dto;

import java.awt.image.BufferedImage;

/**
 * Created by vansteve911
 */
public class ImgCaptchaCode extends CaptchaCode {
    private BufferedImage bufferedImage;

    public ImgCaptchaCode(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public ImgCaptchaCode(String key, String value, Long createTime, BufferedImage bufferedImage) {
        super(key, value, createTime);
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
}