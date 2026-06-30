package io.ddd4j.boot.auth.satoken;

import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import io.ddd4j.auth.satoken.handler.SaMixCheckLoginHandler;
import io.ddd4j.auth.satoken.subject.SaTokenSubjectProvider;
import io.ddd4j.auth.spring.AuthSpringConfiguration;
import io.ddd4j.auth.spring.satoken.SaTokenExceptionHandler;
import io.ddd4j.core.subject.SubjectProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Sa-Token Spring Boot 深度整合自动装配（从 ddd4j-auth-satoken 迁入）。
 *
 * <p>提供：
 * <ul>
 *   <li>注解合并能力（重写 Sa-Token 的 getAnnotation 为 AnnotatedElementUtils.getMergedAnnotation）</li>
 *   <li>{@code SaMixCheckLoginHandler} 注册（多账号混合登录注解处理器）</li>
 *   <li>{@code SubjectProvider} 注册（SubjectKit 全局注册中心写入）</li>
 * </ul>
 *
 * <p>本类依赖 Spring（@AutoConfiguration / @Bean / InitializingBean），
 * 因此从 ddd4j-auth-satoken（纯 Java）迁出到 ddd4j-boot-auth-satoken（Spring Boot 整合层）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(name = "cn.dev33.satoken.stp.StpUtil")
@Import(AuthSpringConfiguration.class)
public class SaTokenEnhanceAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // 重写 Sa-Token 的注解处理器，增加注解合并功能
        SaAnnotationStrategy.instance.getAnnotation = AnnotatedElementUtils::getMergedAnnotation;
    }

    /**
     * 多账号混合登录注解处理器。
     */
    @Bean
    @ConditionalOnMissingBean
    public SaMixCheckLoginHandler saMixCheckLoginHandler() {
        return new SaMixCheckLoginHandler();
    }

    /**
     * Sa-Token SubjectProvider（覆盖 ddd4j-auth-spring 的默认装配，确保 sa-token 优先）。
     */
    @Bean
    @ConditionalOnMissingBean(SubjectProvider.class)
    public SubjectProvider saTokenSubjectProvider() {
        return new SaTokenSubjectProvider();
    }

    /**
     * Sa-Token exception handler for servlet applications.
     */
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(SaTokenExceptionHandler.class)
    public SaTokenExceptionHandler saTokenExceptionHandler() {
        return new SaTokenExceptionHandler();
    }

}
