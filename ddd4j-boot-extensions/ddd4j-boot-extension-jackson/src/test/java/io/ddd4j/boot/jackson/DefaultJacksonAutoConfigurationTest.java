package io.ddd4j.boot.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DefaultJacksonAutoConfiguration} 契约测试。
 */
class DefaultJacksonAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DefaultJacksonAutoConfiguration.class,
                    org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvidePrimaryObjectMapper() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(ObjectMapper.class);
        });
    }

    @Test
    void shouldBackOffWhenJacksonBuilderMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(Jackson2ObjectMapperBuilder.class))
                .withConfiguration(AutoConfigurations.of(DefaultJacksonAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean("jacksonObjectMapper");
                });
    }
}
