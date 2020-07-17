package cc.before30.metricex.cacheex.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * CacheExConfig
 *
 * @author before30
 * @since 2020/07/16
 */
@Configuration
public class CacheExConfig {

    private RedisServer redisServer;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(MeterRegistry registry) {
        CircuitBreakerConfig defaultConfig = CircuitBreakerConfig
                .ofDefaults();

        CircuitBreakerRegistry circuit = CircuitBreakerRegistry.of(defaultConfig);

        TaggedCircuitBreakerMetrics
                .ofCircuitBreakerRegistry(circuit)
                .bindTo(registry);
        return circuit;
    }

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("redis-circuit-breaker");
    }

    @PostConstruct
    public void redisServer() {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
