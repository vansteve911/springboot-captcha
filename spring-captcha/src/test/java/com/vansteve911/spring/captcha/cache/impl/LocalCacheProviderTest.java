package com.vansteve911.spring.captcha.cache.impl;

import com.google.common.cache.LoadingCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


import static com.vansteve911.spring.captcha.common.TestUtils.getPrivateFieldValue;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by vansteve911 on 18/4/6.
 */
@RunWith(SpringRunner.class)
public class LocalCacheProviderTest {

    private static int MAX_CACHE_SIZE = 5;
    private static int EXPIRE_SECONDS = 2;

    private LocalCacheProvider<String> cacheProvider = new LocalCacheProvider<>(MAX_CACHE_SIZE, EXPIRE_SECONDS);

    @Test
    public void tests() throws Exception {
        String key = "a";
        String value = "b";
        cacheProvider.put(key, value);
        assertEquals(value, cacheProvider.get(key));
        // expire test
        Thread.sleep(EXPIRE_SECONDS * 1000);
        assertNull(cacheProvider.get(key));
        // delete test
        cacheProvider.put(key, value);
        cacheProvider.delete(key);
        assertNull(cacheProvider.get(key));
        // check max size
        for (int k = 0; k < MAX_CACHE_SIZE * 2; k++) {
            cacheProvider.put(String.valueOf(k), value);
        }
        LoadingCache<String, String> loadingCache = (LoadingCache<String, String>) getPrivateFieldValue(cacheProvider, "localCache");
        assertEquals(MAX_CACHE_SIZE, loadingCache.asMap().size());
    }

}