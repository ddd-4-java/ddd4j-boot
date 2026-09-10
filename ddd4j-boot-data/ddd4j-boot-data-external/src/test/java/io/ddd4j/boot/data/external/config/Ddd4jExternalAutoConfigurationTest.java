package io.ddd4j.boot.data.external.config;

import io.ddd4j.data.external.ExternalProperties;
import io.ddd4j.data.external.region.BaiduRegionTemplate;
import io.ddd4j.data.external.region.PconlineRegionTemplate;
import io.ddd4j.data.external.region.RegionCache;
import io.ddd4j.data.external.weather.WeatherTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jExternalAutoConfiguration} / {@link Ddd4jGlobalSequenceAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 用户 Bean 覆盖 / 全局序列号装配。
 */
class Ddd4jExternalAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    Ddd4jExternalAutoConfiguration.class, Ddd4jGlobalSequenceAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideExternalBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(ExternalProperties.class);
            assertThat(context).hasSingleBean(BaiduRegionTemplate.class);
            assertThat(context).hasSingleBean(PconlineRegionTemplate.class);
            assertThat(context).hasSingleBean(WeatherTemplate.class);
            assertThat(context).hasBean("globalSequence");
        });
    }

    @Test
    void shouldUseNoneRegionCacheWhenStringRedisTemplateIsMissing() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(RegionCache.class);
            assertThat(context.getBean(RegionCache.class).getString("missing")).isNull();
        });
    }
}
