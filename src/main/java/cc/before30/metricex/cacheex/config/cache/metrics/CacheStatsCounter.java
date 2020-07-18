package cc.before30.metricex.cacheex.config.cache.metrics;

import java.util.concurrent.atomic.LongAdder;

/**
 * CacheStatsCounter
 *
 * @author before30
 * @since 2020/07/17
 */
public class CacheStatsCounter {

    private final LongAdder hitCount;

    private final LongAdder missCount;

    private final LongAdder putCount;

    private final LongAdder evictionCount;

    public CacheStatsCounter() {
        hitCount = new LongAdder();
        missCount = new LongAdder();
        putCount = new LongAdder();
        evictionCount = new LongAdder();
    }

    public void recordHits(int count) {
        hitCount.add(count);
    }

    public void recordHit() {
        recordHits(1);
    }

    public void recordMisses(int count) {
        missCount.add(count);
    }

    public void recordMiss() {
        recordMisses(1);
    }

    public void recordPuts(int count) {
        putCount.add(count);
    }

    public void recordPut() {
        recordPuts(1);
    }

    public void recordEvictions(int count) {
        evictionCount.add(count);
    }

    public void recordEviction() {
        recordEvictions(1);
    }

    public CacheStats snapshot() {
        return new CacheStats(
                negativeToMaxValue(hitCount.sum()),
                negativeToMaxValue(missCount.sum()),
                negativeToMaxValue(putCount.sum()),
                negativeToMaxValue(evictionCount.sum())
        );
    }

    private static long negativeToMaxValue(long value) {
        return (value >= 0) ? value : Long.MAX_VALUE;
    }

    public void incrementBy(CacheStatsCounter other) {
        CacheStats otherStats = other.snapshot();
        hitCount.add(otherStats.getHitCount());
        missCount.add(otherStats.getMissCount());
        putCount.add(otherStats.getPutCount());
        evictionCount.add(otherStats.getEvictionCount());
    }

    @Override
    public String toString() {
        return snapshot().toString();
    }
}
