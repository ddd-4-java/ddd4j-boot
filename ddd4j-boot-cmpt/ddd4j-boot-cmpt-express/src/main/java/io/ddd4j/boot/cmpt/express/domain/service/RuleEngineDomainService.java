package io.ddd4j.boot.cmpt.express.domain.service;

import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleExecutionResult;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleValidationResult;

import java.util.Map;

/**
 * 规则引擎领域服务
 * 包含规则执行的核心业务逻辑，不依赖具体的技术实现
 */
public interface RuleEngineDomainService {

    /**
     * 执行规则表达式
     * 
     * @param rule 规则定义
     * @param context 执行上下文
     * @return 执行结果
     */
    RuleExecutionResult executeRule(RuleDefinition rule, Map<String, Object> context);

    /**
     * 验证规则表达式语法
     * 
     * @param expression 规则表达式
     * @return 验证结果
     */
    RuleValidationResult validateExpression(String expression);

    /**
     * 检查规则是否可用
     * 
     * @param rule 规则定义
     * @return 是否可用
     */
    boolean isRuleAvailable(RuleDefinition rule);
}

