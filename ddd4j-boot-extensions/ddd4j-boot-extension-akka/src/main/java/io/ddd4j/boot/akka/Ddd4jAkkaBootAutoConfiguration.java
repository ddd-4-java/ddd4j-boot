package io.ddd4j.boot.akka;

import io.ddd4j.extension.akka.AkkaAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot Akka 自动配置。
 *
 * <p>通过 {@link Import} 导入库侧 {@link AkkaAutoConfiguration}，
 * 提供 Akka ActorSystem 的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(AkkaAutoConfiguration.class)
@Import(AkkaAutoConfiguration.class)
public class Ddd4jAkkaBootAutoConfiguration {

}
