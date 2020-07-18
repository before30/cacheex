package cc.before30.metricex.cacheex.config.cache;

import cc.before30.metricex.cacheex.config.cache.metrics.CustomCacheWrapper;
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

//    @Setter
//    private CacheMetricsRegistrar cacheMetricsRegistrar;

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(
                name, key -> {
                    CustomCacheWrapper cacheWrapper = new CustomCacheWrapper(delegate.getCache(key));
//                    if (Objects.nonNull(cacheMetricsRegistrar)) {
//                        cacheMetricsRegistrar.bindCacheToRegistry(cacheWrapper);
//                    }
                    return cacheWrapper;
                });
    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
