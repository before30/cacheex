package cc.before30.metricex.cacheex.config.cache.metrics;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * CustomCacheWithCircuitWrapper
 *
 * @author before30
 * @since 2020/07/18
 */
@Slf4j
public class CircuitCacheWrapper extends CustomCacheWrapper {

    private final Cache delegate;
    private final CircuitBreaker circuitBreaker;
    private final CacheStatsCounter cacheStatsCounter = new CacheStatsCounter();

    public CircuitCacheWrapper(Cache delegate, CircuitBreaker circuitBreaker) {
        super(delegate);
        this.delegate = delegate;
        this.circuitBreaker = circuitBreaker;
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
        Supplier<ValueWrapper> supplier = Decorators
                .ofSupplier(() -> delegate.get(key))
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        ValueWrapper result = Try.ofSupplier(supplier)
                .recover((ex) -> {
                    log.error("recover. {} {}",ex, ex.getMessage());
                    return null;
                })
                .get();
        afterGet(result);

        return result;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Supplier<T> supplier = Decorators
                .ofSupplier(() -> delegate.get(key, type))
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        T result = Try.ofSupplier(supplier)
                .recover(ex -> null)
                .get();
        afterGet(result);

        return result;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Supplier<T> supplier = Decorators
                .ofSupplier(() -> delegate.get(key, valueLoader))
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        T result = Try.ofSupplier(supplier)
                .recover(ex -> null)
                .get();
        afterGet(result);

        return result;
    }

    @Override
    public void put(Object key, Object value) {
        Runnable decorate = Decorators.ofRunnable(() -> delegate.put(key, value))
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        Try.runRunnable(decorate)
                .recover((ex) -> {
                    log.error("error... {} {}", ex, ex.getMessage());
                    return null;
                })
                .get();
        afterPut();
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Supplier<ValueWrapper> supplier = Decorators.ofSupplier(() -> delegate.putIfAbsent(key, value))
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        ValueWrapper result = Try.ofSupplier(supplier)
                .recover(ex -> null)
                .get();
        afterPut();

        return result;
    }

    @Override
    public void evict(Object key) {
        Runnable decorate = Decorators.ofRunnable(() -> delegate.evict(key))
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        Try.runRunnable(decorate)
                .recover((ex) -> {
                    log.error("evict failed. {}", ex.getMessage());
                    return null;
                })
                .get();

        afterEvict();
    }

    @Override
    public void clear() {
        Runnable decorate = Decorators.ofRunnable(() -> delegate.clear())
                .withCircuitBreaker(circuitBreaker)
                .decorate();

        Try.runRunnable(decorate)
                .recover(ex -> null)
                .get();
    }

    public CacheStats stats() {
        return cacheStatsCounter.snapshot();
    }

    private void afterGet(Object value) {
        if (Objects.isNull(value)) {
            // cache miss
            cacheStatsCounter.recordMiss();
        } else {
            // cache hit
            cacheStatsCounter.recordHit();
        }
    }

    private void afterPut() {
        cacheStatsCounter.recordPut();
    }

    private void afterEvict() {
        cacheStatsCounter.recordEviction();
    }
}
