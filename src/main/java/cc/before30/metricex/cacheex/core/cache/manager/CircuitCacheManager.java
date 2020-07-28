package cc.before30.metricex.cacheex.core.cache.manager;

import cc.before30.metricex.cacheex.core.cache.CircuitCustomCacheWrapper;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CircuitCacheManager
 *
 * @author before30
 * @since 2020/07/18
 */
@RequiredArgsConstructor
public class CircuitCacheManager implements CacheManager {
    private final CacheManager delegate;
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap();
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(
                name, key -> {
                    CircuitCustomCacheWrapper cacheWrapper = new CircuitCustomCacheWrapper(delegate.getCache(key), circuitBreaker, bulkhead);
                    return cacheWrapper;
                });
    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
