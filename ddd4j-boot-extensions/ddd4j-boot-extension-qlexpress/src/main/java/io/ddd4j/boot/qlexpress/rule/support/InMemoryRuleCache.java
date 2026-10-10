package io.ddd4j.boot.qlexpress.rule.support;

import io.ddd4j.boot.qlexpress.rule.RuleCache;
import io.ddd4j.boot.qlexpress.rule.RuleDefinition;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进程内规则缓存。
 */
public final class InMemoryRuleCache implements RuleCache {

    /**
     * 无参构造器，初始化进程内并发缓存容器。
     */
    public InMemoryRuleCache() {
    }

    private final ConcurrentHashMap<String, RuleDefinition> cache = new ConcurrentHashMap<>();

    @Override
    public RuleDefinition get(String code) {
        return cache.get(code);
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
            cache.remove(code);
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
