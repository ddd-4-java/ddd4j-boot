package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * Spring 领域仓储 Bean
 *
 * <p><b>核心目标</b>：业务代码只写一个 @DomainRepository，同时获得：
 * <ul>
 *   <li>DDD 语义（被 ArchUnit 规则识别）</li>
 *   <li>Spring 自动注册为 Bean（@Repository 元注解）</li>
 *   <li>Spring 自动数据访问异常转换（@Repository 内置）</li>
 * </ul>
 *
 * <p>业务代码使用方式：
 * <pre>
 * &#64;DomainRepository   // ← 只需写一个注解！
 * public interface UserRepository {
 *     // 自动被 Spring 注册为 Bean
 *     // 自动启用 Spring DAO 异常转换
 * }
 * </pre>
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repository                                     // ★ 关键：直接用 Spring 原生元注解
@Inherited
public @interface DomainRepository {

    @AliasFor(annotation = Repository.class, attribute = "value")
    String value() default "";
}
