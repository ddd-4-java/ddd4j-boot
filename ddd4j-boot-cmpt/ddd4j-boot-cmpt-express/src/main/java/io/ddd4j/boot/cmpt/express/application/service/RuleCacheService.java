package io.ddd4j.boot.cmpt.express.application.service;

import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;

/**
 * 规则缓存服务接口
 * 应用层服务，负责规则缓存管理
 */
public interface RuleCacheService {

    /**
     * 获取规则
     */
    RuleDefinition get(String ruleCode);

    /**
     * 缓存规则
     */
    void put(String ruleCode, RuleDefinition rule);

    /**
     * 清除指定规则缓存
     */
    void evict(String ruleCode);

    /**
     * 清除所有规则缓存
     */
    void evictAll();
}

