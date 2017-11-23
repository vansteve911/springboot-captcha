package com.vansteve911.spring.captcha.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Created by vansteve911 on 2018/3/23.
 */
public class CommonUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    private static final Random random = new Random();

    public static int genRandomInt(int bound) {
        refreshRandomSeedIfNeeded();
        return random.nextInt(bound);
    }

    public static int genRandomInt(int lowerBound, int upperBound) {
        if (lowerBound > upperBound) {
            return -1;
        }
        refreshRandomSeedIfNeeded();
        return lowerBound + random.nextInt(upperBound - lowerBound);
    }

    public static long genRandomLong() {
        refreshRandomSeedIfNeeded();
        return random.nextLong();
    }

    private static void refreshRandomSeedIfNeeded() {
        long now = System.currentTimeMillis();
        if (now % 100 == 0) {
            random.setSeed(now);
        }
    }

    public static String loadFile(String filename) {
        try {
            Path path = Paths.get(filename);
            if (!Files.exists(path)) {
                logger.warn("file not exists: " + filename);
                return null;
            }
            return new String(Files.readAllBytes(path));
        } catch (Exception e) {
            logger.error("loadFile failed: " + filename);
        }
        return null;
    }
}
