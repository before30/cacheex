package cc.before30.metricex.cacheex.config;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.actuate.metrics.cache.CacheMeterBinderProvider;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

/**
 * RedisCacheMeterBinderProvider
 *
 * @author before30
 * @since 2020/07/17
 */
//@Component
public class RedisCacheMeterBinderProvider implements CacheMeterBinderProvider<Cache> {

    @Override
    public MeterBinder getMeterBinder(Cache cache, Iterable<Tag> tags) {
        return null;
    }
}
