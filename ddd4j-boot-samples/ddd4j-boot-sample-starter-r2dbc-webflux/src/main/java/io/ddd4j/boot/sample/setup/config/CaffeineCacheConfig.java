package io.ddd4j.boot.sample.setup.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caffeine 本地缓存配置：声明 {@code CacheLoader} Bean，使 refreshAfterWrite=5s 自动刷新生效。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Configuration
public class CaffeineCacheConfig {

    /**
     * 构造 Caffeine 缓存配置对象。
     */
    public CaffeineCacheConfig() {
    }

    /**
     * 必须要指定这个Bean，refreshAfterWrite=5s这个配置属性才生效
     *
     * @return 缓存加载器，load 返回空值、reload 回传旧值以实现缓存刷新
     */
    @Bean
    public CacheLoader<Object, Object> cacheLoader() {

        CacheLoader<Object, Object> cacheLoader = new CacheLoader<Object, Object>() {

            @Override
            public Object load(Object key) throws Exception {
                return null;
            }

            // 重写这个方法将oldValue值返回回去，进而刷新缓存
            @Override
            public Object reload(Object key, Object oldValue) throws Exception {
                return oldValue;
            }
        };

        return cacheLoader;
    }

}
