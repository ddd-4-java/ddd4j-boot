/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 简单问候响应式控制器：提供 /hello 接口，返回欢迎语。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@RestController
public class HelloController {

    /**
     * 构造问候控制器。
     */
    public HelloController() {
    }

    /**
     * 返回欢迎语。
     *
     * @return 欢迎文案的响应式流
     */
    @GetMapping("/hello")
    public Mono<String> hello() {   // 【改】返回类型为Mono<String>
        return Mono.just("Welcome to reactive world ~");     // 【改】使用Mono.just生成响应式数据
    }

}
