package io.ddd4j.boot.akka;

import akka.actor.ActorSystem;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jAkkaBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配（enabled matchIfMissing=true）/ 显式关闭 / 缺类回退。
 */
class Ddd4jAkkaBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jAkkaBootAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldCreateActorSystem() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(ActorSystem.class);
        });
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        runner.withPropertyValues("ddd4j.akka.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ActorSystem.class));
    }

    @Test
    void shouldBackOffWhenActorSystemMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(ActorSystem.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jAkkaBootAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(ActorSystem.class);
                });
    }
}
