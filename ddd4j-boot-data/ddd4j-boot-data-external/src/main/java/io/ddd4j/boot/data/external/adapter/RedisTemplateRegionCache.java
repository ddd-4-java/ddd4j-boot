package io.ddd4j.boot.data.external.adapter;

import io.ddd4j.data.external.region.RegionCache;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Objects;

/**
 * 使用 Spring Data Redis 原生字符串模板实现区域缓存端口。
 */
public class RedisTemplateRegionCache implements RegionCache {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisTemplateRegionCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = Objects.requireNonNull(
                stringRedisTemplate, "stringRedisTemplate must not be null");
    }

    @Override
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }
}
