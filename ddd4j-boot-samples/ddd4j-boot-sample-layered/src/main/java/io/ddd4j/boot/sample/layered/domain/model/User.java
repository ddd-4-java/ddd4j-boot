package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.ddd.model.AggregateRoot;
import io.ddd4j.spring.annotation.DomainEntity;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 用户聚合根（充血模型）。
 *
 * <p>继承 {@link AggregateRoot} 即获得框架无关的充血持久化能力：
 * <pre>
 * user.save();        // 新增
 * user.update();      // 更新
 * User.delete(query); // 条件删除
 * </pre>
 *
 * <p>领域行为直接写在聚合根上（充血），而非 Service 层（贫血）。
 *
 * @author wandl
 */
@DomainEntity(aggregateRoot = true)
public class User extends AggregateRoot<String> {

    /**
     * 用户ID
     */
    private String id;

    /**
     * 手机号（业务键）
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 注册新用户（领域行为）。
     *
     * @param phone    手机号
     * @param nickname 昵称
     */
    public User(String phone, String nickname) {
        this.phone = phone;
        this.nickname = nickname;
        this.status = 1;
    }

    @Override
    public String id() {
        return id;
    }

    /**
     * 修改昵称（领域行为）。
     *
     * @param nickname 新昵称
     * @return 更新后的当前用户对象
     */
    public User rename(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        this.nickname = nickname;
        return this;
    }

    /**
     * 禁用用户（领域行为）。
     *
     * @return 禁用后的当前用户对象
     */
    public User disable() {
        this.status = 0;
        return this;
    }

    /**
     * 启用用户（领域行为）。
     *
     * @return 启用后的当前用户对象
     */
    public User enable() {
        this.status = 1;
        return this;
    }

    /** 获取用户ID。
     * @return 用户ID */
    public String getId() {
        return id;
    }

    /** 设置用户ID。
     * @param id 用户ID */
    public void setId(String id) {
        this.id = id;
    }

    /** 获取手机号。
     * @return 手机号 */
    public String getPhone() {
        return phone;
    }

    /** 设置手机号。
     * @param phone 手机号 */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** 获取昵称。
     * @return 昵称 */
    public String getNickname() {
        return nickname;
    }

    /** 设置昵称。
     * @param nickname 昵称 */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /** 获取状态。
     * @return 状态 */
    public Integer getStatus() {
        return status;
    }

    /** 设置状态。
     * @param status 状态 */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /** 判断当前对象与指定对象是否相等。
     * @param o 待比较对象
     * @return 相等返回 {@code true}，否则返回 {@code false} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /** 返回基于各字段计算的哈希码。
     * @return 哈希码 */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}