package io.ddd4j.boot.qlexpress.rule;

import java.util.List;
import java.util.Optional;

/**
 * 规则持久化 SPI。业务系统可以用 JPA、MyBatis 或远程配置中心替换默认实现。
 *
 * <p>约定实现需保证 {@code code} 唯一，并支持按 id 与 code 两种定位方式。
 */
public interface RuleRepository {

    /**
     * 新增或全量更新规则定义。
     *
     * @param rule 规则定义
     * @return 持久化后的规则定义（含生成的主键与时间戳）
     */
    RuleDefinition save(RuleDefinition rule);

    /**
     * 按唯一标识查询规则。
     *
     * @param id 规则唯一标识
     * @return 命中的规则定义，不存在时为空
     */
    Optional<RuleDefinition> findById(String id);

    /**
     * 按业务编码查询规则。
     *
     * @param code 规则业务编码
     * @return 命中的规则定义，不存在时为空
     */
    Optional<RuleDefinition> findByCode(String code);

    /**
     * 查询全部规则定义。
     *
     * @return 规则定义列表，无数据时返回空列表
     */
    List<RuleDefinition> findAll();

    /**
     * 按唯一标识删除规则。
     *
     * @param id 规则唯一标识
     */
    void deleteById(String id);
}
