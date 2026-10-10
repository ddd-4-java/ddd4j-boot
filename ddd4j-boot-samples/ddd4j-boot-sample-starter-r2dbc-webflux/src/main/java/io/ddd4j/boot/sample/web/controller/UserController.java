/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.boot.sample.entity.User;
import io.ddd4j.boot.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 用户响应式 REST 控制器：基于 WebFlux 提供用户新增、删除、单查与流式查询接口。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 构造用户控制器。
     */
    public UserController() {
    }

    /**
     * 用户响应式服务，注入后委托执行保存、删除与查询操作。
     */
    @Autowired
    private UserService userService;

    /**
     * 保存用户，若用户名重复则复用已存在记录的主键后更新。
     *
     * @param user 待保存的用户信息
     * @return 保存结果的响应式流，发射保存后的用户对象
     */
    @PostMapping("")
    public Mono<User> save(User user) {
        return this.userService.save(user);
    }

    /**
     * 按用户名删除用户。
     *
     * @param username 用户名路径变量
     * @return 删除记录数的响应式流，发射受影响行数
     */
    @DeleteMapping("/{username}")
    public Mono<Long> deleteByUsername(@PathVariable String username) {
        return this.userService.deleteByUsername(username);
    }

    /**
     * 按用户名查询单个用户。
     *
     * @param username 用户名路径变量
     * @return 用户信息的响应式流，无匹配时发射空值
     */
    @GetMapping("/{username}")
    public Mono<User> findByUsername(@PathVariable String username) {
        return this.userService.findByUsername(username);
    }

    /**
     * 流式查询全部用户，以 NDJSON 格式逐条输出，并按秒级节奏延迟发射。
     *
     * @return 用户列表的响应式流，每条记录间延迟 1 秒
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<User> findAll() {
        return this.userService.findAll().delayElements(Duration.ofSeconds(1));
    }
}
