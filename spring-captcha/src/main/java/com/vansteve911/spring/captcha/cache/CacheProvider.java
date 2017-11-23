package com.vansteve911.spring.captcha.cache;

/**
 * Created by vansteve911
 */
public interface CacheProvider<K, V> {
    void put(K key, V value);

    V get(K key);

    void delete(K key);
}
