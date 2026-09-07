package io.ddd4j.boot.mq.kafka.config;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.kafka.KafkaMQClient;
import io.ddd4j.mq.kafka.KafkaMQProperties;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link KafkaMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class KafkaMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, KafkaMQBootAutoConfiguration.class))
            .withUserConfiguration(CustomClientConfiguration.class)
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=kafka")
;

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        KafkaMQBootAutoConfiguration configuration = new KafkaMQBootAutoConfiguration();
        KafkaMQProperties properties = configuration.kafkaMQProperties();

        assertThat(properties).isNotNull();
        assertThat(configuration.kafkaMQClient(properties)).isNotNull();
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(KafkaMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(KafkaMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(KafkaMQClient.class))
                .withConfiguration(AutoConfigurations.of(KafkaMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=kafka")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(KafkaMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.run(context -> assertThat(context.getBean(KafkaMQClient.class))
                        .isSameAs(context.getBean("customKafkaMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        KafkaMQClient customKafkaMQClient() {
            return new KafkaMQClient(
                    new MockProducer<String, String>(
                            true, null, new StringSerializer(), new StringSerializer()),
                    null);
        }
    }
}
