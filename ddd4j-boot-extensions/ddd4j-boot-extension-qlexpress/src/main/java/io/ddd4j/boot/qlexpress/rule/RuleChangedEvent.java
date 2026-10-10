package io.ddd4j.boot.qlexpress.rule;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

import java.time.LocalDateTime;

/**
 * 规则变更应用事件。
 *
 * <p>以不可变 record 承载一次规则变更的关键要素，供缓存失效、审计与下游通知使用。
 *
 * 组件 ruleId：规则唯一标识
 * 组件 ruleCode：规则业务编码
 * 组件 operation：变更动作类型
 * 组件 occurredAt：变更发生时间
 */
public final class RuleChangedEvent {

    private static final long serialVersionUID = 0L;

    private final String ruleId;

    private final String ruleCode;

    private final Operation operation;

    private final LocalDateTime occurredAt;

    /**
     * 规则变更动作类型。
     */
    public enum Operation {

        /**
         * 规则新建。
         */
        CREATED,
        /**
         * 规则内容更新。
         */
        UPDATED,
        /**
         * 规则删除。
         */
        DELETED,
        /**
         * 规则启用。
         */
        ENABLED,
        /**
         * 规则停用。
         */
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

    @JsonCreator()
    public RuleChangedEvent(@JsonProperty("ruleId") String ruleId, @JsonProperty("ruleCode") String ruleCode, @JsonProperty("operation") Operation operation, @JsonProperty("occurredAt") LocalDateTime occurredAt) {
        this.ruleId = ruleId;
        this.ruleCode = ruleCode;
        this.operation = operation;
        this.occurredAt = occurredAt;
    }

    @JsonProperty("ruleId")
    public String ruleId() {
        return ruleId;
    }

    @JsonProperty("ruleCode")
    public String ruleCode() {
        return ruleCode;
    }

    @JsonProperty("operation")
    public Operation operation() {
        return operation;
    }

    @JsonProperty("occurredAt")
    public LocalDateTime occurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (Objects.isNull(obj) || getClass() != obj.getClass()) {
            return false;
        }
        RuleChangedEvent other = (RuleChangedEvent) obj;
        return Objects.equals(this.ruleId, other.ruleId) && Objects.equals(this.ruleCode, other.ruleCode) && Objects.equals(this.operation, other.operation) && Objects.equals(this.occurredAt, other.occurredAt);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + Objects.hashCode(ruleId);
        result = 31 * result + Objects.hashCode(ruleCode);
        result = 31 * result + Objects.hashCode(operation);
        result = 31 * result + Objects.hashCode(occurredAt);
        return result;
    }

    @Override
    public String toString() {
        return "RuleChangedEvent[ruleId=" + ruleId + ", ruleCode=" + ruleCode + ", operation=" + operation + ", occurredAt=" + occurredAt + "]";
    }
}
