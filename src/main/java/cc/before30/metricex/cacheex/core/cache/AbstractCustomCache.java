package cc.before30.metricex.cacheex.core.cache;

import cc.before30.metricex.cacheex.core.cache.stats.CacheStats;
import cc.before30.metricex.cacheex.core.cache.stats.CacheStatsCounter;
import org.springframework.cache.Cache;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * AbstractCustomCache
 *
 * @author before30
 * @since 2020/07/27
 */
public abstract class AbstractCustomCache implements Cache {

    private final CacheStatsCounter cacheStatsCounter = new CacheStatsCounter();

    protected abstract String doGetName();

    @Override
    public String getName() {
        return doGetName();
    }

    protected abstract Object doGetNativeCache();

    @Override
    public Object getNativeCache() {
        return doGetNativeCache();
    }

    protected abstract ValueWrapper doGet(Object key);

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = doGet(key);
        onAfterGet(valueWrapper);
        return valueWrapper;
    }

    protected abstract <T> T doGet(Object key, Class<T> type);

    @Override
    public <T> T get(Object key, Class<T> type) {
        T valueWrapper = doGet(key, type);
        onAfterGet(valueWrapper);
        return valueWrapper;
    }

    protected abstract <T> T doGet(Object key, Callable<T> valueLoader);

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T valueWrapper = doGet(key, valueLoader);
        onAfterGet(valueWrapper);
        return valueWrapper;
    }

    protected abstract void doPut(Object key, Object value);

    @Override
    public void put(Object key, Object value) {
        doPut(key, value);
        onAfterPut();
    }

    protected abstract ValueWrapper doPutIfAbsent(Object key, Object value);

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper valueWrapper = doPutIfAbsent(key, value);
        onAfterPut();
        return valueWrapper;
    }

    protected abstract void doEvict(Object key);

    @Override
    public void evict(Object key) {
        doEvict(key);
        onAfterEvict();
    }

    protected abstract boolean doEvictIfPresent(Object key);

    @Override
    public boolean evictIfPresent(Object key) {
        boolean result = doEvictIfPresent(key);
        if (result) {
            onAfterEvict();
        }

        return result;
    }

    protected abstract void doClear();

    @Override
    public void clear() {
        doClear();
    }

    @Override
    public boolean invalidate() {
        clear();
        return false;
    }

    public CacheStats stats() {
        return cacheStatsCounter.snapshot();
    }

    private void onAfterGet(Object value) {
        if (Objects.isNull(value)) {
            // cache miss
            cacheStatsCounter.recordMiss();
        } else {
            // cache hit
            cacheStatsCounter.recordHit();
        }
    }

    private void onAfterPut() {
        cacheStatsCounter.recordPut();
    }

    private void onAfterEvict() {
        cacheStatsCounter.recordEviction();
    }

}
