package cc.before30.metricex.cacheex.core.cache;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.decorators.Decorators;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * CircuitCustomCacheWrapper
 *
 * @author before30
 * @since 2020/07/27
 */
@Slf4j
@RequiredArgsConstructor
public class CircuitCustomCacheWrapper extends AbstractCustomCache {

    private final Cache delegate;
    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;

    @Override
    protected String doGetName() {
        return delegate.getName();
    }

    @Override
    protected Object doGetNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    protected ValueWrapper doGet(Object key) {
        Supplier<ValueWrapper> supplier = Decorators
                .ofSupplier(() -> delegate.get(key))
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        return Try.ofSupplier(supplier)
                .recover((ex) -> {
                    log.trace("recover. {} {}",ex, ex.getMessage());
                    return null;
                })
                .get();
    }

    @Override
    protected <T> T doGet(Object key, Class<T> type) {
        Supplier<T> supplier = Decorators
                .ofSupplier(() -> delegate.get(key, type))
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        return Try.ofSupplier(supplier)
                .recover(ex -> null)
                .get();
    }

    @Override
    protected <T> T doGet(Object key, Callable<T> valueLoader) {
        Supplier<T> supplier = Decorators
                .ofSupplier(() -> delegate.get(key, valueLoader))
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        return Try.ofSupplier(supplier)
                .recover(ex -> null)
                .get();
    }

    @Override
    protected void doPut(Object key, Object value) {
        Runnable decorate = Decorators.ofRunnable(() -> delegate.put(key, value))
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        Try.runRunnable(decorate)
                .recover((ex) -> {
                    log.trace("error... {} {}", ex, ex.getMessage());
                    return null;
                })
                .get();
    }

    @Override
    protected ValueWrapper doPutIfAbsent(Object key, Object value) {
        Supplier<ValueWrapper> supplier = Decorators.ofSupplier(() -> delegate.putIfAbsent(key, value))
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        return Try.ofSupplier(supplier)
                .recover(ex -> null)
                .get();
    }

    @Override
    protected void doEvict(Object key) {
        Runnable decorate = Decorators.ofRunnable(() -> delegate.evict(key))
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        Try.runRunnable(decorate)
                .recover((ex) -> {
                    log.trace("evict failed. {}", ex.getMessage());
                    return null;
                })
                .get();
    }

    @Override
    protected boolean doEvictIfPresent(Object key) {
        Supplier<Boolean> supplier = Decorators.ofSupplier(() -> delegate.evictIfPresent(key))
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        return Try.ofSupplier(supplier)
                .recover(ex -> false)
                .get();
    }

    @Override
    protected void doClear() {
        Runnable decorate = Decorators.ofRunnable(delegate::clear)
                .withCircuitBreaker(circuitBreaker)
                .withBulkhead(bulkhead)
                .decorate();

        Try.runRunnable(decorate)
                .recover(ex -> null)
                .get();
    }
}
