package io.ddd4j.boot.cmpt.express.domain.event;

import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;
import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 规则更新事件
 * 领域事件：当规则被更新时发布此事件
 */
public class RuleUpdatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final RuleId ruleId;
    private final String ruleCode;
    private final String ruleName;
    private final String ruleType;
    private final boolean expressionChanged;
    private final boolean enabledChanged;
    private final LocalDateTime occurredAt;

    /**
     * 构造函数
     */
    public RuleUpdatedEvent(RuleDefinition rule, boolean expressionChanged, boolean enabledChanged) {
        this.ruleId = rule.getRuleId();
        this.ruleCode = rule.getRuleCode();
        this.ruleName = rule.getRuleName();
        this.ruleType = rule.getRuleType();
        this.expressionChanged = expressionChanged;
        this.enabledChanged = enabledChanged;
        this.occurredAt = LocalDateTime.now();
    }

    /**
     * 构造函数
     */
    public RuleUpdatedEvent(RuleId ruleId, String ruleCode, String ruleName, String ruleType,
                            boolean expressionChanged, boolean enabledChanged) {
        this.ruleId = ruleId;
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.expressionChanged = expressionChanged;
        this.enabledChanged = enabledChanged;
        this.occurredAt = LocalDateTime.now();
    }

    public RuleId getRuleId() {
        return ruleId;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public boolean isExpressionChanged() {
        return expressionChanged;
    }

    public boolean isEnabledChanged() {
        return enabledChanged;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return "RuleUpdatedEvent{" +
                "ruleId=" + ruleId +
                ", ruleCode='" + ruleCode + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", ruleType='" + ruleType + '\'' +
                ", expressionChanged=" + expressionChanged +
                ", enabledChanged=" + enabledChanged +
                ", occurredAt=" + occurredAt +
                '}';
    }
}

