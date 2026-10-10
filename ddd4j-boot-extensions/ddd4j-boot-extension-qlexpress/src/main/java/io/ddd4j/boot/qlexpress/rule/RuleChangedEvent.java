package io.ddd4j.boot.qlexpress.rule;

import java.time.LocalDateTime;

/**
 * 规则变更应用事件。
 *
 * <p>以不可变 record 承载一次规则变更的关键要素，供缓存失效、审计与下游通知使用。
 *
 * @param ruleId    规则唯一标识
 * @param ruleCode  规则业务编码
 * @param operation 变更动作类型
 * @param occurredAt 变更发生时间
 */
public record RuleChangedEvent(String ruleId, String ruleCode, Operation operation,
                               LocalDateTime occurredAt) {

    /**
     * 规则变更动作类型。
     */
    public enum Operation {
        /** 规则新建。 */
        CREATED,
        /** 规则内容更新。 */
        UPDATED,
        /** 规则删除。 */
        DELETED,
        /** 规则启用。 */
        ENABLED,
        /** 规则停用。 */
        DISABLED
    }

    /**
     * 基于规则定义与变更动作快速构造事件，发生时间取当前系统时间。
     *
     * @param rule      规则定义，取其 id 与 code 填充事件
     * @param operation 变更动作类型
     * @return 新建的规则变更事件
     */
    public static RuleChangedEvent of(RuleDefinition rule, Operation operation) {
        return new RuleChangedEvent(rule.getId(), rule.getCode(), operation, LocalDateTime.now());
    }
}
