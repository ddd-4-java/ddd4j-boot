/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.router;

import io.ddd4j.boot.sample.router.handler.TimeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * 响应式函数式路由配置：将 /time 与 /date 请求映射到 {@link TimeHandler}。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Configuration
public class RouterConfig {

    /**
     * 时间处理器，路由命中后委托其生成响应。
     */
    @Autowired
    private TimeHandler timeHandler;

    /**
     * 构造路由配置对象。
     */
    public RouterConfig() {
    }

    /**
     * 注册时间路由：GET /time 返回当前时刻，GET /date 返回当前日期。
     *
     * @return 时间与日期的路由函数
     */
    @Bean
    public RouterFunction<ServerResponse> timerRouter() {
        return route(GET("/time"), req -> timeHandler.getTime(req))
                .andRoute(GET("/date"), timeHandler::getDate);  // 这种方式相对于上一行更加简洁
    }

}
