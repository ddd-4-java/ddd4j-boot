/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.repository;

import io.ddd4j.boot.sample.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * 用户响应式仓储接口：继承 {@link ReactiveCrudRepository}，扩展按用户名查询与删除能力。
 *
 * @author ddd4j
 * @since 1.0.0
 */
public interface UserRepository extends ReactiveCrudRepository<User, String> {  // 1

    /**
     * 按用户名查询用户。
     *
     * @param username 用户名
     * @return 用户信息的响应式流，无匹配时发射空值
     */
    Mono<User> findByUsername(String username);     // 2

    /**
     * 按用户名删除用户。
     *
     * @param username 用户名
     * @return 删除记录数的响应式流
     */
    Mono<Long> deleteByUsername(String username);

}
