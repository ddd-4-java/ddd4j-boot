package io.ddd4j.boot.mq.ons;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import com.aliyun.openservices.ons.api.Producer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.lifecycle.MQStartupState;
import io.ddd4j.mq.ons.OnsMQClient;
import io.ddd4j.mq.ons.OnsProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OnsMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class OnsMQBootAutoConfigurationTest {

    private static final AtomicInteger PRODUCER_STARTS = new AtomicInteger();

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, OnsMQBootAutoConfiguration.class))
            .withUserConfiguration(TestProducerConfiguration.class)
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=ons");

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        PRODUCER_STARTS.set(0);
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(OnsMQClient.class);
            assertThat(context).hasSingleBean(OnsProperties.class);
            assertThat(PRODUCER_STARTS.get()).isPositive();
            assertThat(context.getBean(OnsMQClient.class).startupStatus().snapshot().state())
                    .isEqualTo(MQStartupState.READY);
        });
    }

    @Test
    void missingCloudCredentialsShouldAbortRequiredProducerStartup() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        Ddd4jMQAutoConfiguration.class, OnsMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=ons")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("Initialize MQ client [ons] failed");
                });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(OnsMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(OnsMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(OnsMQClient.class))
                .withConfiguration(AutoConfigurations.of(OnsMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=ons")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(OnsMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(OnsMQClient.class))
                        .isSameAs(context.getBean("customOnsMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        OnsMQClient customOnsMQClient() {
            return new OnsMQClient(TestProducerConfiguration.producer(), new OnsProperties());
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class TestProducerConfiguration {

        @Bean
        static BeanPostProcessor onsProducerTestFixture() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessBeforeInitialization(Object bean, String beanName) {
                    if (bean instanceof OnsMQClient) {
                        try {
                            Field field = OnsMQClient.class.getDeclaredField("producer");
                            field.setAccessible(true);
                            field.set(bean, producer());
                        } catch (ReflectiveOperationException exception) {
                            throw new IllegalStateException("Cannot inject test ONS producer", exception);
                        }
                    }
                    return bean;
                }
            };
        }

        static Producer producer() {
            return (Producer) Proxy.newProxyInstance(Producer.class.getClassLoader(),
                    new Class<?>[] {Producer.class}, (proxy, method, args) -> {
                        if ("start".equals(method.getName())) {
                            PRODUCER_STARTS.incrementAndGet();
                            return null;
                        }
                        if ("isStarted".equals(method.getName())) {
                            return true;
                        }
                        if ("isClosed".equals(method.getName())) {
                            return false;
                        }
                        return null;
                    });
        }
    }
}
