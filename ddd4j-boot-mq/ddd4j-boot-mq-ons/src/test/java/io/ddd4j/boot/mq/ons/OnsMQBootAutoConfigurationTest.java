package io.ddd4j.boot.mq.ons;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.ons.OnsMQClient;
import io.ddd4j.mq.ons.OnsProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OnsMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 *
 * <p>装配契约上下文同样触发急切初始化：{@code OnsMQBootAutoConfiguration} 经
 * {@code @Import(Ddd4jMQRegistrarConfiguration)} 在上下文刷新期执行 {@code MQClient#init}
 * （producer 为必选初始化项，fail-fast）。失败日志显示断点在配置装配期——
 * {@code OnsProperties#sessionProperties} 对空 {@code accessKey} 调
 * {@code Properties.setProperty} 直接 NPE，尚未进入 SDK。
 *
 * <p>该 broker 为托管云服务、无本地镜像，故本测试不起容器，改为补齐「格式合法的假凭据 + 回环地址
 * endpoint」：非空 accessKey/secretKey 让 {@code sessionProperties} 通过；非空
 * {@code name-srv-addr} 让 SDK 构造器走本地解析分支、跳过 {@code fetchNameServerAddr}
 * 联网探测，producer 预热可在离线环境完整走完（心跳/路由刷新均为异步任务，失败只记日志）。
 * 真实连接语义由 ddd4j-boot-mq-rocketmq 等本地 broker 模块的容器测试覆盖，
 * {@code MQClient#init} 生产 fail-fast 契约保持不变。
 */
class OnsMQBootAutoConfigurationTest {

    /**
     * 离线假凭据：仅满足格式校验，任何值都不会被用于真实建连（endpoint 指向回环地址）。
     */
    private static final String FAKE_ACCESS_KEY = "FAKE_ACCESS_KEY_FOR_CONTRACT_TEST";
    private static final String FAKE_SECRET_KEY = "FAKE_SECRET_KEY_FOR_CONTRACT_TEST";
    private static final String FAKE_NAME_SRV_ADDR = "127.0.0.1:9876";
    private static final String FAKE_PRODUCER_ID = "GID_FAKE_CONTRACT_TEST";

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            // ConfigurationPropertiesAutoConfiguration 提供 @ConfigurationProperties 绑定后处理，
            // 缺它则假凭据不会绑定进 OnsProperties（accessKey 为 null，sessionProperties 仍 NPE）
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class,
                    Ddd4jMQAutoConfiguration.class, OnsMQBootAutoConfiguration.class))
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=ons",
                    // 假凭据：补齐 sessionProperties 所需非空字段，并把 NameServer 指到回环地址，
                    // 让急切初始化（MQClient#init 必选 producer 预热）离线走完，不发起真实连接
                    "ddd4j.mq.ons.access-key=" + FAKE_ACCESS_KEY,
                    "ddd4j.mq.ons.secret-key=" + FAKE_SECRET_KEY,
                    "ddd4j.mq.ons.name-srv-addr=" + FAKE_NAME_SRV_ADDR,
                    "ddd4j.mq.ons.producer-id=" + FAKE_PRODUCER_ID);

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        // 默认装配：属性绑定 + Bean 装配 + 上下文刷新期急切初始化全部成功
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(OnsMQClient.class);
            assertThat(context).hasSingleBean(OnsProperties.class);
            // 验证 relaxed binding：假凭据确实绑定进了 OnsProperties
            assertThat(context.getBean(OnsProperties.class).getAccessKey()).isEqualTo(FAKE_ACCESS_KEY);
            assertThat(context.getBean(OnsProperties.class).getNameSrvAddr()).isEqualTo(FAKE_NAME_SRV_ADDR);
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
            // 用户自定义 client 同样要经 registrar 急切初始化（必选 producer 预热），
            // 需填充与 runner 相同的离线假凭据，否则 sessionProperties 对空 accessKey 抛 NPE
            OnsProperties properties = new OnsProperties();
            properties.setAccessKey(FAKE_ACCESS_KEY);
            properties.setSecretKey(FAKE_SECRET_KEY);
            properties.setNameSrvAddr(FAKE_NAME_SRV_ADDR);
            properties.setProducerId(FAKE_PRODUCER_ID);
            return new OnsMQClient(properties);
        }
    }
}
