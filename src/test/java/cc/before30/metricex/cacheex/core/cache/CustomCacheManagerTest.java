package cc.before30.metricex.cacheex.core.cache;

import cc.before30.metricex.cacheex.core.cache.manager.CustomCacheManager;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CustomCacheManagerTest
 *
 * @author before30
 * @since 2020/07/28
 */
public class CustomCacheManagerTest {

    @Test
    public void testDynamicMode() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        CacheManager cm = new CustomCacheManager(cacheManager);

        Cache cache1 = cm.getCache("c1");
        boolean condition2 = cache1 instanceof CustomCacheWrapper;
        assertThat(condition2).isTrue();

        Cache cache1again = cm.getCache("c1");
        assertThat(cache1).isSameAs(cache1again);

        Cache cache2 = cm.getCache("c2");
        boolean condition1 = cache2 instanceof  CustomCacheWrapper;
        assertThat(condition1).isTrue();

        Cache cache2again = cm.getCache("c2");
        assertThat(cache2).isSameAs(cache2again);

        Cache cache3 = cm.getCache("c3");
        boolean condition = cache3 instanceof CustomCacheWrapper;
        assertThat(condition).isTrue();

        Cache cache3again = cm.getCache("c3");
        assertThat(cache3).isSameAs(cache3again);

        cache1.put("key1", "value1");
        assertThat(cache1.get("key1").get()).isEqualTo("value1");
        cache1.put("key2", 2);
        assertThat(cache1.get("key2").get()).isEqualTo(2);
        cache1.put("key3", null);
        assertThat(cache1.get("key3").get()).isNull();
        cache1.evict("key3");
        assertThat(cache1.get("key3")).isNull();
    }
}
