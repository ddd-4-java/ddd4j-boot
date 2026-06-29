package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * Spring 领域实体（充血模型）
 * 
 * <p><b>核心目标</b>：业务代码只写一个 @DomainEntity，自动注册为 Spring Bean。
 * 
 * <p>业务代码使用方式：
 * <pre>
 * &#64;DomainEntity(aggregateRoot = true)
 * public class User { ... }
 * </pre>
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component                                       // ★ 关键：自动注册为 Bean
@Inherited
public @interface DomainEntity {

    /**
     * 是否是聚合根
     */
    boolean aggregateRoot() default false;

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
