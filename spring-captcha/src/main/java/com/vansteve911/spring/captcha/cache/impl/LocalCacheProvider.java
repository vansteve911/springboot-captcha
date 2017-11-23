package com.vansteve911.spring.captcha.cache.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vansteve911.spring.captcha.cache.AbstractCacheProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by vansteve911
 */
public class LocalCacheProvider <T> extends AbstractCacheProvider<String, T> {

    private static final int MAX_CACHE_SIZE = 5000;

    private LoadingCache<String, T> localCache;

    public LocalCacheProvider(int expireSeconds) {
        this(MAX_CACHE_SIZE, expireSeconds);
    }

    public LocalCacheProvider(int maxCacheSize, int expireSeconds) {
        super(maxCacheSize, expireSeconds);
        localCache = CacheBuilder.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterAccess(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, T>() {
                    @Override
                    public T load(String s) throws Exception {
                        return null;
                    }
                });
    }

    @Override
    public void put(String key, T value) {
        localCache.put(key, value);
    }

    @Override
    public T get(String key) {
        return localCache.getIfPresent(key);
    }

    @Override
    public void delete(String key) {
        localCache.invalidate(key);
    }
}
