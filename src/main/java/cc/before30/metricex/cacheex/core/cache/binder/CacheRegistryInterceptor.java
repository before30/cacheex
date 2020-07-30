package cc.before30.metricex.cacheex.core.cache.binder;

import cc.before30.metricex.cacheex.core.cache.CustomCacheWrapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CacheRegistryInterceptor extends CacheInterceptor {
    private final MeterRegistry registry;
    private final Map<String, CacheManager> managers;

    public CacheRegistryInterceptor(CachingConfigurer configurer, CacheOperationSource cacheOperationSource,
                                    MeterRegistry registry, Map<String, CacheManager> managers) {
        configure(configurer::errorHandler, configurer::keyGenerator, configurer::cacheResolver, configurer::cacheManager);
        setCacheOperationSource(cacheOperationSource);
        this.registry = registry;
        this.managers = managers;
    }

    @Override
    protected Collection<? extends Cache> getCaches(CacheOperationInvocationContext<CacheOperation> context, CacheResolver cacheResolver) {
        Collection<? extends Cache> caches = super.getCaches(context, cacheResolver);
        List<CustomCacheWrapper> statsEnabledList = caches.stream().map(this::toWrapper).collect(Collectors.toList());
        for (CustomCacheWrapper statsEnabled : statsEnabledList) {
            String cacheName = statsEnabled.getName();
            String cacheManagerName = findManagerName(cacheName);
            Tags tags = Tags.of(Tag.of("cacheManager", cacheManagerName), Tag.of("name", cacheName));
            CacheMeterBinder binder = new CustomCacheMetrics(statsEnabled, cacheName, tags);
            CacheMetricsRegistrar registrar = new CacheMetricsRegistrar(registry, Arrays.asList((a, b) -> binder));
            registrar.bindCacheToRegistry(statsEnabled);
        }

        return caches;
    }

    private CustomCacheWrapper toWrapper(Cache cache) {
        if (cache instanceof CustomCacheWrapper) {
            return (CustomCacheWrapper) cache;
        } else {
            return new CustomCacheWrapper(cache);
        }
    }

    private String findManagerName(String cacheName) {
        for (Map.Entry<String, CacheManager> entry : managers.entrySet()) {

            if (entry.getValue().getCacheNames().stream().filter(cacheName::equals).findAny().isPresent()) {
                return entry.getKey();
            }
        }

        return "unknown";
    }
}

