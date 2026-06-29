package io.ddd4j.boot.auth.security;

import io.ddd4j.auth.security.subject.SecuritySubjectProvider;
import io.ddd4j.core.subject.SubjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
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
 * <p>Spring Security 的异常处理（SecurityExceptionHandler）已在 ddd4j-auth-security 模块内提供，
 * 通过其自身的 AutoConfiguration.imports 自动装配。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.security.core.context.SecurityContextHolder")
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

}
