package io.ddd4j.boot.cmpt.express.application.service;

import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleExecutionResult;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleValidationResult;
import io.ddd4j.boot.cmpt.express.domain.repository.RuleDefinitionRepository;
import io.ddd4j.boot.cmpt.express.domain.service.RuleEngineDomainService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 规则引擎应用服务
 * 负责用例编排、事务管理、缓存协调等
 * 
 * 注意：此类通过ExpressAutoConfiguration自动配置，无需手动添加@Service注解
 */
public class RuleEngineApplicationService {

    private final RuleEngineDomainService ruleEngineDomainService;
    private final RuleDefinitionRepository ruleRepository;
    private final RuleCacheService ruleCacheService;

    public RuleEngineApplicationService(RuleEngineDomainService ruleEngineDomainService,
                                       RuleDefinitionRepository ruleRepository,
                                       RuleCacheService ruleCacheService) {
        this.ruleEngineDomainService = ruleEngineDomainService;
        this.ruleRepository = ruleRepository;
        this.ruleCacheService = ruleCacheService;
    }

    /**
     * 执行规则
     */
    public RuleExecutionResult executeRule(String ruleCode, Map<String, Object> context) {
        // 1. 获取规则定义（带缓存）
        RuleDefinition rule = getRuleDefinition(ruleCode);
        
        if (rule == null) {
            return RuleExecutionResult.builder()
                    .success(false)
                    .errorCode("RULE_NOT_FOUND")
                    .errorMessage("规则不存在: " + ruleCode)
                    .build();
        }

        // 2. 检查规则是否可用
        if (!ruleEngineDomainService.isRuleAvailable(rule)) {
            return RuleExecutionResult.builder()
                    .success(false)
                    .errorCode("RULE_DISABLED")
                    .errorMessage("规则已禁用: " + ruleCode)
                    .build();
        }

        // 3. 执行规则
        long startTime = System.currentTimeMillis();
        RuleExecutionResult result = ruleEngineDomainService.executeRule(rule, context);
        long executionTime = System.currentTimeMillis() - startTime;

        // 4. 设置执行时间
        return RuleExecutionResult.builder()
                .success(result.isSuccess())
                .errorCode(result.getErrorCode())
                .errorMessage(result.getErrorMessage())
                .result(result.getResult())
                .ruleCode(ruleCode)
                .executedAt(result.getExecutedAt())
                .executionTime(executionTime)
                .build();
    }

    /**
     * 批量执行规则
     */
    public List<RuleExecutionResult> executeRules(List<String> ruleCodes, Map<String, Object> context) {
        return ruleCodes.stream()
                .map(ruleCode -> executeRule(ruleCode, context))
                .collect(Collectors.toList());
    }

    /**
     * 验证规则语法
     */
    public RuleValidationResult validateRule(String expression) {
        return ruleEngineDomainService.validateExpression(expression);
    }

    /**
     * 获取规则定义（带缓存）
     */
    private RuleDefinition getRuleDefinition(String ruleCode) {
        // 先从缓存获取
        RuleDefinition cachedRule = ruleCacheService.get(ruleCode);
        if (cachedRule != null) {
            return cachedRule;
        }

        // 缓存未命中，从数据库获取
        return ruleRepository.findByRuleCode(ruleCode)
                .map(rule -> {
                    // 存入缓存
                    ruleCacheService.put(ruleCode, rule);
                    return rule;
                })
                .orElse(null);
    }

    /**
     * 清除规则缓存
     */
    public void clearRuleCache(String ruleCode) {
        ruleCacheService.evict(ruleCode);
    }

    /**
     * 清除所有规则缓存
     */
    public void clearAllRuleCache() {
        ruleCacheService.evictAll();
    }
}

