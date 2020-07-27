# CacheEx

Micrometer 는 Cache Monitoring 위해 몇가지 빌트인 바인더를 제공한다. 
* [Guava](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/GuavaCacheMetrics.java#L31)
* [EhCache](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/EhCache2Metrics.java#L28)
* [Hazecast](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/HazelcastCacheMetrics.java#L27)
* [Caffeine](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/CaffeineCacheMetrics.java#L42)
* [JCache](https://github.com/micrometer-metrics/micrometer/blob/master/micrometer-core/src/main/java/io/micrometer/core/instrument/binder/cache/JCacheMetrics.java)

CacheEx는 위와 같은 방식으로 RedisCache에 필요한 바인더를 구현한다.

Binder에서 사용할 Cache는 기존 Spring Cache를 Wrapping하는 형태로 구현한다. 공통 구현인 통계를 뽑는 부분은 AbstractCustomCache에 구현한다.
* AbstractCustomCache
* CustomCacheWrapper
* CircuitCustomCacheWrapper


