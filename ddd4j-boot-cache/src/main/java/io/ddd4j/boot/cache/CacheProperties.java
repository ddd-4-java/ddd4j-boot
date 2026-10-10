package io.ddd4j.boot.cache;

import io.ddd4j.cache.CacheKit;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ddd4j 缓存配置属性。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Getter
@ConfigurationProperties(prefix = "ddd4j.cache")
public class CacheProperties {

    /**
     * 默认本地缓存实现类型（CAFFEINE / GUAVA / HUTOOL）
     */
    private CacheKit.LocalCacheType defaultType = CacheKit.LocalCacheType.CAFFEINE;

    /**
     * 显式无参构造器，供 {@code @EnableConfigurationProperties} 绑定时实例化。
     */
    public CacheProperties() {
    }

    /**
     * 设置默认本地缓存实现类型，同时同步到 {@link CacheKit} 静态默认值。
     *
     * @param defaultType 本地缓存实现类型（CAFFEINE / GUAVA / HUTOOL）
     */
    public void setDefaultType(CacheKit.LocalCacheType defaultType) {
        this.defaultType = defaultType;
        CacheKit.setDefaultType(defaultType);
    }

}
