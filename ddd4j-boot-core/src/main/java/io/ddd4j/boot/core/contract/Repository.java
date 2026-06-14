package io.ddd4j.boot.core.contract;

import java.util.Optional;

/**
 * 框架无关的仓储接口（纯净 DDD 轨道）。
 *
 * <p>这是 DDD 仓储模式的<b>端口</b>定义，领域层定义接口，基础设施层提供实现。
 * 接口本身不依赖任何 ORM 框架（MyBatis/JPA），实现可以是：
 * <ul>
 *   <li>{@code ddd4j-boot-data} 的 MyBatis Plus 实现</li>
 *   <li>JPA 实现</li>
 *   <li>R2DBC 反应式实现</li>
 *   <li>事件溯源实现（{@code ddd4j-boot-ddd} 模块）</li>
 * </ul>
 *
 * <p>与 {@code io.ddd4j.boot.core.service.IBaseService}（继承 MyBatis Plus IService）的区别：
 * <ul>
 *   <li>本接口<b>不依赖</b> MyBatis Plus</li>
 *   <li>返回领域对象而非 PO/Entity</li>
 *   <li>方法语义面向领域（findById/save），而非数据库操作（selectById/insert）</li>
 * </ul>
 *
 * @param <T> 领域模型类型
 * @param <ID> 标识类型
 * @author wandl
 * @since 3.4.x
 */
public interface Repository<T extends DomainModel, ID> {

    /**
     * 保存领域模型（新增或更新）。
     *
     * @param entity 领域模型
     * @return 保存后的领域模型（含生成的 ID 等回填字段）
     */
    T save(T entity);

    /**
     * 按标识查找领域模型。
     *
     * @param id 标识
     * @return 领域模型（不存在返回 empty）
     */
    Optional<T> findById(ID id);

    /**
     * 按标识删除领域模型。
     *
     * @param id 标识
     */
    void deleteById(ID id);

    /**
     * 按标识判断是否存在。
     *
     * @param id 标识
     * @return 存在返回 true
     */
    boolean existsById(ID id);

}
