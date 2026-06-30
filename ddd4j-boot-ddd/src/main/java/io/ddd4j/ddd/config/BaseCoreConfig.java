package io.ddd4j.ddd.config;

import io.ddd4j.spring.config.SpringCoreConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * ddd4j Boot 基础自动配置。
 *
 * <p>作为 Spring Boot 自动装配壳，导入主仓 {@link SpringCoreConfig}，
 * 避免在 boot 层重复定义 SpringContext / SpringContextAwareContext Bean。
 */
@AutoConfiguration
@Import(SpringCoreConfig.class)
public class BaseCoreConfig {

}
