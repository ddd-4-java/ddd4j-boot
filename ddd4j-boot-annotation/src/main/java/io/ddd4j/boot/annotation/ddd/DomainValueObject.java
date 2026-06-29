package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Spring 领域值对象
 *
 * <p><b>核心目标</b>：业务代码只写一个 @DomainValueObject，自动注册为 Spring Bean。
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface DomainValueObject {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
