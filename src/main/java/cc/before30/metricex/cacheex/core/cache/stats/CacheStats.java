package cc.before30.metricex.cacheex.core.cache.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Immutable;

/**
 * CacheStats
 *
 * @author before30
 * @since 2020/07/17
 */
@Immutable
@RequiredArgsConstructor
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