package io.ddd4j.boot.sample.layered.infrastructure.persistence;

import io.ddd4j.boot.sample.layered.domain.model.User;
import io.ddd4j.boot.sample.layered.domain.model.UserQuery;
import io.ddd4j.boot.sample.layered.domain.model.UserRepository;
import io.ddd4j.data.mybatis.repository.impl.BaseRepositoryImpl;
import io.ddd4j.spring.annotation.DomainRepository;

import java.util.List;

/**
 * 用户仓储实现（基础设施层）。
 *
 * <p>继承 {@link BaseRepositoryImpl} 四泛型，即获得 27 个方法的默认实现。
 * 本类零样板代码——只需要指定四个泛型参数：
 * <ul>
 *   <li>{@code UserMapper} — MyBatis Mapper（MP）</li>
 *   <li>{@code User} — 领域模型（M）</li>
 *   <li>{@code UserPO} — 持久化对象（P）</li>
 *   <li>{@code UserQuery} — 查询对象（Q）</li>
 * </ul>
 *
 * <p>构造器自动注册 Model↔PO↔Query 映射，让 {@code user.save()} /
 * {@code query.page()} 等充血方法能自动反查到本仓储。
 *
 * @author wandl
 */
@DomainRepository
public class UserRepositoryImpl extends BaseRepositoryImpl<UserMapper, User, UserPO, UserQuery> implements UserRepository {

    /**
     * 聚合填充示例：查用户列表后自动填充关联数据。
     *
     * <p>实际业务可在此处批量查询关联数据（如角色、权限），
     * 避免 N+1 查询。此处仅做占位。
     */
    @Override
    public void fill(UserQuery query, List<User> models) {
        // TODO: 按 query.getFills() 的值决定填充哪些关联数据
    }

}
