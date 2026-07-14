package io.ddd4j.boot.qlexpress.rule;

import java.time.LocalDateTime;

/**
 * 规则变更应用事件。
 */
public record RuleChangedEvent(String ruleId, String ruleCode, Operation operation,
                               LocalDateTime occurredAt) {

    public enum Operation {
        CREATED,
        UPDATED,
        DELETED,
        ENABLED,
        DISABLED
    }

    public static RuleChangedEvent of(RuleDefinition rule, Operation operation) {
        return new RuleChangedEvent(rule.getId(), rule.getCode(), operation, LocalDateTime.now());
    }
}
