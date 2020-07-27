package cc.before30.metricex.cacheex.core.cache.metrics;

import cc.before30.metricex.cacheex.core.cache.AbstractCustomCache;
import cc.before30.metricex.cacheex.core.cache.binder.CustomCacheMetrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider;

/**
 * CustomCacheMetricsBinderProvider
 *
 * @author before30
 * @since 2020/07/27
 */
public class CustomCacheMetricsBinderProvider implements CacheMeterBinderProvider<AbstractCustomCache> {
    @Override
    public MeterBinder getMeterBinder(AbstractCustomCache cache, Iterable<Tag> tags) {
        return new CustomCacheMetrics(cache, cache.getName(), tags);
    }
}
