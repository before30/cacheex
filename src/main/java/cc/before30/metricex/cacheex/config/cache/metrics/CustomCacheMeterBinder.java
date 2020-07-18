package cc.before30.metricex.cacheex.config.cache.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;

/**
 * CustomCacheMetrics
 *
 * @author before30
 * @since 2020/07/17
 */
public class CustomCacheMeterBinder extends CacheMeterBinder {

    private final CustomCacheWrapper cache;

    public CustomCacheMeterBinder(CustomCacheWrapper cache, String cacheName, Iterable<Tag> tags) {
        super(cache, cacheName, tags);
        this.cache = cache;
    }

    @Override
    protected Long size() {
        return Long.valueOf(0);
    }

    @Override
    protected long hitCount() {
        return cache.stats().getHitCount();
    }

    @Override
    protected Long missCount() {
        return cache.stats().getMissCount();
    }

    @Override
    protected Long evictionCount() {
        return cache.stats().getEvictionCount();
    }

    @Override
    protected long putCount() {
        return cache.stats().getPutCount();
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {

    }
}
