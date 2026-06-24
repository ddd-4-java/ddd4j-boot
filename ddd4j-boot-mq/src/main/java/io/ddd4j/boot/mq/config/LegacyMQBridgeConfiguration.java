package io.ddd4j.boot.mq.config;

import io.ddd4j.boot.mq.core.MQClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Legacy {@link MQClient} 启动桥接，兼容旧版 base-mq 自研客户端路径。
 *
 * @deprecated 请使用 {@link Ddd4jMQAutoConfiguration} + {@code ddd4j-boot-cmpt-*}。
 */
@Deprecated
@Slf4j
@Order(PriorityOrdered.HIGHEST_PRECEDENCE + 10)
@Configuration
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "legacy-enabled", havingValue = "true")
public class LegacyMQBridgeConfiguration {

    @Autowired(required = false)
    private List<MQClient> mqClients;

    /**
     * 初始化 legacy MQClient 实现。
     *
     * @param event 应用启动事件
     */
    @EventListener
    public void initLegacyMQClient(ApplicationStartedEvent event) {
        try {
            if (mqClients != null && !mqClients.isEmpty()) {
                for (MQClient mqClient : mqClients) {
                    mqClient.init();
                    mqClient.start();
                }
                log.warn("Legacy MQClient path is enabled; migrate to ddd4j-boot-cmpt-* adapters.");
            }
        } catch (Exception e) {
            log.error("初始化 Legacy MQ 失败", e);
        }
    }
}
