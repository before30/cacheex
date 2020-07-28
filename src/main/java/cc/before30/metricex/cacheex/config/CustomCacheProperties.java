package cc.before30.metricex.cacheex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CustomCacheProperties
 *
 * @author before30
 * @since 2020/07/18
 */
@ConfigurationProperties(prefix = "spring.cache")
public class CustomCacheProperties {
    private List<String> cacheNames;

    public List<String> getCacheNames() {
        return cacheNames;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }
}
