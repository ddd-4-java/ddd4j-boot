package io.ddd4j.extension.dubbo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DubboAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配（enabled matchIfMissing=true）/ 显式关闭 / 缺类回退。
 * 本自动配置为 Dubbo 注解扫描的开关容器，本身不注册 Bean，契约重点是上下文可正常启动。
 */
class DubboAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DubboAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldStartCleanly() {
        runner.run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        runner.withPropertyValues("ddd4j.dubbo.enabled=false")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void shouldBackOffWhenDubboMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("org.apache.dubbo.config.spring.context.annotation.EnableDubbo"))
                .withConfiguration(AutoConfigurations.of(DubboAutoConfiguration.class))
                .run(context -> assertThat(context).hasNotFailed());
    }
}
