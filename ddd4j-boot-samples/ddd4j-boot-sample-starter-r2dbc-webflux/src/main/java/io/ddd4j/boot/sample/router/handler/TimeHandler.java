/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.router.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * 时间响应式函数式路由处理器：以纯文本返回当前时刻与日期。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Component
public class TimeHandler {

    /**
     * 构造时间处理器。
     */
    public TimeHandler() {
    }

    /**
     * 查询当前时间（HH:mm:ss）。
     *
     * @param serverRequest 当前服务端请求
     * @return 200 文本响应，正文为 Now is HH:mm:ss
     */
    public Mono<ServerResponse> getTime(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Now is " + new SimpleDateFormat("HH:mm:ss").format(new Date())), String.class);
    }

    /**
     * 查询当前日期（yyyy-MM-dd）。
     *
     * @param serverRequest 当前服务端请求
     * @return 200 文本响应，正文为 Today is yyyy-MM-dd
     */
    public Mono<ServerResponse> getDate(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Today is " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())), String.class);
    }
}
