package io.ddd4j.boot.web.webflux;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.extension.context.SpringContext;
import org.springframework.extension.utils.SpringContextUtils;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.extension.context.NestedMessageSource;
import org.springframework.extension.context.SpringContextAwareContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringExtensionCompatibilityTest {
    private final SpringContext previousContext = SpringContextUtils.getContext();

    @AfterEach
    void restoreProviderContext() {
        SpringContextUtils.setContext(previousContext);
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner().withUserConfiguration(Config.class);

    @Test
    void contextAwareProviderUsesActualSpringContext() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            SpringContextAwareContext provider = context.getBean(SpringContextAwareContext.class);
            assertThat(provider.getApplicationContext()).isSameAs(context.getSourceApplicationContext());
            assertThat(provider.getInstance("string")).isEqualTo("registered-value");
            assertThat(provider.getInstance(String.class)).isEqualTo("registered-value");
        });
    }

    @Test
    void missingBeanPreservesSpringLookupError() {
        runner.run(context -> assertThatThrownBy(() -> context.getBean(SpringContextAwareContext.class).getInstance("missing"))
                .isInstanceOf(org.springframework.beans.factory.NoSuchBeanDefinitionException.class));
    }

    @Test
    void messageArgumentsAndLocaleAreDelegated() {
        StaticMessageSource delegate = new StaticMessageSource();
        delegate.addMessage("greeting", Locale.CHINA, "你好，{0}");
        NestedMessageSource source = new NestedMessageSource(delegate);
        assertThat(source.getMessage("greeting", new Object[]{"DDD4J"}, Locale.CHINA)).isEqualTo("你好，DDD4J");
    }

    @Test
    void defaultMessageUsesTheTargetSpringVersion() {
        NestedMessageSource source = new NestedMessageSource(new StaticMessageSource());
        assertThat(source.getMessage("missing", new Object[]{"DDD4J"}, "default {0}", Locale.US)).isEqualTo("default DDD4J");
    }

    @Test
    void resolvableAndAdditionalDelegateRemainCompatible() {
        StaticMessageSource delegate = new StaticMessageSource();
        delegate.addMessage("found", Locale.US, "resolved {0}");
        NestedMessageSource source = new NestedMessageSource();
        source.addMessageSource(delegate);
        assertThat(source.getMessage(new DefaultMessageSourceResolvable(new String[]{"found"}, new Object[]{"value"}), Locale.US)).isEqualTo("resolved value");
        assertThat(source.getDelegates()).containsExactly(delegate);
    }

    @Test
    void dataScopeStaticLookupFindsTheRegisteredProviderContext() {
        runner.run(context -> {
            assertThat(SpringContextUtils.getContext())
                    .isSameAs(context.getBean(SpringContextAwareContext.class));
            assertThat(SpringContextUtils.getContext()
                    .getApplicationContext().getBean(String.class)).isEqualTo("registered-value");
        });
    }

    @Test
    void controllerTokenizationKeepsItsObservableBehavior() {
        assertThat(org.springframework.extension.utils.StringUtils.tokenizeToStringArray(" first, second;third "))
                .containsExactly("first", "second", "third");
    }

    @Configuration(proxyBeanMethods = false)
    static class Config {
        @Bean
        SpringContextAwareContext provider() { return new SpringContextAwareContext(); }
        @Bean
        String string() { return "registered-value"; }
    }
}
