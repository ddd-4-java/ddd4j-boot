package io.ddd4j.boot.data.external.adapter;

import io.ddd4j.data.external.region.RegionCache;
import org.springframework.data.redis.core.RedisOperationTemplate;

import java.time.Duration;
import java.util.Objects;

/**
 * Adapts redistpl-plus RedisOperationTemplate to the ddd4j region cache port.
 */
public class RedisOperationRegionCache implements RegionCache {

    private final RedisOperationTemplate redisOperation;

    public RedisOperationRegionCache(RedisOperationTemplate redisOperation) {
        this.redisOperation = Objects.requireNonNull(redisOperation, "redisOperation must not be null");
    }

    @Override
    public String getString(String key) {
        return redisOperation.getString(key);
    }

    @Override
    public void set(String key, String value, Duration ttl) {
        redisOperation.set(key, value, ttl);
    }
}
