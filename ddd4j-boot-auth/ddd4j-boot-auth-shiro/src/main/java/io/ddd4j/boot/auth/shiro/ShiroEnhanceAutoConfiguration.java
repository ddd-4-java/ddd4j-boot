package io.ddd4j.boot.auth.shiro;

import io.ddd4j.auth.shiro.subject.ShiroSubjectProvider;
import io.ddd4j.core.subject.SubjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Apache Shiro Spring Boot 深度整合自动装配（从 ddd4j-auth-shiro 迁入的 WebShiroBizConfiguration）。
 *
 * <p>提供：
 * <ul>
 *   <li>{@code SubjectProvider}（ShiroSubjectProvider，确保 Shiro 适配生效）</li>
 * </ul>
 *
 * <p>Shiro 的异常处理（ShiroExceptionHandler）已在 ddd4j-auth-spring 模块内提供，
 * 通过其 AuthSpringAutoConfiguration 自动装配。
 *
 * <p>原 {@code WebShiroBizConfiguration}（@Configuration）已从 ddd4j-auth-shiro 迁出，
 * 因为它依赖 Spring，违反"auth-shiro 纯 Java"约束。本类是其 Spring Boot 整合版。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.apache.shiro.SecurityUtils")
public class ShiroEnhanceAutoConfiguration {

    /**
     * Shiro SubjectProvider（覆盖默认装配）。
     */
    @Bean
    @ConditionalOnMissingBean(SubjectProvider.class)
    public SubjectProvider shiroSubjectProvider() {
        return new ShiroSubjectProvider();
    }

}
