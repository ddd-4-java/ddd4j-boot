package io.ddd4j.boot.auth.shiro;

import io.ddd4j.auth.shiro.subject.ShiroSubjectProvider;
import io.ddd4j.auth.spring.AuthSpringConfiguration;
import io.ddd4j.auth.spring.shiro.ShiroExceptionHandler;
import io.ddd4j.core.subject.SubjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Apache Shiro Spring Boot 深度整合自动装配（从 ddd4j-auth-shiro 迁入的 WebShiroBizConfiguration）。
 *
 * <p>提供：
 * <ul>
 *   <li>{@code SubjectProvider}（ShiroSubjectProvider，确保 Shiro 适配生效）</li>
 * </ul>
 *
 * <p>Shiro 的异常处理由本 Boot 自动配置按 Servlet Web 环境显式注册。
 *
 * <p>原 {@code WebShiroBizConfiguration}（@Configuration）已从 ddd4j-auth-shiro 迁出，
 * 因为它依赖 Spring，违反"auth-shiro 纯 Java"约束。本类是其 Spring Boot 整合版。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.apache.shiro.SecurityUtils")
@Import(AuthSpringConfiguration.class)
public class ShiroEnhanceAutoConfiguration {

    /**
     * Shiro SubjectProvider（覆盖默认装配）。
     */
    @Bean
    @ConditionalOnMissingBean(SubjectProvider.class)
    public SubjectProvider shiroSubjectProvider() {
        return new ShiroSubjectProvider();
    }

    /**
     * Shiro exception handler for servlet applications.
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(ShiroExceptionHandler.class)
    public ShiroExceptionHandler shiroExceptionHandler() {
        return new ShiroExceptionHandler();
    }

}
