/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.boot.sample.entity.MyEvent;
import io.ddd4j.boot.sample.repository.MyEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 事件响应式 REST 控制器：以 NDJSON 流式方式批量写入与查询 MyEvent 事件。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@RestController
@RequestMapping("/events")
public class MyEventController {

    /**
     * 构造事件控制器。
     */
    public MyEventController() {
    }

    /**
     * 事件响应式仓储，提供流式插入与查询能力。
     */
    @Autowired
    private MyEventRepository myEventRepository;

    /**
     * 批量写入事件流：接收 NDJSON 请求体，逐条插入后完成信号收尾。
     *
     * @param events NDJSON 格式的事件响应式流
     * @return 插入完成信号，无正文输出
     */
    @PostMapping(path = "", consumes = MediaType.APPLICATION_NDJSON_VALUE) // 1
    public Mono<Void> loadEvents(@RequestBody Flux<MyEvent> events) {
        return this.myEventRepository.insert(events).then();    // 2
    }

    /**
     * 以 NDJSON 流式查询全部事件。
     *
     * @return 事件响应式流，逐条发射
     */
    @GetMapping(path = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MyEvent> getEvents() {
        return this.myEventRepository.findBy();
    }

}
