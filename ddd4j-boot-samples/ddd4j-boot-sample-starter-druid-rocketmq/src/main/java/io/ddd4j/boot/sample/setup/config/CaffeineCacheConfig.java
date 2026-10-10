package io.ddd4j.boot.sample.setup.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caffeine 本地缓存装配配置：注册缓存加载器，使 {@code refreshAfterWrite} 刷新策略生效。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Configuration
public class CaffeineCacheConfig {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public CaffeineCacheConfig() {
    }

    /**
     * 必须要指定这个Bean，refreshAfterWrite=5s这个配置属性才生效
     *
     * @return 缓存加载器，未命中时返回 {@code null}，刷新时沿用旧值
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
