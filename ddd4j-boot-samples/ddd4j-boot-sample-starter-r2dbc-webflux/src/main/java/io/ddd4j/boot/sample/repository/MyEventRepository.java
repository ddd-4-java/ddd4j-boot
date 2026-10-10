/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.repository;

import io.ddd4j.boot.sample.entity.MyEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

/**
 * 事件响应式 MongoDB 仓储接口：继承 {@link ReactiveMongoRepository}，提供尾游标流式查询事件能力。
 *
 * <p>参考：<a href="https://blog.51cto.com/liukang/2090198">Tailable Cursor 说明</a></p>
 *
 * @author ddd4j
 * @since 1.0.0
 */
public interface MyEventRepository extends ReactiveMongoRepository<MyEvent, Long> { // 1

    /**
     * 以尾游标方式持续查询事件流，可订阅到新写入的事件。
     *
     * @return 事件响应式流，持续发射
     */
    @Tailable
        // 1
    Flux<MyEvent> findBy(); // 2

}
