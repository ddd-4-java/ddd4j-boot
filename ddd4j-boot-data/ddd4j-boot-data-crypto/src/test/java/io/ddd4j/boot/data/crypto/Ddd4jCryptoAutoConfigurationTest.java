package io.ddd4j.boot.data.crypto;

import io.ddd4j.data.crypto.provider.DefaultCryptoProvider;
import io.ddd4j.data.crypto.strategy.DefaultCryptoStrategy;
import io.ddd4j.data.crypto.strategy.NoOpCryptoStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jCryptoAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配（enabled matchIfMissing=true）/ 显式关闭。
 */
class Ddd4jCryptoAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jCryptoAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideCryptoBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(DefaultCryptoProvider.class);
            assertThat(context).hasSingleBean(NoOpCryptoStrategy.class);
            assertThat(context).hasSingleBean(DefaultCryptoStrategy.class);
        });
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        // 上游 CryptoProperties.PREFIX = "crypto"（历史命名，未带 ddd4j 命名空间）
        runner.withPropertyValues("crypto.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(DefaultCryptoProvider.class));
    }
}
