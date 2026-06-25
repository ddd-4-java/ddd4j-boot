package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.contract.BaseRepository;

/**
 * 用户仓储接口（领域层定义，基础设施层实现）。
 *
 * <p>空接口——仅声明泛型，所有 CRUD 方法由 {@link BaseRepository} 提供。
 * 基础设施层的 {@code UserRepositoryImpl} 继承 {@code BaseRepositoryImpl} 即获得 27 个方法的默认实现。
 *
 * @author wandl
 */
public interface UserRepository extends BaseRepository<User, UserQuery> {
}
