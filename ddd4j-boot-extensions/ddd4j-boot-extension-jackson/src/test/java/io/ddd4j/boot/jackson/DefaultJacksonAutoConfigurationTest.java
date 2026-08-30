package io.ddd4j.boot.jackson;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DefaultJacksonAutoConfiguration} 契约测试（Jackson 3.x 重写版）。
 *
 * <p>原版本基于 {@code Jackson2ObjectMapperBuilder}（Spring Boot 3.x 提供的 Jackson 2.x
 * 集成点）。本测试针对 Jackson 3.x 重写后版本：直接构造 {@link JsonMapper}，绕开
 * Spring Boot 的 Jackson 自动装配层（Spring Boot 3.5.x 仍仅支持 Jackson 2.x builder 系列）。
 *
 * <p>覆盖：默认装配（{@code @Primary JsonMapper} Bean）/ ApplicationContext 启动成功。
 */
class DefaultJacksonAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DefaultJacksonAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvidePrimaryJsonMapper() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(JsonMapper.class);
            JsonMapper mapper = context.getBean(JsonMapper.class);
            assertThat(mapper).isNotNull();
        });
    }
}
