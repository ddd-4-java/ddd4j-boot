package io.ddd4j.boot.sample.layered.domain.model;

import io.ddd4j.core.ddd.repository.Repository;

/**
 * 用户仓储接口（领域层定义，零基础设施依赖）。
 *
 * <p>仅声明聚合根类型（{@link User}）与标识类型（{@link String}），
 * <b>不暴露 PO 概念</b>。基础设施层的 {@code UserRepositoryImpl}
 * 继承 {@code BaseRepositoryImpl} 获得 MyBatis-Plus 适配能力。
 *
 * @author wandl
 */
public interface UserRepository extends Repository<User, String> {
}
