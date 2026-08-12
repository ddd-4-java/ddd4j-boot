package io.ddd4j.boot.mq.core.config;

import io.ddd4j.mq.event.MQEventSerialization;
import io.ddd4j.mq.serialization.JsonMQEventSerialization;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jMQAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认关闭（enabled 无 matchIfMissing）/ 显式开启 / 显式关闭 / 缺类回退 / 用户 Bean 覆盖。
 */
class Ddd4jMQAutoConfigurationTest {

    @Test
    void shouldStayOffByDefaultWithoutEnabledFlag() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(MQEventSerialization.class);
                });
    }

    @Test
    void shouldAssembleWhenEnabled() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.enabled=true")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(MQEventSerialization.class);
                    assertThat(context.getBean(MQEventSerialization.class))
                            .isInstanceOf(JsonMQEventSerialization.class);
                });
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(MQEventSerialization.class));
    }

    @Test
    void shouldBackOffWhenSerializationClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(MQEventSerialization.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.enabled=true")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(MQEventSerialization.class);
                });
    }

    @Test
    void customSerializationShouldTakePrecedence() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.enabled=true")
                .withUserConfiguration(CustomSerializationConfiguration.class)
                .run(context -> assertThat(context.getBean(MQEventSerialization.class))
                        .isSameAs(context.getBean("customSerialization")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomSerializationConfiguration {

        @Bean
        MQEventSerialization customSerialization() {
            return new JsonMQEventSerialization();
        }
    }
}
