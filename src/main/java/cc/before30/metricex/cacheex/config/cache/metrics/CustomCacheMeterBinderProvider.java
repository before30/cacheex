package cc.before30.metricex.cacheex.config.cache.metrics;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider;

/**
 * CustomCacheMeterBinderProvider
 *
 * @author before30
 * @since 2020/07/17
 */
public class CustomCacheMeterBinderProvider implements CacheMeterBinderProvider<CustomCacheWrapper> {
    @Override
    public MeterBinder getMeterBinder(CustomCacheWrapper cache, Iterable<Tag> tags) {
        return new CustomCacheMeterBinder(cache, cache.getName(), tags);
    }
}
