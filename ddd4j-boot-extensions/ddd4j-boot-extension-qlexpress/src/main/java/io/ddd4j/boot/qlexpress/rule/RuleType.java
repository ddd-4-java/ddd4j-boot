package io.ddd4j.boot.qlexpress.rule;

/**
 * 规则用途分类。
 */
public enum RuleType {
    /** 决策类规则：输出业务决策结论。 */
    DECISION,
    /** 校验类规则：校验数据或请求是否满足约束。 */
    VALIDATION,
    /** 计算类规则：基于表达式计算输出值。 */
    CALCULATION
}
