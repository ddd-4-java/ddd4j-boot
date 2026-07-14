package io.ddd4j.boot.qlexpress.rule;

import java.util.List;
import java.util.Optional;

/**
 * 规则持久化 SPI。业务系统可以用 JPA、MyBatis 或远程配置中心替换默认实现。
 */
public interface RuleRepository {

    RuleDefinition save(RuleDefinition rule);

    Optional<RuleDefinition> findById(String id);

    Optional<RuleDefinition> findByCode(String code);

    List<RuleDefinition> findAll();

    void deleteById(String id);
}
