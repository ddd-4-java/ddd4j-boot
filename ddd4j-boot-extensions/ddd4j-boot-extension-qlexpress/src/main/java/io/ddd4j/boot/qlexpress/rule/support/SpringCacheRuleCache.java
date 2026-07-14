package io.ddd4j.boot.qlexpress.rule.support;

import io.ddd4j.boot.qlexpress.rule.RuleCache;
import io.ddd4j.boot.qlexpress.rule.RuleDefinition;
import org.springframework.cache.Cache;

import java.util.Objects;

/**
 * Spring Cache 规则缓存适配器，可由 Caffeine、Redis、JetCache 等具体实现支撑。
 */
public final class SpringCacheRuleCache implements RuleCache {

    private final Cache cache;

    public SpringCacheRuleCache(Cache cache) {
        this.cache = Objects.requireNonNull(cache, "cache 不能为空");
    }

    @Override
    public RuleDefinition get(String code) {
        return cache.get(code, RuleDefinition.class);
    }

    @Override
    public void put(String code, RuleDefinition rule) {
        if (Objects.nonNull(code) && Objects.nonNull(rule)) {
            cache.put(code, rule);
        }
    }

    @Override
    public void evict(String code) {
        if (Objects.nonNull(code)) {
            cache.evict(code);
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
