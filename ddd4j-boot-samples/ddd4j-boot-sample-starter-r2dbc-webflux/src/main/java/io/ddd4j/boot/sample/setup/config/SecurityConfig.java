package io.ddd4j.boot.sample.setup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * WebFlux 安全配置：启用响应式安全过滤链，放行 /actuator/** 监控端点。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * 构造安全配置对象。
     */
    public SecurityConfig() {
    }

    /**
     * 构建 WebFlux 安全过滤链：对所有交换请求校验权限，但 /actuator/** 允许匿名访问。
     *
     * @param http 服务器安全配置器
     * @return 安全过滤链
     * @throws Exception 构建过滤链过程中的异常
     */
    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange((exchange) -> {
            exchange.pathMatchers("/actuator/**").permitAll();
        });
        return http.build();
    }
}
