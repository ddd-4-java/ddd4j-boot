package io.ddd4j.boot.tx.local;

import io.ddd4j.core.constant.SpiKeys;
import io.ddd4j.core.context.Contexts;
import io.ddd4j.tx.TransactionPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 本地事务自动装配。
 *
 * <p>当 Spring 容器中存在 {@link PlatformTransactionManager} 时自动激活，
 * 注册 {@link SpringTransactionPort} 为 {@link TransactionPort} 实现。
 *
 * @author hiwepy
 * @since 3.4.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(PlatformTransactionManager.class)
public class LocalTransactionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionPort.class)
    public TransactionPort springTransactionPort(PlatformTransactionManager transactionManager) {
        SpringTransactionPort port = new SpringTransactionPort(transactionManager);
        // 注册到 SPI，使业务方可以通过 Contexts 获取
        Contexts.register(SpiKeys.TRANSACTION_PORT, TransactionPort.class, port);
        return port;
    }
}
