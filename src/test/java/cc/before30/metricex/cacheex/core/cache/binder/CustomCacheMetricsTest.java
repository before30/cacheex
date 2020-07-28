package cc.before30.metricex.cacheex.core.cache.binder;

import cc.before30.metricex.cacheex.core.cache.CustomCacheWrapper;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.search.RequiredSearch;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CustomCacheMetricsTest
 *
 * @author before30
 * @since 2020/07/28
 */
class CustomCacheMetricsTest {

    private Tags expectedTag = Tags.of("app", "test");
    private Cache delegate = new ConcurrentMapCache("test_cache");
    private CustomCacheWrapper cache = new CustomCacheWrapper(delegate);
    private CustomCacheMetrics metrics = new CustomCacheMetrics(cache, cache.getName(), expectedTag);

    private void verifyCommonCacheMetrics(MeterRegistry meterRegistry, CustomCacheMetrics meterBinder) {
        meterRegistry.get("cache.puts").tags(expectedTag).functionCounter();
        meterRegistry.get("cache.gets").tags(expectedTag).tag("result", "hit").functionCounter();

        if (meterBinder.size() != null) {
            meterRegistry.get("cache.size").tags(expectedTag).gauge();
        }
        if (meterBinder.missCount() != null) {
            meterRegistry.get("cache.gets").tags(expectedTag).tag("result", "miss").functionCounter();
        }
        if (meterBinder.evictionCount() != null) {
            meterRegistry.get("cache.evictions").tags(expectedTag).functionCounter();
        }
    }

    private RequiredSearch fetch(MeterRegistry meterRegistry, String name) {
        return fetch(meterRegistry, name, expectedTag);
    }

    private RequiredSearch fetch(MeterRegistry meterRegistry, String name, Iterable<Tag> tags) {
        return meterRegistry.get(name).tags(tags);
    }

    @Test
    public void reportExpectedGeneralMetrics() {
        MeterRegistry registry = new SimpleMeterRegistry();
        metrics.bindTo(registry);

        verifyCommonCacheMetrics(registry, metrics);

        Gauge cacheSize = fetch(registry, "cache.size").gauge();
        assertThat(cacheSize.value()).isEqualTo(0.0);

        FunctionCounter hitCount = fetch(registry, "cache.gets", Tags.of("result", "hit")).functionCounter();
        assertThat(hitCount.count()).isEqualTo(metrics.hitCount());

        FunctionCounter missCount = fetch(registry, "cache.gets", Tags.of("result", "miss")).functionCounter();
        assertThat(missCount.count()).isEqualTo(metrics.missCount().doubleValue());

        FunctionCounter cachePuts = fetch(registry, "cache.puts").functionCounter();
        assertThat(cachePuts.count()).isEqualTo(metrics.putCount());

        FunctionCounter cacheEviction = fetch(registry, "cache.evictions").functionCounter();
        assertThat(cacheEviction.count()).isEqualTo(metrics.evictionCount().doubleValue());
    }

    @Test
    public void constructInstanceViaStaticMethodMonitor() {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        CustomCacheMetrics.monitor(meterRegistry, cache, cache.getName(), expectedTag);

        assertNotNull(meterRegistry.get("cache.gets").tags(expectedTag).functionCounter());
    }

    @Test
    void returnHitCount() {
        assertThat(metrics.hitCount()).isEqualTo(cache.stats().getHitCount());
    }

    @Test
    void returnMissCount() {
        assertThat(metrics.missCount()).isEqualTo(cache.stats().getMissCount());
    }

    @Test
    void returnEvictionCount() {
        assertThat(metrics.evictionCount()).isEqualTo(cache.stats().getEvictionCount());
    }

    @Test
    void returnPutCount() {
        assertThat(metrics.putCount()).isEqualTo(cache.stats().getPutCount());
    }
}