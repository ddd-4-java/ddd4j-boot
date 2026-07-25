package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.cqrs.query.Query;

/**
 * 用户查询对象（充血查询，COLA 合规 —— 零基础设施依赖）。
 *
 * <p>继承 {@link Query}{@code <User>} —— P 绑聚合根类型（{@link User}），不再 import PO 类。
 * Lambda 字段引用 {@code User::getPhone} 由基础设施层（{@code MybatisAggregateRepository}）
 * 通过 MappingKit 注册的 MODEL_PO 映射翻译为 PO 列名。
 *
 * <p>业务方使用：
 * <pre>
 * new UserQuery().setPhone("13800138000").one("用户不存在");
 *
 * new UserQuery().setStatus(1).setCurrent(1).setSize(10).page();
 *
 * new UserQuery().setPhone("13800138000").notExist("该手机号已注册");
 * </pre>
 *
 * @author wandl
 */
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

    public UserQuery() {
        super();
    }

    public String getId() {
        return id;
    }

    public UserQuery setId(String id) {
        this.id = id;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserQuery setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public UserQuery setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public UserQuery setStatus(Integer status) {
        this.status = status;
        return this;
    }

}