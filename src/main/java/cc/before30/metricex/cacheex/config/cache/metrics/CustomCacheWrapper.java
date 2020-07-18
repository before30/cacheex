package cc.before30.metricex.cacheex.config.cache.metrics;

import cc.before30.metricex.cacheex.config.cache.metrics.CacheStats;
import cc.before30.metricex.cacheex.config.cache.metrics.CacheStatsCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Resilience4jCache
 *
 * @author before30
 * @since 2020/07/17
 */
@Slf4j
@RequiredArgsConstructor
public class CustomCacheWrapper implements Cache {

    private final Cache delegate;
    private final CacheStatsCounter cacheStatsCounter = new CacheStatsCounter();

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = delegate.get(key);
        afterGet(value);
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = delegate.get(key, type);
        afterGet(value);
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = delegate.get(key, valueLoader);
        afterGet(value);
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        delegate.put(key, value);
        afterPut();
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper valueWrapper = delegate.putIfAbsent(key, value);
        afterPut();
        return valueWrapper;
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
        afterEvict();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    public CacheStats stats() {
        return cacheStatsCounter.snapshot();
    }

    private void afterGet(Object value) {
        if (Objects.isNull(value)) {
            // cache miss
            cacheStatsCounter.recordMiss();
        } else {
            // cache hit
            cacheStatsCounter.recordHit();
        }
    }

    private void afterPut() {
        cacheStatsCounter.recordPut();
    }

    private void afterEvict() {
        cacheStatsCounter.recordEviction();
    }
}
