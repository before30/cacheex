package cc.before30.metricex.cacheex.config;

import cc.before30.metricex.cacheex.config.cache.CustomCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * CacheBinder
 *
 * @author before30
 * @since 2020/07/17
 */
//@Component
@RequiredArgsConstructor
public class CacheBinder {

//    private final CacheManager cacheManager;
//    private final CacheMetricsRegistrar cacheMetricsRegistrar;
//
//    @PostConstruct
//    public void init() {
//        if (cacheManager instanceof CustomCacheManager) {
//            ((CustomCacheManager)cacheManager).setCacheMetricsRegistrar(cacheMetricsRegistrar);
//        }
//    }
}
