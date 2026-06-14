package io.ddd4j.boot.core.contract;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 纯净领域模型基类（不继承任何框架类）。
 *
 * <p>这是 ddd4j-boot 的<b>纯净 DDD 轨道</b>领域模型基类。与
 * {@code io.ddd4j.boot.core.entity.BaseEntity}（MyBatis Plus ActiveRecord 轨道）的区别：
 *
 * <table border="1">
 *   <tr><th></th><th>DomainModel（本类）</th><th>BaseEntity（MP 轨道）</th></tr>
 *   <tr><td>继承</td><td>仅 implements Serializable</td><td>extends MP Model&lt;T&gt;</td></tr>
 *   <tr><td>框架依赖</td><td>无</td><td>MyBatis Plus</td></tr>
 *   <tr><td>持久化方法</td><td>无（由 Repository 负责）</td><td>insertById/updateById（AR）</td></tr>
 *   <tr><td>ORM 注解</td><td>无</td><td>@TableField/@TableLogic</td></tr>
 *   <tr><td>适用场景</td><td>COLA/Clean/Hexagonal 严格 DDD</td><td>快速 CRUD 脚手架</td></tr>
 * </table>
 *
 * <p>使用方式：
 * <pre>
 * public class User extends DomainModel {
 *     private UserId id;       // 值对象
 *     private String name;
 *     // 纯领域行为，不含持久化逻辑
 *     public void rename(String newName) { this.name = newName; }
 * }
 * </pre>
 *
 * <p>事件溯源（ES）场景请使用 {@code ddd4j-boot-ddd} 模块的
 * {@code io.ddd4j.boot.ddd.aggregate.DddAggregateRoot}（基于 fuinorg）。
 *
 * @author wandl
 * @since 3.4.x
 */
public abstract class DomainModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建时间（审计字段，无 ORM 注解，由基础设施层填充） */
    protected LocalDateTime createTime;

    /** 更新时间（审计字段，无 ORM 注解，由基础设施层填充） */
    protected LocalDateTime updateTime;

    /** 创建人（审计字段） */
    protected String createBy;

    /** 更新人（审计字段） */
    protected String updateBy;

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

}
