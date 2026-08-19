package io.ddd4j.boot.tx.seata;

import io.ddd4j.core.constant.SpiKeys;
import io.ddd4j.core.context.Contexts;
import io.ddd4j.tx.TransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seata 分布式事务自动装配。
 *
 * <p>当 classpath 存在 {@code io.seata.spring.annotation.GlobalTransactionScanner} 时自动激活，
 * 注册 {@link SeataTransactionManager} 为 {@link TransactionManager} 实现，并通过 SPI 注册到
 * {@link Contexts}，使业务方可以通过 {@code Contexts.getOrThrow(SpiKeys.TRANSACTION_MANAGER)} 获取。
 *
 * @author hiwepy
 * @since 3.4.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "io.seata.spring.annotation.GlobalTransactionScanner")
public class SeataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionManager.class)
    public TransactionManager seataTransactionManager() {
        SeataTransactionManager manager = new SeataTransactionManager();
        // 注册到 SPI，使业务方可以通过 Contexts 获取
        Contexts.register(SpiKeys.TRANSACTION_MANAGER, TransactionManager.class, manager);
        return manager;
    }
}
