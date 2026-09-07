package io.ddd4j.boot.qlexpress.rule;

import java.time.LocalDateTime;

/**
 * 规则变更应用事件。
 */
public final class RuleChangedEvent {

    private final String ruleId;
    private final String ruleCode;
    private final Operation operation;
    private final LocalDateTime occurredAt;

    public RuleChangedEvent(String ruleId, String ruleCode, Operation operation, LocalDateTime occurredAt) {
        this.ruleId = ruleId;
        this.ruleCode = ruleCode;
        this.operation = operation;
        this.occurredAt = occurredAt;
    }

    public String ruleId() {
        return ruleId;
    }

    public String ruleCode() {
        return ruleCode;
    }

    public Operation operation() {
        return operation;
    }

    public LocalDateTime occurredAt() {
        return occurredAt;
    }

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
