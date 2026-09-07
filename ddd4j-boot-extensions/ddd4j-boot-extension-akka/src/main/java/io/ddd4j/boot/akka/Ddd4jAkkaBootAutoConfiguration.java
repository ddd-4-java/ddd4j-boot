package io.ddd4j.boot.akka;

import akka.actor.ActorSystem;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ddd4j-boot Akka 自动配置。
 *
 * <p>自包含实现：不再依赖 {@code ddd4j-extension-akka}，直接在本模块内通过 {@code @Bean}
 * 装配 {@link ActorSystem}（由 {@link AkkaAutoConfiguration} 工厂创建）。
 *
 * <p>Actor 实例的查找由 {@link io.ddd4j.boot.akka.actor.SpringExtension} /
 * {@link io.ddd4j.boot.akka.actor.SpringActorProducer} 通过
 * {@link io.ddd4j.core.context.Contexts} 完成。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ActorSystem.class)
@ConditionalOnProperty(prefix = "ddd4j.akka", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
public class Ddd4jAkkaBootAutoConfiguration {

    /**
     * Akka 配置属性。
     *
     * @return AkkaProperties
     */
    @Bean
    @ConditionalOnMissingBean(AkkaProperties.class)
    @ConfigurationProperties(prefix = "ddd4j.akka")
    public AkkaProperties akkaProperties() {
        return new AkkaProperties();
    }

    /**
     * Akka 工厂（纯 Java，提供 ActorSystem 创建逻辑）。
     *
     * @return AkkaAutoConfiguration
     */
    @Bean
    @ConditionalOnMissingBean(AkkaAutoConfiguration.class)
    public AkkaAutoConfiguration akkaAutoConfiguration() {
        return new AkkaAutoConfiguration();
    }

    /**
     * 装配 ActorSystem。
     *
     * @param akkaAutoConfiguration Akka 工厂
     * @param properties           Akka 配置
     * @return ActorSystem 实例
     */
    @Bean
    @ConditionalOnMissingBean(ActorSystem.class)
    public ActorSystem actorSystem(AkkaAutoConfiguration akkaAutoConfiguration, AkkaProperties properties) {
        return akkaAutoConfiguration.actorSystem(properties);
    }

}
