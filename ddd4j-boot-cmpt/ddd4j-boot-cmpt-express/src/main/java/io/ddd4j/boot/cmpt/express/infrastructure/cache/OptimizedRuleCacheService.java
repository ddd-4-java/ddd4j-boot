package io.ddd4j.boot.cmpt.express.infrastructure.cache;

import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OptimizedRuleCacheService {

@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 使用本地缓存 + Redis缓存的二级缓存策略
private final Cache<String, RuleDefinition> localCache =
        CacheProperties.Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();

/**
 * 获取规则定义（二级缓存）
 */
public RuleDefinition getRuleDefinition(String ruleCode) {
    // 1. 先查本地缓存
    RuleDefinition rule = localCache.getIfPresent(ruleCode);
    if (rule != null) {
        return rule;
    }

    // 2. 查Redis缓存
    String cacheKey = "rule_engine:rule:" + ruleCode;
    rule = (RuleDefinition) redisTemplate.opsForValue().get(cacheKey);
    if (rule != null) {
        localCache.put(ruleCode, rule);
        return rule;
    }

    // 3. 查数据库并更新缓存
    // 实现逻辑...
    return null;
}
}