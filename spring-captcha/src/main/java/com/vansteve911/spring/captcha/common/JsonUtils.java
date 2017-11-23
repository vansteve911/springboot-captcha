package com.vansteve911.spring.captcha.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by vansteve911 on 2018/3/29.
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return jsonMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("fromJson error", e);
        }
        return null;
    }

    public static String toJson(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("toJson error", e);
        }
        return null;
    }

    public static <K, V> Map<K, V> fromJsonToTypedMap(String json, Class<K> keyClass, Class<V> valueClass) {
        try {
            TypeReference<Map<K, V>> typeRef = new TypeReference<Map<K, V>>() {};
            return jsonMapper.readValue(json, typeRef);
        } catch (IOException e) {
            logger.error("fromJsonToMap", e);
        }
        return null;
    }

    public static Map<String, String> fromJsonToStrMap(String json) {
        return fromJsonToTypedMap(json, String.class, String.class);
    }
}
