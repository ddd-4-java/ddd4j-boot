package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.ddd.repository.Repository;

import java.io.Serializable;

/**
 * 用户仓储接口（领域层定义，基础设施层实现）。
 *
 * <p>空接口——仅声明聚合根与标识类型，基础设施层的 {@code UserRepositoryImpl}
 * 继承 {@code BaseRepositoryImpl} 获得 MyBatis-Plus 适配能力。
 *
 * @author wandl
 */
public interface UserRepository extends Repository<User, Serializable> {
}
