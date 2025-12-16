package io.ddd4j.boot.cmpt.express.domain.repository;

import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;

import java.util.List;
import java.util.Optional;

/**
 * 规则定义仓储接口
 * 领域层接口，定义规则持久化的抽象
 */
public interface RuleDefinitionRepository {

    /**
     * 根据规则编码查找规则
     */
    Optional<RuleDefinition> findByRuleCode(String ruleCode);

    /**
     * 查找所有启用的规则，按优先级降序排列
     */
    List<RuleDefinition> findEnabledRulesOrderByPriorityDesc();

    /**
     * 根据规则类型查找启用的规则
     */
    List<RuleDefinition> findByRuleTypeAndEnabled(String ruleType);

    /**
     * 保存规则
     */
    RuleDefinition save(RuleDefinition rule);

    /**
     * 根据ID查找规则
     */
    Optional<RuleDefinition> findById(Long id);

    /**
     * 删除规则
     */
    void deleteById(Long id);

    /**
     * 查找所有规则
     */
    List<RuleDefinition> findAll();
}

