package io.ddd4j.boot.sample.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caffeine 缓存加载器配置。
 *
 * <p>注册 {@link CacheLoader} Bean 后，配置项 refreshAfterWrite=5s 的
 * 异步刷新语义才会真正生效。
 */
@Configuration
public class CaffeineCacheConfig {

    /**
     * 构造 Caffeine 缓存配置类实例。
     *
     */
    public CaffeineCacheConfig() {
    }

    /**
     * 必须要指定这个Bean，refreshAfterWrite=5s这个配置属性才生效。
     *
     * <p>默认加载返回 null；重写 reload 后刷新时把旧值原样返回，
     * 避免刷新期间出现缓存空窗。
     *
     * @return 缓存加载器 Bean
     */
    @Bean
    public CacheLoader<Object, Object> cacheLoader() {

        CacheLoader<Object, Object> cacheLoader = new CacheLoader<Object, Object>() {

            /**
             * 缓存未命中时的加载逻辑，默认返回 null。
             *
             * @param key 缓存键
             * @return 默认返回 null，交由上层决定未命中处理
             * @throws Exception 加载过程中的异常
             */
            @Override
            public Object load(Object key) throws Exception {
                return null;
            }

            // 重写这个方法将oldValue值返回回去，进而刷新缓存
            /**
             * 刷新缓存时沿用旧值，避免刷新期间出现空窗。
             *
             * @param key      缓存键
             * @param oldValue 刷新前的旧值
             * @return 原样返回的旧值
             * @throws Exception 刷新过程中的异常
             */
            @Override
            public Object reload(Object key, Object oldValue) throws Exception {
                return oldValue;
            }
        };

        return cacheLoader;
    }

}
