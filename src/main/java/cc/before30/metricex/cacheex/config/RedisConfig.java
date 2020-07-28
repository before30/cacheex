package cc.before30.metricex.cacheex.config;

import cc.before30.metricex.cacheex.core.cache.manager.CircuitCacheManager;
import cc.before30.metricex.cacheex.core.cache.manager.CustomCacheManager;
import cc.before30.metricex.cacheex.core.cache.metrics.CustomCacheMetricsBinderProvider;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * RedicConfig
 *
 * @author before30
 * @since 2020/07/16
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig extends CachingConfigurerSupport {

    private final CustomCacheProperties cacheProperties;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder().commandTimeout(Duration.ofMillis(50)).build();

        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    @Bean
    @Primary
    @Override
    public CacheManager cacheManager() {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10));

        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .build();

        CustomCacheManager cacheManager = new CustomCacheManager(redisCacheManager);
        cacheProperties.getCacheNames().forEach(cacheManager::getCache);

        return cacheManager;
    }

    @Bean
    public CacheManager circuitCacheManager() {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10));

        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .build();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("cache-circuit");
        Bulkhead bulkhead = bulkheadRegistry.bulkhead("cache-bulkhead");

        CircuitCacheManager circuitCacheManager = new CircuitCacheManager(redisCacheManager, circuitBreaker, bulkhead);
        circuitCacheManager.getCache("circuit_cache");
        return circuitCacheManager;
    }

    @Bean
    public CustomCacheMetricsBinderProvider customCacheMetricsBinderProvider() {
        return new CustomCacheMetricsBinderProvider();
    }
}
