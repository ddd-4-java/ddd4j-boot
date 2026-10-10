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

    /** 构造 UserQuery 对象。 */
    public UserQuery() {
        super();
    }

    /** 获取用户ID。
     * @return 用户ID */
    public String getId() {
        return id;
    }

    /**
     * 设置用户ID（链式调用）。
     *
     * @param id 用户ID
     * @return 当前查询对象本身，支持链式调用
     */
    public UserQuery setId(String id) {
        this.id = id;
        return this;
    }

    /** 获取手机号。
     * @return 手机号 */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号查询条件（链式调用）。
     *
     * @param phone 手机号
     * @return 当前查询对象本身，支持链式调用
     */
    public UserQuery setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    /** 获取昵称。
     * @return 昵称 */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置昵称查询条件（链式调用）。
     *
     * @param nickname 昵称
     * @return 当前查询对象本身，支持链式调用
     */
    public UserQuery setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    /** 获取状态。
     * @return 状态 */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态查询条件（链式调用）。
     *
     * @param status 状态：0-禁用，1-启用
     * @return 当前查询对象本身，支持链式调用
     */
    public UserQuery setStatus(Integer status) {
        this.status = status;
        return this;
    }

}