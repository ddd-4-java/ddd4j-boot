package io.ddd4j.data.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Ddd4j Data 自动配置入口。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-data / BaseDataConfig。
 * 实际 MyBatis Plus 拦截器 / 异常处理由 ddd4j-boot-extensions 下的 data-mybatis 子模块提供。
 */
@Configuration
@EnableConfigurationProperties(BaseDataProperties.class)
public class BaseDataConfig {

}
