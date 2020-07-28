package cc.before30.metricex.cacheex;

import cc.before30.metricex.cacheex.config.CustomCacheProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(CustomCacheProperties.class)
@SpringBootApplication
public class CacheExApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheExApplication.class, args);
    }

}
