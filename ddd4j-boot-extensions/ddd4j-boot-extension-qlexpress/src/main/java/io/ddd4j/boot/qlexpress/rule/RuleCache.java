package io.ddd4j.boot.qlexpress.rule;

/**
 * 规则缓存 SPI。
 */
public interface RuleCache {

    RuleDefinition get(String code);

    void put(String code, RuleDefinition rule);

    void evict(String code);

    void clear();
}
