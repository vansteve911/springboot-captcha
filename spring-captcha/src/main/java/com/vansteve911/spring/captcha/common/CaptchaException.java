package com.vansteve911.spring.captcha.common;

import org.springframework.util.CollectionUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vansteve911
 */
public class CaptchaException extends RuntimeException {

    private static EnumMap<Type, String> msgTypes = new EnumMap<Type, String>(Stream.of(Type.values())
            .collect(Collectors.toMap(e -> e, Type::name)));

    public enum Type {
        GENERATE_FAILED,
        GENERATE_TOO_FREQUENTLY,
        SEND_FAILED
    }

    public static void setMsgTypes(Map<Type, String> map) {
        if (CollectionUtils.isEmpty(map) ||
                !Stream.of(Type.values()).allMatch(e -> map.keySet().contains(e))) {
            throw new IllegalArgumentException("illegal map " + map);
        }
        msgTypes = new EnumMap<>(map);
    }

    public static String typeMsg(Type type) {
        return msgTypes.get(type);
    }

    public CaptchaException(Type type) {
        super(typeMsg(type));
    }

    public CaptchaException(String message) {
        super(message);
    }
}