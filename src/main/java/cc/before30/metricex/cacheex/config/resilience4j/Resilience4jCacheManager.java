package cc.before30.metricex.cacheex.config.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resilience4jCacheManager
 *
 * @author before30
 * @since 2020/07/17
 */
@RequiredArgsConstructor
public class Resilience4jCacheManager implements CacheManager {

    private final CacheManager delegate;
    private final CircuitBreaker circuitBreaker;
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap();
    private final MeterRegistry meterRegistry;

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(
                name, key -> new Resilience4jCache(delegate.getCache(key), circuitBreaker, meterRegistry));

    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
