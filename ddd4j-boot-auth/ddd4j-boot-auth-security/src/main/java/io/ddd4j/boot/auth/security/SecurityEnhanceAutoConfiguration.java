package io.ddd4j.boot.auth.security;

import io.ddd4j.auth.security.handler.SecurityExceptionHandler;
import io.ddd4j.auth.security.subject.SecuritySubjectProvider;
import io.ddd4j.auth.spring.AuthSpringConfiguration;
import io.ddd4j.core.subject.SubjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security Spring Boot 深度整合自动装配。
 *
 * <p>提供：
 * <ul>
 *   <li>{@code PasswordEncoder}（BCrypt，Spring Security 标准）</li>
 *   <li>{@code SubjectProvider}（SecuritySubjectProvider，确保 Spring Security 适配生效）</li>
 * </ul>
 *
 * <p>Spring Security 的异常处理由本 Boot 自动配置按 Servlet Web 环境显式注册。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.springframework.security.core.context.SecurityContextHolder")
@Import(AuthSpringConfiguration.class)
public class SecurityEnhanceAutoConfiguration {

    /**
     * 密码加密工具（BCrypt，Spring Security 标准）。
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(6);
    }

    /**
     * Spring Security SubjectProvider（覆盖默认装配）。
     */
    @Bean
    @ConditionalOnMissingBean(SubjectProvider.class)
    public SubjectProvider securitySubjectProvider() {
        return new SecuritySubjectProvider();
    }

    /**
     * Spring Security exception handler for servlet applications.
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(SecurityExceptionHandler.class)
    public SecurityExceptionHandler securityExceptionHandler() {
        return new SecurityExceptionHandler();
    }

}
