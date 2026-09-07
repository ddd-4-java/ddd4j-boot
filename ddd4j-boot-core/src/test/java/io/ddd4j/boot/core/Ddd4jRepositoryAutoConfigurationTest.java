package io.ddd4j.boot.core;

import io.ddd4j.core.cqrs.command.CommandBus;
import io.ddd4j.core.cqrs.command.DefaultCommandBus;
import io.ddd4j.core.ddd.model.AggregateRoot;
import io.ddd4j.core.ddd.repository.Repository;
import io.ddd4j.core.ddd.repository.RepositoryRegistry;
import io.ddd4j.core.exception.BizRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Ddd4jRepositoryAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 缺类回退 / Repository 自动注册 / CommandBus 用户覆盖 / 关闭对称清理。
 */
class Ddd4jRepositoryAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jRepositoryAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideCommandBusAndRegistrar() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(CommandBus.class);
            assertThat(context).hasSingleBean(Ddd4jRepositoryAutoConfiguration.Ddd4jRepositoryRegistrar.class);
        });
    }

    @Test
    void shouldBackOffWhenRepositoryRegistryClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(RepositoryRegistry.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jRepositoryAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(CommandBus.class);
                });
    }

    @Test
    void repositoryBeanShouldBeRegisteredIntoRegistry() {
        runner.withUserConfiguration(TestRepositoryConfiguration.class)
                .run(context -> assertThat(RepositoryRegistry.repository(TestOrderAggregate.class))
                        .isInstanceOf(TestOrderRepository.class));
    }

    @Test
    void customCommandBusShouldTakePrecedence() {
        runner.withUserConfiguration(CustomCommandBusConfiguration.class)
                .run(context -> assertThat(context.getBean(CommandBus.class))
                        .isSameAs(context.getBean("customCommandBus")));
    }

    @Test
    void closedContextShouldUnregisterRepositories() {
        ApplicationContextRunner cycleRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jRepositoryAutoConfiguration.class))
                .withUserConfiguration(TestRepositoryConfiguration.class);
        cycleRunner.run(context ->
                assertThat(RepositoryRegistry.repository(TestOrderAggregate.class))
                        .isInstanceOf(TestOrderRepository.class));
        // context 已关闭：BaseContext 与静态注册表应被对称移除
        assertThatThrownBy(() -> RepositoryRegistry.repository(TestOrderAggregate.class))
                .isInstanceOf(BizRuntimeException.class);
        // 重建上下文应能重新注册，不残留上一次的实例
        cycleRunner.run(context ->
                assertThat(RepositoryRegistry.repository(TestOrderAggregate.class))
                        .isInstanceOf(TestOrderRepository.class));
    }

    static class TestOrderAggregate extends AggregateRoot<Long> {

        private final Long id;

        TestOrderAggregate(Long id) {
            this.id = id;
        }

        @Override
        public Long id() {
            return id;
        }
    }

    static class TestOrderRepository implements Repository<TestOrderAggregate, Long> {

        @Override
        public Optional<TestOrderAggregate> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public TestOrderAggregate save(TestOrderAggregate aggregate) {
            return aggregate;
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class TestRepositoryConfiguration {

        @Bean
        TestOrderRepository testOrderRepository() {
            return new TestOrderRepository();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomCommandBusConfiguration {

        @Bean
        CommandBus customCommandBus() {
            return new DefaultCommandBus(Collections.emptyList());
        }
    }
}
