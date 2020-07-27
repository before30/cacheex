package cc.before30.metricex.cacheex.core.cache.binder;

import cc.before30.metricex.cacheex.core.cache.AbstractCustomCache;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.cache.CacheMeterBinder;

/**
 * RedisCacheMetrics
 *
 * @author before30
 * @since 2020/07/27
 */
public class CustomCacheMetrics extends CacheMeterBinder {

    private final AbstractCustomCache customCache;

    public static <C extends AbstractCustomCache> C monitor(MeterRegistry registry, C cache,
                                                            String cacheName, String... tags) {
        return monitor(registry, cache, cacheName, Tags.of(tags));
    }

    public static <C extends AbstractCustomCache> C monitor(MeterRegistry registry, C cache,
                                                            String cacheName, Iterable<Tag> tags) {
        new CustomCacheMetrics(cache, cacheName, tags).bindTo(registry);
        return cache;
    }

    public CustomCacheMetrics(AbstractCustomCache cache, String cacheName, Iterable<Tag> tags) {
        super(cache, cacheName, tags);
        this.customCache = cache;
    }

    @Override
    protected Long size() {
        return (long) 0;
    }

    @Override
    protected long hitCount() {
        return customCache.stats().getHitCount();
    }

    @Override
    protected Long missCount() {
        return customCache.stats().getMissCount();
    }

    @Override
    protected Long evictionCount() {
        return customCache.stats().getEvictionCount();
    }

    @Override
    protected long putCount() {
        return customCache.stats().getPutCount();
    }

    @Override
    protected void bindImplementationSpecificMetrics(MeterRegistry registry) {

    }
}
