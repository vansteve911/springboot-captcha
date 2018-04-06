package com.vansteve911.spring.captcha.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by vansteve911 on 2018/3/23.
 */
public class CommonUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static int genRandomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static int genRandomInt(int lowerBound, int upperBound) {
        if (lowerBound > upperBound) {
            int tmp = lowerBound;
            lowerBound = upperBound;
            upperBound = tmp;
        }
        return lowerBound + ThreadLocalRandom.current().nextInt(upperBound - lowerBound);
    }

    public static long genRandomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static String loadFile(String filename) {
        try {
            URL url = CommonUtils.class.getClassLoader().getResource(filename);
            if (url == null) {
                logger.warn("file not exists: " + filename);
                return null;
            }
            Path path = Paths.get(url.toURI());
            return new String(Files.readAllBytes(path));
        } catch (Exception e) {
            logger.error("loadFile failed: " + filename);
        }
        return null;
    }
}
