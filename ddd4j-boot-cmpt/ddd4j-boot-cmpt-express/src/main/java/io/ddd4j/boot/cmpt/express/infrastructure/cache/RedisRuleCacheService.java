package io.ddd4j.boot.cmpt.express.infrastructure.cache;

import io.ddd4j.boot.cmpt.express.application.service.RuleCacheService;
import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis规则缓存服务实现
 * 基础设施层：使用Redis实现缓存
 * 
 * 注意：此类通过ExpressAutoConfiguration自动配置，无需手动添加@Service注解
 */
public class RedisRuleCacheService implements RuleCacheService {

    private static final String RULE_CACHE_PREFIX = "rule_engine:rule:";
    private static final long RULE_CACHE_TTL = 300; // 5分钟缓存

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisRuleCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RuleDefinition get(String ruleCode) {
        String cacheKey = RULE_CACHE_PREFIX + ruleCode;
        return (RuleDefinition) redisTemplate.opsForValue().get(cacheKey);
    }

    @Override
    public void put(String ruleCode, RuleDefinition rule) {
        String cacheKey = RULE_CACHE_PREFIX + ruleCode;
        redisTemplate.opsForValue().set(cacheKey, rule, RULE_CACHE_TTL, TimeUnit.SECONDS);
    }

    @Override
    public void evict(String ruleCode) {
        String cacheKey = RULE_CACHE_PREFIX + ruleCode;
        redisTemplate.delete(cacheKey);
    }

    @Override
    public void evictAll() {
        Set<String> keys = redisTemplate.keys(RULE_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}

