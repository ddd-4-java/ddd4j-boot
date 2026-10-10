package io.ddd4j.boot.sample.setup.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caffeine 本地缓存装配配置。
 * <p>
 * 声明 {@code CacheLoader} Bean，使缓存的 {@code refreshAfterWrite} 刷新策略生效。
 * </p>
 */
@Configuration
public class CaffeineCacheConfig {

    /**
     * 构造 Caffeine 缓存配置类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public CaffeineCacheConfig() {
    }

    /**
     * 必须要指定这个Bean，refreshAfterWrite=5s这个配置属性才生效
     *
     * @return 刷新时保留旧值、加载时返回空的缓存加载器
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
