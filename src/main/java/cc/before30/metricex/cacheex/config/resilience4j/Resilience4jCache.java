package cc.before30.metricex.cacheex.config.resilience4j;

import com.google.common.base.Joiner;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.support.NoOpCache;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Resilience4jCache
 *
 * @author before30
 * @since 2020/07/17
 */
@Slf4j
@RequiredArgsConstructor
public class Resilience4jCache implements Cache {

    private final Cache delegate;
    private final CircuitBreaker circuitBreaker;
    private final Cache failSafeCache = new NoOpCache("fail-safe-cache");
    private final MeterRegistry meterRegistry;

    // Metric Name
    private final static String CACHE_GETS = "cache.gets";

    // Tag-Key
    private final static String RESULT = "result";
    private final static String CACHE_NAME = "cache-name";

    // Tag-Value
    private final static String HITS = "hits";
    private final static String MISS = "miss";

    private void cacheGets(String result) {
        Counter.builder(CACHE_GETS)
                .tag(RESULT, result)
                .tag(CACHE_NAME, getName())
                .register(this.meterRegistry)
                .increment();
    }


    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return Try
                .of(() -> Decorators.ofCheckedSupplier(() -> delegate.get(key))
                        .withCircuitBreaker(circuitBreaker)
                        .decorate().apply())
                .onSuccess(it -> {
                    if (it == null) {
                        cacheGets(MISS);
                    } else {
                        cacheGets(HITS);
                    }
                    log.info("call with key");
                })
                .recoverWith(ex -> Try.success(failSafeCache.get(key))) // 이거 왜?
                .get();
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return Try
                .of(() -> Decorators.ofCheckedSupplier(() -> delegate.get(key, type))
                        .withCircuitBreaker(circuitBreaker)
                        .decorate().apply())
                .onSuccess(it -> {
                    cacheGets(HITS);
                    log.info("call get with type");
                })
                .onFailure(it -> {
                    cacheGets(MISS);
                })
                .recoverWith(ex -> Try.success(failSafeCache.get(key, type))) // 이거 왜?
                .get();
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return Try
                .of(() -> Decorators.ofCheckedSupplier(() -> delegate.get(key, valueLoader))
                        .withCircuitBreaker(circuitBreaker)
                        .decorate().apply())
                .onSuccess(it -> {
                    cacheGets(HITS);
                    log.info("call get with valueLoader");
                })
                .onFailure(it -> {
                    cacheGets(MISS);
                })
                .recoverWith(ex -> Try.success(failSafeCache.get(key, valueLoader))) // 이거 왜?
                .get();
    }

    @Override
    public void put(Object key, Object value) {
        delegate.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        Try.of(() -> Decorators.ofCheckedSupplier(
                () -> {
                    delegate.evict(key);
                    return true;
                })
                .withCircuitBreaker(circuitBreaker)
                .decorate().apply())
                .onFailure(it -> {
                    log.error("failed to evict cache. cacheName: {}, key: {} error: {}", delegate.getName(), key, it.getLocalizedMessage());
                })
                .get();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

}
