package io.ddd4j.boot.cmpt.express.domain.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 规则定义实体
 * 领域模型：规则的核心实体，包含规则的业务属性
 */
public class RuleDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private RuleId ruleId;          // 规则ID值对象
    private String ruleCode;        // 规则编码（唯一标识）
    private String ruleName;        // 规则名称
    private String ruleExpression;  // 规则表达式
    private String ruleDescription; // 规则描述
    private String ruleType;        // 规则类型（DECISION-决策规则，VALIDATION-校验规则等）
    private Boolean enabled;        // 是否启用
    private Integer priority;       // 优先级
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RuleDefinition() {
    }

    /**
     * 使用RuleId的构造函数
     */
    public RuleDefinition(RuleId ruleId, String ruleCode, String ruleName, String ruleExpression, 
                          String ruleDescription, String ruleType, Boolean enabled, Integer priority) {
        this.ruleId = ruleId;
        this.id = ruleId != null ? ruleId.toLong() : null;
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.ruleExpression = ruleExpression;
        this.ruleDescription = ruleDescription;
        this.ruleType = ruleType;
        this.enabled = enabled;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 便捷构造函数（兼容旧代码，使用Long id）
     */
    public RuleDefinition(Long id, String ruleCode, String ruleName, String ruleExpression, 
                          String ruleDescription, String ruleType, Boolean enabled, Integer priority) {
        this(id != null ? RuleId.valueOf(id) : null, ruleCode, ruleName, ruleExpression, 
             ruleDescription, ruleType, enabled, priority);
    }

    /**
     * 启用规则
     */
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 禁用规则
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新规则表达式
     */
    public void updateExpression(String expression) {
        this.ruleExpression = expression;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查规则是否可用
     */
    public boolean isAvailable() {
        return enabled != null && enabled;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        if (id != null) {
            this.ruleId = RuleId.valueOf(id);
        }
    }

    public RuleId getRuleId() {
        return ruleId;
    }

    public void setRuleId(RuleId ruleId) {
        this.ruleId = ruleId;
        if (ruleId != null) {
            this.id = ruleId.toLong();
        }
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
