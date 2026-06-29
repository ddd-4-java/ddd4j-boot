package io.ddd4j.boot.cache.config;

import io.ddd4j.cache.CacheKit;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ddd4j 缓存配置属性。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@ConfigurationProperties(prefix = "ddd4j.cache")
public class CacheProperties {

    /**
     * 默认本地缓存实现类型（CAFFEINE / GUAVA / HUTOOL）
     */
    private CacheKit.LocalCacheType defaultType = CacheKit.LocalCacheType.CAFFEINE;

    public CacheKit.LocalCacheType getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(CacheKit.LocalCacheType defaultType) {
        this.defaultType = defaultType;
        CacheKit.setDefaultType(defaultType);
    }

}
