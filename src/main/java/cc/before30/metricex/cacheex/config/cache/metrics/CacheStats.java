package cc.before30.metricex.cacheex.config.cache.metrics;

import lombok.Getter;
import org.springframework.data.annotation.Immutable;

/**
 * CacheStats
 *
 * @author before30
 * @since 2020/07/17
 */
@Immutable
public class CacheStats {
    private static final CacheStats EMPTY_STATS = new CacheStats(0, 0, 0, 0);

    @Getter
    private final long hitCount;
    @Getter
    private final long missCount;
    @Getter
    private final long putCount;
    @Getter
    private final long evictionCount;


    public CacheStats(long hitCount,
                      long missCount,
                      long putCount,
                      long evictionCount) {
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.putCount = putCount;
        this.evictionCount = evictionCount;
    }

    public static CacheStats empty() {
        return EMPTY_STATS;
    }

    public long getRequestCount() {
        return hitCount + missCount;
    }

    public double hitRate() {
        long requestCount = getRequestCount();
        return (requestCount == 0) ? 1.0 : (double) hitCount / requestCount;
    }

    @Override
    public String toString() {
        return "CacheStats{" +
                "hitCount=" + hitCount +
                ", missCount=" + missCount +
                ", putCount=" + putCount +
                ", evictionCount=" + evictionCount +
                '}';
    }
}