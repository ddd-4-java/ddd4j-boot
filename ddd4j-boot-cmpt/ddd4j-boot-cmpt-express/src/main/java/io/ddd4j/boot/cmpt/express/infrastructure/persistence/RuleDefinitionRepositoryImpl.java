package io.ddd4j.boot.cmpt.express.infrastructure.persistence;

import io.ddd4j.boot.cmpt.express.domain.repository.RuleDefinitionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Repository;

/**
 * 规则定义仓储实现
 * 基础设施层：持久化实现
 * 
 * 注意：这是一个接口示例，实际使用时需要根据项目的持久化方案来实现：
 * - 如果使用JPA：实现JpaRepository接口
 * - 如果使用MyBatis：实现Mapper接口
 * - 如果使用其他方案：直接实现RuleDefinitionRepository接口
 */
@Repository
@ConditionalOnClass(name = "org.springframework.data.repository.Repository")
public interface RuleDefinitionRepositoryImpl extends RuleDefinitionRepository {

    // 默认实现为空，需要根据实际持久化方案来实现
    // 如果使用JPA，可以继承JpaRepository<RuleDefinition, Long>
    // 如果使用MyBatis，可以实现BaseMapper<RuleDefinition>
}

