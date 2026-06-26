package io.ddd4j.ddd.config;

import org.fuin.esc.mem.InMemoryEventStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * Ddd4j DDD 自动配置入口。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-ddd / DddAutoConfiguration。
 * 提供：
 * <ul>
 *     <li>EventStore Bean：默认 {@link InMemoryEventStore}，可通过 ddd4j.ddd.event-store-type 切换</li>
 *     <li>领域层零 MyBatis Plus 依赖（pom 中显式排除）</li>
 * </ul>
 *
 * <p>业务系统可提供自己的 EventStore Bean 来覆盖默认实现（如 KurrentDB）。
 */
@Configuration
@EnableConfigurationProperties(DddProperties.class)
public class DddAutoConfiguration {

    /**
     * 默认内存事件存储（dev/test）。
     * 生产环境应替换为持久化实现（如 KurrentDB）。
     */
    @Bean
    @ConditionalOnMissingBean(name = "escEventStore")
    @ConditionalOnProperty(prefix = "ddd4j.ddd", name = "event-store-type", havingValue = "memory", matchIfMissing = true)
    public InMemoryEventStore inMemoryEventStore() {
        return new InMemoryEventStore(Executors.newSingleThreadExecutor());
    }
}
