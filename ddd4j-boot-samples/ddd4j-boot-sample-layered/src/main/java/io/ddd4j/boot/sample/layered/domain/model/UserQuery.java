package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.cqrs.query.Query;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询对象（充血查询，COLA 合规 —— 零基础设施依赖）。
 *
 * <p>继承 {@link Query}{@code <User>} —— P 绑聚合根类型（{@link User}），不再 import PO 类。
 * Lambda 字段引用 {@code User::getPhone} 由基础设施层（{@code MybatisAggregateRepository}）
 * 通过 MappingKit 注册的 MODEL_PO 映射翻译为 PO 列名。
 *
 * <p>业务方使用：
 * <pre>
 * UserQuery.builder()
 *     .phone("13800138000")
 *     .build()
 *     .one();  // 查单个
 *
 * UserQuery.builder()
 *     .status(1)
 *     .current(1).size(10)
 *     .build()
 *     .page(); // 分页查询
 *
 * UserQuery.builder()
 *     .phone("13800138000")
 *     .build()
 *     .notExist("该手机号已注册"); // 查不到才通过，查到则抛异常
 * </pre>
 *
 * @author wandl
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQuery extends Query<User> {

    /**
     * 用户ID
     */
    private String id;

    /**
     * 手机号（精确匹配）
     */
    private String phone;

    /**
     * 昵称（模糊匹配）
     */
    private String nickname;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

}
