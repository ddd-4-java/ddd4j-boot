package io.ddd4j.boot.core;

import io.ddd4j.core.constant.SpiKeys;
import io.ddd4j.core.context.BaseContext;
import io.ddd4j.core.context.Contexts;
import io.ddd4j.core.ddd.event.DomainEventPublisher;
import io.ddd4j.spring.context.SpringContext;
import io.ddd4j.spring.context.SpringContextBridge;
import io.ddd4j.spring.event.SpringDomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jCoreAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 缺类回退 / SPI 注册 / 关闭对称移除 / 多上下文重建无残留。
 */
class Ddd4jCoreAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jCoreAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldExposeCoreRuntimeBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(SpringContext.class);
            assertThat(context).hasSingleBean(SpringContextBridge.class);
            assertThat(context).hasSingleBean(SpringDomainEventPublisher.class);
        });
    }

    @Test
    void refreshShouldRegisterDomainEventPublisherSpi() {
        runner.run(context ->
                assertThat(BaseContext.get(SpiKeys.DOMAIN_EVENT_PUBLISHER, DomainEventPublisher.class))
                        .isPresent());
    }

    @Test
    void shouldBackOffWhenCoreClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(SpringContext.class, Contexts.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jCoreAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean("springContextBridge");
                });
    }

    @Test
    void contextCloseShouldRemoveSpiState() {
        runner.run(context ->
                assertThat(BaseContext.get(SpiKeys.DOMAIN_EVENT_PUBLISHER, DomainEventPublisher.class))
                        .isPresent());
        // runner.run 返回前上下文已关闭，SPI 应被对称移除，无全局残留
        assertThat(BaseContext.get(SpiKeys.DOMAIN_EVENT_PUBLISHER, DomainEventPublisher.class))
                .isEmpty();
    }

    @Test
    void repeatedContextCyclesShouldNotLeakOrFail() {
        // 连续创建/关闭多个上下文：若关闭清理缺失，第二次 refresh 将抛异常
        runner.run(context -> assertThat(context).hasNotFailed());
        runner.run(context -> assertThat(context).hasNotFailed());
        runner.run(context -> assertThat(context).hasNotFailed());
        assertThat(BaseContext.get(SpiKeys.DOMAIN_EVENT_PUBLISHER, DomainEventPublisher.class))
                .isEmpty();
    }
}
