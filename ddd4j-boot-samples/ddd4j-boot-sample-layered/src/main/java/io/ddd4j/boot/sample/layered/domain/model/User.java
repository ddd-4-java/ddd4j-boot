package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.ddd.model.AggregateRoot;
import io.ddd4j.spring.annotation.DomainEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

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
@Data
@EqualsAndHashCode(callSuper = true)
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
     */
    public User disable() {
        this.status = 0;
        return this;
    }

    /**
     * 启用用户（领域行为）。
     */
    public User enable() {
        this.status = 1;
        return this;
    }

}
