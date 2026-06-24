package io.ddd4j.boot.mq.config;

import io.ddd4j.boot.core.context.BaseContext;
import io.ddd4j.boot.core.contract.MQEvent;
import io.ddd4j.boot.mq.contract.MQDestination;
import io.ddd4j.boot.mq.publish.MQEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.function.Consumer;

/**
 * 将 {@link MQEventPublisher} 桥接到 {@link BaseContext}，兼容 {@link MQEvent#publish()} 旧调用方式。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "enabled", havingValue = "true")
@ConditionalOnBean(MQEventPublisher.class)
public class MQEventPublisherBridgeConfiguration {

    private final MQEventPublisher mqEventPublisher;
    private final Ddd4jMQProperties properties;

    /**
     * 注册 BaseContext 发布器，供 boot-core 中 MQEvent.publish() 使用。
     */
    @PostConstruct
    public void registerBaseContextPublisher() {
        Consumer<MQEvent> bridge = event -> {
            String namespace = event.getNamespace() != null ? event.getNamespace() : properties.getNamespace();
            MQDestination destination = MQDestination.of(event.getTopic(), event.getTag(), namespace);
            mqEventPublisher.publish(event, destination);
        };
        BaseContext.inject("MQEventPublisher", bridge);
        log.info("MQEventPublisher bridged to BaseContext for legacy MQEvent.publish() calls.");
    }
}
