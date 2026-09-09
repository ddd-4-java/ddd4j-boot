package io.ddd4j.boot.cache;

import io.ddd4j.boot.cache.CacheProperties;
import io.ddd4j.cache.CacheKit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jCacheAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 缺类回退。
 */
class Ddd4jCacheAutoConfigurationTest {

    @Test
    void contextShouldSupportCacheRegistrationReadAndEviction() {
        String cacheName = "boot-cache-contract";
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            CacheKit.build(cacheName, 60);
            CacheKit.put(cacheName, "key", "value");
            assertThat(CacheKit.<String>get(cacheName, "key")).isEqualTo("value");
            CacheKit.invalidate(cacheName, "key");
            assertThat((Object) CacheKit.get(cacheName, "key")).isNull();
            CacheKit.unregister(cacheName);
        });
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jCacheAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldBindCacheProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(CacheProperties.class);
        });
    }

    @Test
    void shouldBackOffWhenCacheKitMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("io.ddd4j.cache.CacheKit"))
                .withConfiguration(AutoConfigurations.of(Ddd4jCacheAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(CacheProperties.class);
                });
    }
}
