package com.vansteve911.spring.captcha.cache;

/**
 * Created by vansteve911
 */
public abstract class AbstractCacheProvider<K, V> implements CacheProvider<K, V> {

    protected int expireSeconds;

    protected int maxCacheSize;

    public AbstractCacheProvider(int expireSeconds, int maxCacheSize) {
        this.expireSeconds = expireSeconds;
        this.maxCacheSize = maxCacheSize;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }
}
