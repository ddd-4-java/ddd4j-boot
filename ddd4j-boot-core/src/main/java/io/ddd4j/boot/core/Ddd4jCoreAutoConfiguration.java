package io.ddd4j.boot.core;

import io.ddd4j.core.constant.SpiKeys;
import io.ddd4j.core.context.BaseContext;
import io.ddd4j.core.context.Contexts;
import io.ddd4j.core.ddd.event.DomainEventPublisher;
import io.ddd4j.core.i18n.I18nProvider;
import io.ddd4j.core.subject.SubjectDataProvider;
import io.ddd4j.core.subject.SubjectProvider;
import io.ddd4j.core.util.SubjectKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.ddd4j.spring.config.SpringCoreConfig;
import io.ddd4j.spring.context.SpringContext;

import java.util.Objects;

/**
 * ddd4j 核心 SPI 生命周期自动配置。
 *
 * <p>将 Spring 容器中的 SPI Bean 注入到 ddd4j 上下文，替代旧的 {@code @Component} 方式。
 * 使用 {@link ObjectProvider} 避免业务方未提供某 SPI 时启动失败。
 *
 * <p>导入上游 {@link SpringCoreConfig} 获取基础 Bean，本类负责 SPI 注册。
 *
 * @author wandl
 * @since 3.4.x
 */
@AutoConfiguration
@ConditionalOnClass({Contexts.class, SpringContext.class})
@Import(SpringCoreConfig.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class Ddd4jCoreAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(Ddd4jCoreAutoConfiguration.class);

    /**
     * SPI 注册桥接器：在上下文刷新时将 Spring Bean 注册到 ddd4j BaseContext。
     */
    @Bean
    @ConditionalOnMissingBean
    public Ddd4jSpiRegistrationBridge ddd4jSpiRegistrationBridge(
            ObjectProvider<DomainEventPublisher> domainEventPublisherProvider,
            ObjectProvider<SubjectProvider> subjectProviderProvider,
            ObjectProvider<SubjectDataProvider> subjectDataProviderProvider,
            ObjectProvider<I18nProvider> i18nProviderProvider) {
        return new Ddd4jSpiRegistrationBridge(
                domainEventPublisherProvider,
                subjectProviderProvider,
                subjectDataProviderProvider,
                i18nProviderProvider);
    }

    /**
     * SPI 注册桥接器：监听上下文刷新事件，将 Spring Bean 注册到 ddd4j BaseContext。
     */
    @Order(Ordered.HIGHEST_PRECEDENCE + 100)
    public static class Ddd4jSpiRegistrationBridge implements org.springframework.context.ApplicationListener<org.springframework.context.event.ContextRefreshedEvent> {

        private final ObjectProvider<DomainEventPublisher> domainEventPublisherProvider;
        private final ObjectProvider<SubjectProvider> subjectProviderProvider;
        private final ObjectProvider<SubjectDataProvider> subjectDataProviderProvider;
        private final ObjectProvider<I18nProvider> i18nProviderProvider;

        public Ddd4jSpiRegistrationBridge(
                ObjectProvider<DomainEventPublisher> domainEventPublisherProvider,
                ObjectProvider<SubjectProvider> subjectProviderProvider,
                ObjectProvider<SubjectDataProvider> subjectDataProviderProvider,
                ObjectProvider<I18nProvider> i18nProviderProvider) {
            this.domainEventPublisherProvider = domainEventPublisherProvider;
            this.subjectProviderProvider = subjectProviderProvider;
            this.subjectDataProviderProvider = subjectDataProviderProvider;
            this.i18nProviderProvider = i18nProviderProvider;
        }

        @Override
        public void onApplicationEvent(org.springframework.context.event.ContextRefreshedEvent event) {
            // DomainEventPublisher（必需）
            DomainEventPublisher domainPublisher = domainEventPublisherProvider.getIfAvailable();
            if (Objects.nonNull(domainPublisher)) {
                BaseContext.inject(SpiKeys.DOMAIN_EVENT_PUBLISHER, DomainEventPublisher.class, domainPublisher);
                log.info("ddd4j SPI: DomainEventPublisher registered");
            } else {
                log.warn("ddd4j SPI: No DomainEventPublisher bean found. DomainEvent.publish() will fail.");
            }

            // SubjectProvider（可选）
            SubjectProvider subjectProvider = subjectProviderProvider.getIfAvailable();
            if (Objects.nonNull(subjectProvider)) {
                BaseContext.inject(SpiKeys.SUBJECT_PROVIDER, SubjectProvider.class, subjectProvider);
                SubjectKit.register(subjectProvider);
                log.info("ddd4j SPI: SubjectProvider registered");
            }

            // SubjectDataProvider（可选）
            SubjectDataProvider subjectDataProvider = subjectDataProviderProvider.getIfAvailable();
            if (Objects.nonNull(subjectDataProvider)) {
                BaseContext.inject(SpiKeys.SUBJECT_DATA_PROVIDER, SubjectDataProvider.class, subjectDataProvider);
                SubjectKit.setDataProvider(subjectDataProvider);
                log.info("ddd4j SPI: SubjectDataProvider registered");
            }

            // I18nProvider（可选）
            I18nProvider i18nProvider = i18nProviderProvider.getIfAvailable();
            if (Objects.nonNull(i18nProvider)) {
                BaseContext.inject(SpiKeys.I18N_PROVIDER, I18nProvider.class, i18nProvider);
                log.info("ddd4j SPI: I18nProvider registered");
            }
        }
    }
}
