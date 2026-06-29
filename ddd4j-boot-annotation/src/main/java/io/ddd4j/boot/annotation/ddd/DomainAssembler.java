package io.ddd4j.boot.annotation.ddd;

import io.ddd4j.annotation.ddd.DDDAnnotation;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * Spring 领域装配器
 * 
 * <p>用于在领域对象与 DTO 之间进行数据装配。
 * 
 * <p><b>核心目标</b>：业务代码只写一个 @DomainAssembler，自动注册为 Spring Bean。
 */
@DDDAnnotation
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface DomainAssembler {

    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";
}
