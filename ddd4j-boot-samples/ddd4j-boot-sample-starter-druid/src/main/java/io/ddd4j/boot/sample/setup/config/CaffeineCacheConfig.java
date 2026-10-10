package io.ddd4j.boot.sample.setup.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CaffeineCacheConfig 类
 */
@Configuration
public class CaffeineCacheConfig {
    /**
     * 构造 CaffeineCacheConfig 实例。
     *
     */
    public CaffeineCacheConfig() {
    }

    /**
     * 必须要指定这个Bean，refreshAfterWrite=5s这个配置属性才生效
     *
     * @return 缓存加载器
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
