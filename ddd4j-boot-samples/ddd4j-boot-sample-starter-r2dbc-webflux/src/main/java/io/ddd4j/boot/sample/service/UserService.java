/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.service;

import io.ddd4j.boot.sample.entity.User;
import io.ddd4j.boot.sample.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 用户响应式服务：封装 {@link UserRepository} 的保存、删除与查询能力，全链路返回 Reactor 类型。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Service
public class UserService {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public UserService() {
    }

    /**
     * 用户仓储，提供 R2DBC 响应式数据访问。
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 保存或更新。 如果传入的user没有id属性，由于username是unique的，在重复的情况下有可能报错，
     * 这时找到以保存的user记录用传入的user更新它。
     *
     * @param user 待保存的用户
     * @return 保存结果流；用户名冲突时自动回退为更新既有记录
     */
    public Mono<User> save(User user) {
        return userRepository.save(user).onErrorResume(e -> // 1
                userRepository.findByUsername(user.getUsername()) // 2
                        .flatMap(originalUser -> { // 4
                            user.setId(originalUser.getId());
                            return userRepository.save(user); // 3
                        }));
    }

    /**
     * 按用户名删除用户。
     *
     * @param username 用户名
     * @return 受影响行数流
     */
    public Mono<Long> deleteByUsername(String username) {
        return userRepository.deleteByUsername(username);
    }

    /**
     * 按用户名查询用户。
     *
     * @param username 用户名
     * @return 用户对象流；无匹配时为空流
     */
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 查询全部用户。
     *
     * @return 用户对象流
     */
    public Flux<User> findAll() {
        return userRepository.findAll();
    }
}
