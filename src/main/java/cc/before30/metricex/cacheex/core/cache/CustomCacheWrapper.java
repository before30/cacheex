package cc.before30.metricex.cacheex.core.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * CustomCacheWrapper
 *
 * @author before30
 * @since 2020/07/27
 */
@RequiredArgsConstructor
public class CustomCacheWrapper extends AbstractCustomCache {

    private final Cache delegate;

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
        return delegate.get(key);
    }

    @Override
    protected <T> T doGet(Object key, Class<T> type) {
        return delegate.get(key, type);
    }

    @Override
    protected <T> T doGet(Object key, Callable<T> valueLoader) {
        return delegate.get(key, valueLoader);
    }

    @Override
    protected void doPut(Object key, Object value) {
        delegate.put(key, value);
    }

    @Override
    protected ValueWrapper doPutIfAbsent(Object key, Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    protected void doEvict(Object key) {
        delegate.evict(key);
    }

    @Override
    protected boolean doEvictIfPresent(Object key) {
        return delegate.evictIfPresent(key);
    }

    @Override
    protected void doClear() {
        delegate.clear();
    }
}
