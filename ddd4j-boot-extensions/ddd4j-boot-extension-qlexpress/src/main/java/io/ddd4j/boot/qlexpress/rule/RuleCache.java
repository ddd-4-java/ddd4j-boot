package io.ddd4j.boot.qlexpress.rule;

/**
 * 规则缓存 SPI。
 *
 * <p>用于缓存已编译/已解析的规则定义，避免每次执行都回源加载；实现可基于内存或分布式缓存。
 */
public interface RuleCache {

    /**
     * 按规则编码读取缓存中的规则定义。
     *
     * @param code 规则业务编码
     * @return 缓存命中时返回规则定义，未命中返回 {@code null}
     */
    RuleDefinition get(String code);

    /**
     * 写入或覆盖指定编码的规则缓存。
     *
     * @param code 规则业务编码
     * @param rule 规则定义
     */
    void put(String code, RuleDefinition rule);

    /**
     * 按规则编码失效单条缓存。
     *
     * @param code 规则业务编码
     */
    void evict(String code);

    /**
     * 清空全部规则缓存。
     */
    void clear();
}
