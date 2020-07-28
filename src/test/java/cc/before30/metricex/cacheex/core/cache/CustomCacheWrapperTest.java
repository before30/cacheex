package cc.before30.metricex.cacheex.core.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomCacheWrapperTest
 *
 * @author before30
 * @since 2020/07/28
 */
public class CustomCacheWrapperTest {

    private Cache delegate = new ConcurrentMapCache("test_cache");

    private CustomCacheWrapper cache = new CustomCacheWrapper(delegate);

    @Test
    public void testPutIfAbsentNullValue() {
        Object key = new Object();
        Object value = null;

        assertThat(cache.get(key)).isNull();
        assertThat(cache.putIfAbsent(key, value)).isNull();
        assertThat(cache.get(key).get()).isEqualTo(value);
        Cache.ValueWrapper wrapper = cache.putIfAbsent(key, "anotherValue");
        // A value is set but is 'null'
        assertThat(wrapper).isNotNull();
        assertThat(wrapper.get()).isEqualTo(null);
        // not changed
        assertThat(cache.get(key).get()).isEqualTo(value);

    }
}