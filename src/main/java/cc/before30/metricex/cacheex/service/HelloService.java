package cc.before30.metricex.cacheex.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * HelloService
 *
 * @author before30
 * @since 2020/07/16
 */
@Service
@Slf4j
public class HelloService {

    @Cacheable(value = "TEST_NAME", key = "#id")
    public String put(Long id) {
        log.info("{} will cached.");
        return id + ": will cached:" + System.currentTimeMillis();
    }

    @CacheEvict(value = "TEST_NAME", key = "#id")
    public String delete(Long id) {
        log.info("{} will delted.");
        return "Deleted";
    }

    @Cacheable(value = "TEST_NAME_2", key = "#id")
    public String put2(Long id) {
        return String.valueOf(id);
    }
}
