package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.contract.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询对象（充血查询）。
 *
 * <p>继承 {@link Query} 即获得全套查询能力：
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
 * <p>查询条件由业务层用 MyBatis Plus Wrapper 显式构造，不再使用字段后缀约定。
 *
 * @author wandl
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQuery extends Query {

    /**
     * 用户ID
     */
    private String id;

    /**
     * 手机号（精确匹配）
     */
    private String phone;

    /**
     * 昵称（模糊匹配，业务层用 wrapper.like 构造）
     */
    private String nickname;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 链式构造器（简化 Builder 模式）。
     */
    public static UserQuery builder() {
        return new UserQuery();
    }

    public UserQuery id(String id) {
        this.id = id;
        return this;
    }

    public UserQuery phone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserQuery nickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public UserQuery status(Integer status) {
        this.status = status;
        return this;
    }

}
