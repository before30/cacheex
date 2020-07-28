package cc.before30.metricex.cacheex.core.cache.manager;

import cc.before30.metricex.cacheex.core.cache.CustomCacheWrapper;
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
public class CustomCacheManager implements CacheManager {

    private final CacheManager delegate;
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap();

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(
                name, key -> {
                    CustomCacheWrapper cacheWrapper = new CustomCacheWrapper(delegate.getCache(key));
                    return cacheWrapper;
                });
    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
